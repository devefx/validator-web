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

import org.devefx.validator.Validator;
import org.devefx.validator.ValidatorUtils;
import org.devefx.validator.internal.util.WebContextThreadStack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractValidatorHttpServlet extends HttpServlet {

	private static final long serialVersionUID = -3075517295438877891L;

	private ServletConfig servletConfig;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		this.servletConfig = servletConfig;
	}
	
	@Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		try {
			WebContextThreadStack.engageThread(servletConfig, req, resp);
			
			Validator validator = ValidatorUtils.getValidator();
	        if (!validator.validate(getClass(), req, resp)) {
	            return;
	        }
	        super.service(req, resp);
		} finally {
			WebContextThreadStack.disengageThread();
		}
    }
}
