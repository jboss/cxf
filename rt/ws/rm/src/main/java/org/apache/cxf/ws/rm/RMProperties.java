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

package org.apache.cxf.ws.rm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.cxf.ws.rm.v200702.AckRequestedType;
import org.apache.cxf.ws.rm.v200702.CloseSequenceType;
import org.apache.cxf.ws.rm.v200702.SequenceAcknowledgement;
import org.apache.cxf.ws.rm.v200702.SequenceType;

public class RMProperties {
    private SequenceType sequence;
    private Collection<SequenceAcknowledgement> acks;
    private Collection<AckRequestedType> acksRequested;
    private CloseSequenceType closeSequence;
    private String namespaceURI;
    
    public Collection<SequenceAcknowledgement> getAcks() {
        return acks;
    }
    
    public Collection<AckRequestedType> getAcksRequested() {
        return acksRequested;
    }
    
    public CloseSequenceType getCloseSequence() {
        return closeSequence;
    }
    
    public SequenceType getSequence() {
        return sequence;
    }
    
    public void setAcks(Collection<SequenceAcknowledgement> a) {
        // use threadsafe implementation for working copy, to avoid concurrent modifications
        acks = new CopyOnWriteArrayList<SequenceAcknowledgement>(a);
    }
    
    public void setAcksRequested(Collection<AckRequestedType> ar) {
        // use threadsafe implementation for working copy, to avoid concurrent modifications
        acksRequested = new CopyOnWriteArrayList<AckRequestedType>(ar);       
    }
    
    public void setCloseSequence(CloseSequenceType cs) {
        closeSequence = cs;
    }
    
    public void setSequence(SequenceType s) {
        sequence = s;
    }
    
    public void setSequence(SourceSequence seq) {
        SequenceType s = RMUtils.getWSRMFactory().createSequenceType();
        s.setIdentifier(seq.getIdentifier());
        s.setMessageNumber(seq.getCurrentMessageNr());   
        setSequence(s);
    }
    
    public void addAck(DestinationSequence seq) {
        if (null == acks) {
            acks = new ArrayList<SequenceAcknowledgement>();
        }
        SequenceAcknowledgement ack = seq.getAcknowledgment();
        acks.add(ack);
        seq.acknowledgmentSent();
    }
    
    /**
     * Get the WS-ReliableMessaging namespace to be used for encoding and decoding messages.
     * 
     * @return
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }
    
    /**
     * Set the WS-ReliableMessaging namespace to be used for encoding and decoding messages.
     * 
     * @return namespace URI
     */
    public void exposeAs(String uri) {
        namespaceURI = uri;
    }
}