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

package org.apache.cxf.tools.java2wsdl.processor.internal.jaxws;

import java.io.File;

import org.apache.cxf.BusFactory;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.tools.common.ProcessorTestBase;
import org.apache.cxf.tools.java2wsdl.generator.wsdl11.WSDL11Generator;

public class JaxwsServiceBuilderNoAnnoTest extends ProcessorTestBase {
    JaxwsServiceBuilder builder = new JaxwsServiceBuilder();
    WSDL11Generator generator = new WSDL11Generator();
    
    public void setUp() throws Exception {
        super.setUp();
        builder.setBus(BusFactory.getDefaultBus());
    }

    public void tearDown() {
    }

    // Revisit:
    // * Missing wsdl:types
    // * getPrice MUST refeter to schema element
    //     <wsdl:message name="getPrice">
    //        <wsdl:part name="arg0" type="xsd:string"/>
    //     </wsdl:message>
    // CXF-521
    public void testGeneratedWithElementryClass() throws Exception {
        builder.setServiceClass(org.apache.cxf.tools.fortest.classnoanno.docbare.Stock.class);
        ServiceInfo service =  builder.build();
        generator.setServiceModel(service);
        File output = getOutputFile("stock_noanno_bare.wsdl");
        generator.generate(output);
        assertTrue(output.exists());
    }

    // Passed
    public void testGeneratedWithDocWrappedClass() throws Exception {
        builder.setServiceClass(org.apache.cxf.tools.fortest.classnoanno.docwrapped.Stock.class);
        ServiceInfo service =  builder.build();
        generator.setServiceModel(service);
        File output = getOutputFile("stock_noanno_wrapped.wsdl");
        generator.generate(output);
        assertTrue(output.exists());

        String expectedFile = getClass().getResource("expected/stock_noanno_wrapped.wsdl").getFile();
        assertFileEquals(expectedFile, output.getAbsolutePath());
    }

    // Revisit:
    // * Missing wsdl:types
    // * Binding style should be RPC not Document
    // * input message of binding operation "getPrice" MUST specify a value for the "namespace" attribute
    // * output message of binding operation "getPrice" MUST specify a value for the "namespace" attribute
    // CXF-522
    public void testGeneratedWithRPCClass() throws Exception {
        builder.setServiceClass(org.apache.cxf.tools.fortest.classnoanno.rpc.Stock.class);
        ServiceInfo service =  builder.build();
        generator.setServiceModel(service);
        File output = getOutputFile("stock_noanno_rpc.wsdl");
        generator.generate(output);
        assertTrue(output.exists());
    }

    private File getOutputFile(String fileName) {
        return new File(output, fileName);
    }
}
