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

package org.devefx.validator.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

	private PushbackServletInputStream pushbackInputStream;
	
	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

    public boolean hasMessageBody() throws IOException {
        if (super.getContentLength() == 0) {
            return false;
        }
        return true;
    }
	
    public boolean hasEmptyMessageBody() throws IOException {
    	InputStream body = super.getInputStream();
        if (body == null) {
            return true;
        } else if (body.markSupported()) {
            body.mark(1);
            if (body.read() == -1) {
                return true;
            } else {
                body.reset();
                return false;
            }
        } else {
            this.pushbackInputStream = new PushbackServletInputStream(body);
            int b = this.pushbackInputStream.read();
            if (b == -1) {
                return true;
            } else {
                this.pushbackInputStream.unread(b);
                return false;
            }
        }
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
    	if (this.pushbackInputStream != null) {
    		return this.pushbackInputStream;
    	}
    	return super.getInputStream();
    }
}