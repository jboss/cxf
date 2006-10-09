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

package org.apache.cxf.tools.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.extensions.soap12.SOAP12Fault;
import javax.wsdl.extensions.soap12.SOAP12Header;
import javax.wsdl.extensions.soap12.SOAP12Operation;

import org.apache.cxf.tools.common.ExtensionInvocationHandler;
import org.apache.cxf.tools.common.extensions.soap.SoapAddress;
import org.apache.cxf.tools.common.extensions.soap.SoapBinding;
import org.apache.cxf.tools.common.extensions.soap.SoapBody;
import org.apache.cxf.tools.common.extensions.soap.SoapHeader;
import org.apache.cxf.tools.common.extensions.soap.SoapOperation;

public final class SOAPBindingUtil {
    private static Map<String, String> bindingMap = new HashMap<String, String>();

    static {
        bindingMap.put("RPC", "SOAPBinding.Style.RPC");
        bindingMap.put("DOCUMENT", "SOAPBinding.Style.DOCUMENT");
        bindingMap.put("LITERAL", "SOAPBinding.Use.LITERAL");
        bindingMap.put("ENCODED", "SOAPBinding.Use.ENCODED");
        bindingMap.put("BARE", "SOAPBinding.ParameterStyle.BARE");
        bindingMap.put("WRAPPED", "SOAPBinding.ParameterStyle.WRAPPED");
    }

    private SOAPBindingUtil() {
    }

    public static String getBindingAnnotation(String key) {
        return bindingMap.get(key.toUpperCase());
    }

