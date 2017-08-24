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

package org.devefx.validator.script;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.Container;
import org.devefx.validator.beans.factory.InitializingBean;
import org.devefx.validator.script.handler.ExceptionHandler;
import org.devefx.validator.script.handler.NotFoundHandler;

public class UrlProcessor implements InitializingBean {
    
    private static final Log log = LogFactory.getLog(UrlProcessor.class);

    /**
     * The mapping of URLs to {@link Handler}s
     */
    protected Map<String, Handler> urlMapping = new HashMap<>();
    
    /**
     * The default if we have no other action (HTTP-404)
     */
    protected NotFoundHandler notFoundHandler = new NotFoundHandler();
    
    /**
     * If execution fails, we do this (HTTP-501)
     */
    protected ExceptionHandler exceptionHandler = new ExceptionHandler();
    
    @Override
    public void afterSetup(Container container) {
        Collection<Handler> handlers = container.getBeansOfType(Handler.class).values();
        for (Handler handler : handlers) {
            String path = handler.getPath();
            Handler previous = urlMapping.put(path, handler);
            if (previous != null) {
                throw new RuntimeException("the mapping already exists: " + path +
                        "[" + handler + "]");
            }
        }
    }
    
    public Map<String, Handler> getUrlMapping() {
        return urlMapping;
    }
    
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() == 0 || "/".equals(pathInfo)) {
                notFoundHandler.handle(request, response);
            } else {
                // Loop through all the known URLs
                for (Map.Entry<String, Handler> entry : urlMapping.entrySet()) {
                    String url = entry.getKey();
                    // If this URL matches, call the handler
                    if (pathInfo.startsWith(url)) {
                        Handler handler = entry.getValue();
                        handler.handle(request, response);
                        return;
                    }
                }
                notFoundHandler.handle(request, response);
            }
        } catch (SecurityException se) {
            // We don't want to give the client any information about the security error, handle it with a 404.
            log.error("Security Exception: ", se);
            notFoundHandler.handle(request, response);
        } catch (Exception e) {
            exceptionHandler.setException(e);
            exceptionHandler.handle(request, response);
        }
    }
}
