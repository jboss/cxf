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

package org.apache.cxf.tools.wadlto.jaxrs;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.tools.common.ProcessorTestBase;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.wadlto.WadlToolConstants;

import org.junit.Test;

public class JAXRSContainerTest extends ProcessorTestBase {

    @Test    
    public void testCodeGenInterfaces() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstore.xml"));
            context.put(WadlToolConstants.CFG_COMPILE, "true");

            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", true, false, "org.apache.cxf.jaxrs.model.wadl");
            verifyFiles("class", true, false, "org.apache.cxf.jaxrs.model.wadl");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test    
    public void testCodeGenWithImportedSchema() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstoreImport.xml"));
            context.put(WadlToolConstants.CFG_COMPILE, "true");

            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", false, false, "org.apache.cxf.jaxrs.model.wadl");
            verifyFiles("class", false, false, "org.apache.cxf.jaxrs.model.wadl");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test    
    public void testCodeGenWithImportedSchemaAndCatalog() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstoreImportCatalog.xml"));
            context.put(WadlToolConstants.CFG_CATALOG, getLocation("/wadl/jax-rs-catalog.xml"));
            context.put(WadlToolConstants.CFG_COMPILE, "true");

            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", false, false, "org.apache.cxf.jaxrs.model.wadl");
            verifyFiles("class", false, false, "org.apache.cxf.jaxrs.model.wadl");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test    
    public void testCodeGenNoIds() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/singleResource.xml"));
            context.put(WadlToolConstants.CFG_RESOURCENAME, "CustomResource");
            context.put(WadlToolConstants.CFG_COMPILE, "true");
            
            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            List<File> javaFiles = FileUtils.getFilesRecurse(output, ".+\\." + "java" + "$");
            assertEquals(1, javaFiles.size());
            assertTrue(checkContains(javaFiles, "application.CustomResource.java"));
            
            List<File> classFiles = FileUtils.getFilesRecurse(output, ".+\\." + "class" + "$");
            assertEquals(1, classFiles.size());
            assertTrue(checkContains(classFiles, "application.CustomResource.class"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test    
    public void testCodeGenInterfacesCustomPackage() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);
            
            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstore.xml"));
            context.put(WadlToolConstants.CFG_PACKAGENAME, "custom.books");
            context.put(WadlToolConstants.CFG_COMPILE, "true");

            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", true, false, "custom.books");
            verifyFiles("class", true, false, "custom.books");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test    
    public void testCodeGenImpl() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstore.xml"));
            context.put(WadlToolConstants.CFG_SERVER, "true");
            context.put(WadlToolConstants.CFG_COMPILE, "true");
            
            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", true, false, "org.apache.cxf.jaxrs.model.wadl");
            verifyFiles("class", true, false, "org.apache.cxf.jaxrs.model.wadl");
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    
    @Test    
    public void testCodeGenInterfaceAndImpl() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);
         
            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstore.xml"));
            context.put(WadlToolConstants.CFG_INTERFACE, "true");
            context.put(WadlToolConstants.CFG_SERVER, "true");
            context.put(WadlToolConstants.CFG_COMPILE, "true");
            
            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyFiles("java", true, true, "org.apache.cxf.jaxrs.model.wadl");
            verifyFiles("class", true, true, "org.apache.cxf.jaxrs.model.wadl");
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    
    @Test    
    public void testCodeGenTypesOnly() {
        try {
            JAXRSContainer container = new JAXRSContainer(null);

            ToolContext context = new ToolContext();
            context.put(WadlToolConstants.CFG_OUTPUTDIR, output.getCanonicalPath());
            context.put(WadlToolConstants.CFG_WADLURL, getLocation("/wadl/bookstore.xml"));
            context.put(WadlToolConstants.CFG_TYPES, "true");

            container.setContext(context);
            container.execute();

            assertNotNull(output.list());
            
            verifyTypes("java");
            
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    
    private void verifyFiles(String ext, boolean subresourceExpected, boolean interfacesAndImpl, 
                             String resourcePackage) {
        List<File> files = FileUtils.getFilesRecurse(output, ".+\\." + ext + "$");
        int size = interfacesAndImpl ? 9 : 7;
        if (!subresourceExpected) {
            size--;
        }
        assertEquals(size, files.size());
        doVerifyTypes(files, ext);
        if (subresourceExpected) {
            assertTrue(checkContains(files, resourcePackage + ".FormInterface." + ext));
        }
        assertTrue(checkContains(files, resourcePackage + ".BookStore." + ext));
        if (interfacesAndImpl) {
            if (subresourceExpected) {
                assertTrue(checkContains(files, resourcePackage + ".FormInterfaceImpl." + ext));
            }
            assertTrue(checkContains(files, resourcePackage + ".BookStoreImpl." + ext));
        }
    }
    
    private void verifyTypes(String ext) {
        List<File> files = FileUtils.getFilesRecurse(output, ".+\\." + ext + "$");
        assertEquals(5, files.size());
        doVerifyTypes(files, ext);
    }
    
    private void doVerifyTypes(List<File> files, String ext) {
        assertTrue(checkContains(files, "superbooks.Book." + ext));
        assertTrue(checkContains(files, "superbooks.Book2." + ext));
        assertTrue(checkContains(files, "superbooks.Chapter." + ext));
        assertTrue(checkContains(files, "superbooks.ObjectFactory." + ext));
        assertTrue(checkContains(files, "superbooks.package-info." + ext));
    }
    
    private boolean checkContains(List<File> clsFiles, String name) {
        for (File f : clsFiles) {
            if (f.getAbsolutePath().replace(File.separatorChar, '.').endsWith(name)) {
                return true;
            }
        }
        return false;
    }
    
    protected String getLocation(String wsdlFile) throws URISyntaxException {
        return getClass().getResource(wsdlFile).toString();
    }
}
