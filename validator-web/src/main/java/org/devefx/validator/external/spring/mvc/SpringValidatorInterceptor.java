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

package org.devefx.validator.external.spring.mvc;

import org.devefx.validator.Validator;
import org.devefx.validator.ValidatorUtils;
import org.devefx.validator.internal.util.FakeServletConfig;
import org.devefx.validator.internal.util.WebContextThreadStack;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SpringValidatorInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {
	
	private ServletConfig servletConfig;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletConfig = new FakeServletConfig("springInterceptor", servletContext);
	}
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	Validator validator = ValidatorUtils.getValidator();
        if (handler instanceof HandlerMethod) {
        	try {
        		// set up the web context and delegate to the processor
        		WebContextThreadStack.engageThread(servletConfig, request, response);
        		// validate the request
        		HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (!validator.validate(handlerMethod.getBeanType(), request, response)) {
                    return false;
                }
                if (!validator.validate(handlerMethod.getMethod(), request, response)) {
                    return false;
                }
        	} finally {
        		WebContextThreadStack.disengageThread();
        	}
        }
        return super.preHandle(request, response, handler);
    }
}
