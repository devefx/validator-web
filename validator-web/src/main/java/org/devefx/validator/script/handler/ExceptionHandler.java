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

public class ExceptionHandler implements Handler {
	
	private static final Log log = LogFactory.getLog(ExceptionHandler.class);

	private Exception cause;
	
	/**
	 * The cause of the failure
	 * @param cause
	 */
	public void setException(Exception cause) {
		this.cause = cause;
	}
	
	@Override
	public String getPath() {
		throw new RuntimeException("not support path maping");
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		if (log.isWarnEnabled()) {
			log.warn("Error: " + cause);
		}
		
		try {
			// We are going to act on this in engine.js so we are hoping that
            // that SC_NOT_IMPLEMENTED (501) is not something that the servers
            // use that much. I would have used something unassigned like 506+
            // But that could cause future problems and might not get through
            // proxies and the like
			response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Error. Details logged to the console");
		} catch (Exception ex) {
			// If the browser has gone away we expect to fail, and may not be
            // able to recover
            // Technically an IOException should work here, but Jetty appears to
            // throw an ArrayIndexOutOfBoundsException sometimes, if the browser
            // has gone away.
			log.debug("Error in error handler, the browser probably went away: " + ex);
		}
	}
}
