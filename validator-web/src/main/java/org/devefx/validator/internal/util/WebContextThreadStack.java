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

package org.devefx.validator.internal.util;

import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.WebContext;
import org.devefx.validator.web.context.DefaultWebContext;

public final class WebContextThreadStack {
	
	private static final Log log = LogFactory.getLog(WebContextThreadStack.class);

	/**
     * The storage of thread based data as a stack
     */
    private static ThreadLocal<Stack<WebContext>> contextStack = new ThreadLocal<Stack<WebContext>>();
	
	private WebContextThreadStack() {
	}
	
    /**
     * Accessor for the WebContext that is associated with this thread.
     */
	public static WebContext get() {
		Stack<WebContext> stack = contextStack.get();
		if (stack == null || stack.empty()) {
			return null;
		}
		return stack.peek();
    }
	
    /**
     * Make the current thread know what the current request is.
     * @param servletConfig The servlet configuration object used by a servlet container
     * @param request The incoming http request
     * @param response The outgoing http reply
     * @see #disengageThread()
     */
	public static void engageThread(ServletConfig servletConfig, HttpServletRequest request, HttpServletResponse response) {
		try {
			WebContext ec = new DefaultWebContext(request, response, servletConfig,
					servletConfig.getServletContext());
			engageThread(ec);
		} catch (Exception ex) {
			log.fatal("Failed to create an ExecutionContext", ex);
		}
    }
    
    /**
     * Make the current thread know what the current request is.
     * Uses an existing WebContext for example from another thread.
     * @see #disengageThread()
     */
	public static void engageThread(WebContext webContext) {
		Stack<WebContext> stack = contextStack.get();
		if (stack == null) {
			stack = new Stack<WebContext>();
			contextStack.set(stack);
		}
		stack.add(webContext);
    }
    
    /**
     * Unset the current ExecutionContext
     * @see #engageThread(ServletConfig, HttpServletRequest, HttpServletResponse)
     */
	public static void disengageThread() {
		if (contextStack != null) {
			Stack<WebContext> stack = contextStack.get();
			if (stack != null) {
				if (!stack.empty()) {
					stack.pop();
				}
				if (stack.empty()) {
					contextStack.set(null);
				}
			}
		}
    }
}
