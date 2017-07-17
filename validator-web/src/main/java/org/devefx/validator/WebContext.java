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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface WebContext {

    /**
     * Returns the current session associated with this request, or if the
     * request does not have a session, creates one.
     * @return Returns the http session.
     * @see HttpServletRequest#getSession()
     */
    HttpSession getSession();

    /**
     * Returns the current HttpSession associated with this request or, if
     * there is no current session and create is true, returns a new session.
     * If create is false and the request has no valid HttpSession, this method
     * returns null.
     * @param create false to return null if there's no current session
     * @return the session associated with this request
     * @see HttpServletRequest#getSession(boolean)
     */
    HttpSession getSession(boolean create);

    /**
     * Accessor for the http request information.
     * @return Returns the request.
     */
    HttpServletRequest getHttpServletRequest();

    /**
     * Accessor for the http response bean.
     * <p>You can't use this request to directly reply to the response or to add
     * headers or cookies.
     * @return Returns the response.
     */
    HttpServletResponse getHttpServletResponse();
}
