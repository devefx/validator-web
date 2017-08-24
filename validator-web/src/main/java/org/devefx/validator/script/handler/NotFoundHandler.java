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

package org.devefx.validator.script.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.script.Handler;

public class NotFoundHandler implements Handler {
    
    private static final Log log = LogFactory.getLog(NotFoundHandler.class);

    @Override
    public String getPath() {
        throw new RuntimeException("not support path maping");
    }
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        if (log.isWarnEnabled()) {
            log.warn("Page not found. pathInfo='" + request.getPathInfo()
                    + "' requestUrl='" + request.getRequestURI() + "'");
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
