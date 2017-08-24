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

package org.devefx.validator.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class ResourceUtils {

    private static ClassLoader defaultClassLoader;
    private static Charset charset;

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        ResourceUtils.defaultClassLoader = defaultClassLoader;
    }

    public static URL getResourceAsUrl(ClassLoader loader, String resource) throws IOException {
        URL url = null;
        if (loader != null) url = loader.getResource(resource);
        if (url == null) url = ClassLoader.getSystemResource(resource);
        if (url == null) throw new IOException("Could not find resource " + resource);
        return url;
    }
    
    public static URL getResourceAsUrl(String resource) throws IOException {
        return getResourceAsUrl(getClassLoader(), resource);
    }
    
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = null;
        if (loader != null) in = loader.getResourceAsStream(resource);
        if (in == null) in = ClassLoader.getSystemResourceAsStream(resource);
        if (in == null) throw new IOException("Could not find resource " + resource);
        return in;
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(getClassLoader(), resource);
    }

    public static Reader getResourceAsReader(String resource) throws IOException {
        if (charset == null) {
            return new InputStreamReader(getResourceAsStream(resource));
        }
        return new InputStreamReader(getResourceAsStream(resource), charset);
    }

    public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
        if (charset == null) {
            return new InputStreamReader(getResourceAsStream(loader, resource));
        }
        return new InputStreamReader(getResourceAsStream(loader, resource), charset);
    }

    public static String getResourceAsString(String resource) throws IOException {
        return getResourceAsString(getClassLoader(), resource);
    }

    public static String getResourceAsString(ClassLoader loader, String resource) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(getResourceAsReader(loader, resource));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (builder.length() != 0)
                    builder.append("\n");
                builder.append(line);
            }
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }
        return builder.toString();
    }

    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }
    
    public static Reader getUrlAsReader(String urlString) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }
        return reader;
    }
    
    public static void close(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    public static ClassLoader getClassLoader() {
        if (defaultClassLoader != null)
            return defaultClassLoader;
        return Thread.currentThread().getContextClassLoader();
    }

    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        ResourceUtils.charset = charset;
    }
}
