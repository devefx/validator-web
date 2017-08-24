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
import java.io.PushbackInputStream;

import javax.servlet.ServletInputStream;

public class PushbackServletInputStream extends ServletInputStream {

    private final PushbackInputStream pushbackInputStream;
    
    public PushbackServletInputStream(InputStream in) {
        this(in, 1);
    }
    
    public PushbackServletInputStream(InputStream in, int size) {
        this.pushbackInputStream = new PushbackInputStream(in, size);
    }
    
    @Override
    public int available() throws IOException {
        return this.pushbackInputStream.available();
    }
    
    @Override
    public int read() throws IOException {
        return this.pushbackInputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.pushbackInputStream.read(b, off, len);
    }
    
    public void unread(int b) throws IOException {
        this.pushbackInputStream.unread(b);
    }
    
    public void unread(byte[] b, int off, int len) throws IOException {
        this.pushbackInputStream.unread(b, off, len);
    }
    
    public void unread(byte[] b) throws IOException {
        this.pushbackInputStream.unread(b);
    }
}
