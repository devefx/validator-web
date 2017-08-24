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

package org.devefx.validator.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.devefx.validator.WebContext;

public class DefaultWebContext extends DefaultServerContext implements WebContext {

    /**
     * The HttpServletRequest associated with the current request
     */
    private final HttpServletRequest request;

    /**
     * The HttpServletResponse associated with the current request
     */
    private final HttpServletResponse response;
    
    public DefaultWebContext(HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig, ServletContext servletContext) {
        setServletConfig(servletConfig);
        setServletContext(servletContext);
        this.request = request;
        this.response = response;
    }
    
    @Override
    public HttpSession getSession() {
        return request.getSession();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        return response;
    }
}
