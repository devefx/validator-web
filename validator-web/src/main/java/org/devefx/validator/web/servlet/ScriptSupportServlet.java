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

package org.devefx.validator.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.Container;
import org.devefx.validator.internal.util.StartupUtil;
import org.devefx.validator.internal.util.WebContextThreadStack;
import org.devefx.validator.script.UrlProcessor;

public class ScriptSupportServlet extends HttpServlet {
	
	private static final long serialVersionUID = 3093149631565854976L;
	
	private static final Log log = LogFactory.getLog(ScriptSupportServlet.class);

	protected Container container;
	
	protected ServletConfig servletConfig;
	
	protected UrlProcessor processor;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		try {
			this.container = StartupUtil.createAndSetupDefaultContainer(servletConfig);
			
			this.servletConfig = servletConfig;
		} catch (Exception ex) {
			log.fatal("init failed", ex);
            throw new ServletException(ex);
		}
		
		this.processor = this.container.getBean(UrlProcessor.class);
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// set up the web context and delegate to the processor
			WebContextThreadStack.engageThread(servletConfig, request, response);
			this.processor.handle(request, response);
		} finally {
			WebContextThreadStack.disengageThread();
		}
	}
}
