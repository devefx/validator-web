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

package org.devefx.validator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public interface ServerContext {
	
    /**
     * Accessor for the servlet config.
     * @return Returns the config.
     */
    ServletConfig getServletConfig();

    /**
     * Returns the ServletContext to which this session belongs.
     * @return The servlet context information.
     */
    ServletContext getServletContext();
    
    /**
    *
    * Returns the portion of the request URI that indicates the context
    * of the request. The context path always comes first in a request
    * URI. The path starts with a "/" character but does not end with a "/"
    * character. For servlets in the default (root) context, this method
    * returns "". The container does not decode this string.
    *
    * <p>It is possible that a servlet container may match a context by
    * more than one context path. In such cases this method will return the
    * actual context path used by the request and it may differ from the
    * path returned by the
    * {@link javax.servlet.ServletContext#getContextPath()} method.
    * The context path returned by
    * {@link javax.servlet.ServletContext#getContextPath()}
    * should be considered as the prime or preferred context path of the
    * application.
    *
    * @return		a <code>String</code> specifying the
    *			portion of the request URI that indicates the context
    *			of the request
    *
    * @see javax.servlet.ServletContext#getContextPath()
    */
    String getContextPath();
}
