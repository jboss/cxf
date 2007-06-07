/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.transport.jbi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;


import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.staxutils.StaxUtils;


import org.apache.cxf.transport.AbstractConduit;
import org.apache.cxf.transport.AbstractDestination;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;

public class JBIDestination extends AbstractDestination {
    
    private static final Logger LOG = LogUtils.getL7dLogger(JBIDestination.class);
    
    private final DeliveryChannel channel;
    private JBIDispatcher dispatcher;
    private volatile boolean running;
    
    public JBIDestination(EndpointInfo info,
                          DeliveryChannel dc) {
        super(getTargetReference(info, null), info);
        this.channel = dc;
    }

    
    protected Logger getLogger() {
        return LOG;
    }
    
    /**
     * @param inMessage the incoming message
     * @return the inbuilt backchannel
     */
    protected Conduit getInbuiltBackChannel(Message inMessage) {
        return new BackChannelConduit(EndpointReferenceUtils.getAnonymousEndpointReference(),
                                      inMessage);
    }
    
    public void shutdown() {
        running = false;
    }

    public void deactivate() {
        running = false;
    }

    public void activate()  {
        getLogger().info(new org.apache.cxf.common.i18n.Message(
            "ACTIVE.JBI.SERVER.TRANSPORT", getLogger()).toString());
        dispatcher = new JBIDispatcher();
        new Thread(dispatcher).start();
    }

    
    // this should deal with the cxf message 
    protected class BackChannelConduit extends AbstractConduit {
        
        protected Message inMessage;
        protected JBIDestination jbiDestination;
                
        BackChannelConduit(EndpointReferenceType ref, Message message) {
            super(ref);
            inMessage = message;
        }
        
        /**
         * Register a message observer for incoming messages.
         * 
         * @param observer the observer to notify on receipt of incoming
         */
        public void setMessageObserver(MessageObserver observer) {
            // shouldn't be called for a back channel conduit
        }

        /**
         * Send an outbound message, assumed to contain all the name-value
         * mappings of the corresponding input message (if any). 
         * 
         * @param message the message to be sent.
         */
        public void prepare(Message message) throws IOException {
            // setup the message to be send back
            DeliveryChannel dc = channel;
            if (dc == null) {
                dc = JBITransportFactory.getDeliveryChannel();
            }
            message.put(MessageExchange.class, inMessage.get(MessageExchange.class));
            message.setContent(OutputStream.class,
                               new JBIDestinationOutputStream(inMessage, dc));
        }        

        protected Logger getLogger() {
            return LOG;
        }
    }
    
    
    private class JBIDispatcher implements Runnable {

        public final void run() {

            try {
                running = true;
                getLogger().info(new org.apache.cxf.common.i18n.Message(
                    "RECEIVE.THREAD.START", getLogger()).toString());
                do {
                    MessageExchange exchange = null;
                    synchronized (channel) {
                        exchange = channel.accept();
                    }

                    if (exchange != null) {
                        try {
                            getLogger().info(new org.apache.cxf.common.i18n.Message(
                                    "DISPATCH.TO.SU", getLogger()).toString());
                            dispatch(exchange);
                            
                        } finally {
                            //
                        }
                    }
                } while(running);
            } catch (Exception ex) {
                getLogger().log(Level.SEVERE, new org.apache.cxf.common.i18n.Message(
                    "ERROR.DISPATCH.THREAD", getLogger()).toString(), ex);
            }
            getLogger().fine(new org.apache.cxf.common.i18n.Message(
                                 "JBI.SERVER.TRANSPORT.MESSAGE.PROCESS.THREAD.EXIT", getLogger()).toString());
        }
    }
    
            

    public void dispatch(MessageExchange exchange) throws IOException {
        QName opName = exchange.getOperation(); 
        getLogger().fine("dispatch method: " + opName);
        
        NormalizedMessage nm = exchange.getMessage("in");
        try {
            //get the message to be interceptor
            MessageImpl inMessage = new MessageImpl();
            inMessage.put(MessageExchange.class, exchange);
            //get the message to be interceptor
            
            XMLStreamReader reader = StaxUtils.createXMLStreamReader(nm.getContent());
            inMessage.setContent(XMLStreamReader.class, reader);
            
                                           
            //dispatch to correct destination in case of multiple endpoint
            inMessage.setDestination(this);
            getMessageObserver().onMessage(inMessage);
            
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, new org.apache.cxf.common.i18n.Message(
                "ERROR.PREPARE.MESSAGE", getLogger()).toString(), ex);
            throw new IOException(ex.getMessage());
        }

    }
}
