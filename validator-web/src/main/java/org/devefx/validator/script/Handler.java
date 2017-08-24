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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Handler {
    
    /**
     * @return Path mapping URIs to this Handle
     */
    String getPath();
    
    /**
     * Handle a URL request that has been mapped to this Handler
     * @param request The HTTP request data
     * @param response Where we write the HTTP response data
     * @throws IOException If the write process fails
     */
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
