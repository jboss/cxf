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
package org.apache.cxf.rs.security.saml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.cxf.common.util.PropertyUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

public class DeflateEncoderDecoder {
    public InputStream inflateToken(byte[] deflatedToken) 
        throws DataFormatException {
        Inflater inflater = new Inflater(true);
        inflater.setInput(deflatedToken);

        byte[] buffer = new byte[deflatedToken.length];
        int inflateLen;
        ByteArrayOutputStream inflatedToken = new ByteArrayOutputStream();
        while (!inflater.finished()) {
            inflateLen = inflater.inflate(buffer, 0, deflatedToken.length);
            if (inflateLen == 0 && !inflater.finished()) {
                if (inflater.needsInput()) {
                    throw new DataFormatException("Inflater can not inflate all the token bytes");
                } else {
                    break;
                }
            }
            
            inflatedToken.write(buffer, 0, inflateLen);
        }

        return new ByteArrayInputStream(inflatedToken.toByteArray());
    }
    
    public byte[] deflateToken(byte[] tokenBytes) {
        return deflateToken(tokenBytes, true);
    }
    
    public byte[] deflateToken(byte[] tokenBytes, boolean nowrap) {
        
        return deflateToken(tokenBytes, getDeflateLevel(), nowrap);
    }
    
    public byte[] deflateToken(byte[] tokenBytes, int level, boolean nowrap) {
        
        Deflater compresser = new Deflater(level, nowrap);
        
        compresser.setInput(tokenBytes);
        compresser.finish();
        
        byte[] output = new byte[tokenBytes.length * 2];
        
        int compressedDataLength = compresser.deflate(output);
        
        byte[] result = new byte[compressedDataLength];
        System.arraycopy(output, 0, result, 0, compressedDataLength);
        return result;
    }
    
    private static int getDeflateLevel() {
        Integer level = null;
        
        Message m = PhaseInterceptorChain.getCurrentMessage();
        if (m != null) {
            level = PropertyUtils.getInteger(m, "deflate.level");
        }
        if (level == null) {
            level = Deflater.DEFLATED;
        }
        return level;
    }
}