    public static <T> T getProxy(Class<T> cls, Object obj) {
        InvocationHandler ih = new ExtensionInvocationHandler(obj);
        Object proxy = Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, ih);
        return cls.cast(proxy);
    }

    public static String getBindingStyle(Binding binding) {
        Iterator ite = binding.getExtensibilityElements().iterator();
        while (ite.hasNext()) {
            Object obj = ite.next();
            if (isSOAPBinding(obj)) {
                return getSoapBinding(obj).getStyle();
            }
        }
        return "";
    }

    public static SoapOperation getSoapOperation(List<ExtensibilityElement> exts) {
        if (exts != null) {
            for (ExtensibilityElement ext : exts) {
                if (isSOAPOperation(ext)) {
                    return getSoapOperation(ext);
                }
            }
        }
        return null;
    }
    
    public static SoapOperation getSoapOperation(Object obj) {
        if (isSOAPOperation(obj)) {
            return getProxy(SoapOperation.class, obj);
        }
        return null;
    }

    public static String getSOAPOperationStyle(BindingOperation bop) {
        String style = "";
        if (bop != null) {
            Iterator ite = bop.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (isSOAPOperation(obj)) {
                    style = getSoapOperation(obj).getStyle();
                    break;
                }
            }
        }
        return style;
    }

    public static SoapBody getBindingInputSOAPBody(BindingOperation bop) {
        BindingInput bindingInput = bop.getBindingInput();
        if (bindingInput != null) {
            Iterator ite = bindingInput.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (isSOAPBody(obj)) {
                    return getSoapBody(obj);
                }
            }
        }

        return null;
    }

    public static SoapBody getBindingOutputSOAPBody(BindingOperation bop) {
        BindingOutput bindingOutput = bop.getBindingOutput();
        if (bindingOutput != null) {
            Iterator ite = bindingOutput.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (isSOAPBody(obj)) {
                    return getSoapBody(obj);
                }
            }
        }

        return null;
    }

    public static SoapBody getSoapBody(List<ExtensibilityElement> exts) {
        for (ExtensibilityElement ext : exts) {
            if (isSOAPBody(ext)) {
                return getSoapBody(ext);
            }
        }
        return null;
    }

    public static SoapBody getSoapBody(Object obj) {
        if (isSOAPBody(obj)) {
            return getProxy(SoapBody.class, obj);
        }
        return null;
    }

    public static boolean isSOAPBody(Object obj) {
        return obj instanceof SOAPBody || obj instanceof SOAP12Body;
    }

    public static boolean isSOAPHeader(Object obj) {
        return obj instanceof SOAPHeader || obj instanceof SOAP12Header;
    }

    public static List<SoapHeader> getSoapHeaders(List<ExtensibilityElement> exts) {
        List<SoapHeader> headers = new ArrayList<SoapHeader>();
        for (ExtensibilityElement ext : exts) {
            if (isSOAPHeader(ext)) {
                headers.add(getSoapHeader(ext));
            }
        }
        return headers;
    }

    public static SoapHeader getSoapHeader(Object obj) {
        if (isSOAPHeader(obj)) {
            return getProxy(SoapHeader.class, obj);
        }
        return null;
    }

    public static SoapAddress getSoapAddress(Object obj) {
        if (isSOAPAddress(obj)) {
            return getProxy(SoapAddress.class, obj);
        }
        return null;
    }

    public static boolean isSOAPAddress(Object obj) {
        return obj instanceof SOAPAddress || obj instanceof SOAP12Address;
    }

    public static SoapHeader getBindingInputSOAPHeader(BindingOperation bop) {
        BindingInput bindingInput = bop.getBindingInput();
        if (bindingInput != null) {
            Iterator ite = bindingInput.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (isSOAPHeader(obj)) {
                    return getProxy(SoapHeader.class, obj);
                }
            }
        }

        return null;
    }

    public static SoapHeader getBindingOutputSOAPHeader(BindingOperation bop) {
        BindingOutput bindingOutput = bop.getBindingOutput();
        if (bindingOutput != null) {
            Iterator ite = bindingOutput.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (isSOAPHeader(obj)) {
                    return getProxy(SoapHeader.class, obj);
                }
            }
        }

        return null;
    }

    public static SoapBinding getSoapBinding(List<ExtensibilityElement> exts) {
        for (ExtensibilityElement ext : exts) {
            if (isSOAPBinding(ext)) {
                return getSoapBinding(ext);
            }
        }
        return null;
    }
    
    public static SoapBinding getSoapBinding(Object obj) {
        if (isSOAPBinding(obj)) {
            return getProxy(SoapBinding.class, obj);
        }
        return null;
    }

    public static boolean isSOAPBinding(Object obj) {
        return obj instanceof SOAPBinding || obj instanceof SOAP12Binding;
    }

    public static boolean isMixedStyle(Binding binding) {
        Iterator ite = binding.getExtensibilityElements().iterator();
        String bindingStyle = "";
        String previousOpStyle = "";
        String style = "";
        while (ite.hasNext()) {
            Object obj = ite.next();
            if (isSOAPBinding(obj)) {
                SoapBinding soapBinding = getSoapBinding(obj);
                bindingStyle = soapBinding.getStyle();
                if (bindingStyle == null) {
                    bindingStyle = "";
                }
            }
        }
        Iterator ite2 = binding.getBindingOperations().iterator();
        while (ite2.hasNext()) {
            BindingOperation bop = (BindingOperation)ite2.next();
            Iterator ite3 = bop.getExtensibilityElements().iterator();
            while (ite3.hasNext()) {
                Object obj = ite3.next();

                if (isSOAPOperation(obj)) {
                    SoapOperation soapOperation = getSoapOperation(obj);
                    style = soapOperation.getStyle();
                    if (style == null) {
                        style = "";
                    }

                    if ("".equals(bindingStyle) && "".equals(previousOpStyle) || "".equals(bindingStyle)
                        && previousOpStyle.equalsIgnoreCase(style)) {
                        previousOpStyle = style;

                    } else if (!"".equals(bindingStyle) && "".equals(previousOpStyle)
                               && bindingStyle.equalsIgnoreCase(style)
                               || bindingStyle.equalsIgnoreCase(previousOpStyle)
                               && bindingStyle.equalsIgnoreCase(style)) {
                        previousOpStyle = style;
                    } else if (!"".equals(bindingStyle) && "".equals(style) && "".equals(previousOpStyle)) {
                        continue;
                    } else {
                        return true;
                    }

                }

            }
        }

        return false;

    }

    public static String getCanonicalBindingStyle(Binding binding) {
        String bindingStyle = getBindingStyle(binding);
        if (bindingStyle != null && !("".equals(bindingStyle))) {
            return bindingStyle;
        }
        for (Iterator ite2 = binding.getBindingOperations().iterator(); ite2.hasNext();) {
            BindingOperation bindingOp = (BindingOperation)ite2.next();
            String bopStyle = getSOAPOperationStyle(bindingOp);
            if (!"".equals(bopStyle)) {
                return bopStyle;
            }
        }
        return "";

    }

    public static boolean isSOAPOperation(Object obj) {
        return obj instanceof SOAPOperation || obj instanceof SOAP12Operation;
    }

    public static boolean isSOAPFault(Object obj) {
        return obj instanceof SOAPFault || obj instanceof SOAP12Fault;
    }
}
