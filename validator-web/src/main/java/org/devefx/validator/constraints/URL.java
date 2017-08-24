/*
 * Copyright 2016-2017, Youqian Yue (devefx@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devefx.validator.constraints;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

import java.net.MalformedURLException;

@Script
public class URL implements ConstraintValidator {

    @InitParam
    private String protocol;
    @InitParam
    private String host;
    @InitParam
    private int port;
    
    public URL() {
        this(null, null, -1);
    }
    
    public URL(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        String stringValue = value.toString();
        if (stringValue.length() == 0) {
            return true;
        }
        java.net.URL url;
        try {
            url = new java.net.URL(stringValue);
        } catch (MalformedURLException e) {
            return false;
        }
        
        if (protocol != null && protocol.length() > 0 && !url.getProtocol().equals(protocol)) {
            return false;
        }
        if (host != null && host.length() > 0 && !url.getHost().equals(host)) {
            return false;
        }
        if (port != -1 && url.getPort() != port) {
            return false;
        }
        return true;
    }

}
