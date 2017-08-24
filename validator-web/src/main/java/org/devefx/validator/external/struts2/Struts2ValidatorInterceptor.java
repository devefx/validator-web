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

package org.devefx.validator.external.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.util.ServletContextAware;
import org.devefx.validator.Validator;
import org.devefx.validator.ValidatorUtils;
import org.devefx.validator.internal.util.FakeServletConfig;
import org.devefx.validator.internal.util.WebContextThreadStack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class Struts2ValidatorInterceptor extends MethodFilterInterceptor implements ServletContextAware {

    private static final long serialVersionUID = -7579546773406364670L;

    private ServletConfig servletConfig;
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletConfig = new FakeServletConfig("struts2Interceptor", servletContext);
    }
    
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        ActionProxy actionProxy = invocation.getProxy();
        if (actionProxy instanceof ActionSupport) {
            HttpServletRequest request;
            HttpServletResponse response;
            try {
                ActionContext context = invocation.getInvocationContext();
                request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);
                response = (HttpServletResponse) context.get(StrutsStatics.HTTP_RESPONSE);
            } catch (ClassCastException e) {
                throw new ServletException("non-HTTP request or response");
            }
            try {
                // set up the web context and delegate to the processor
                WebContextThreadStack.engageThread(servletConfig, request, response);
                // validate the request
                Class<?> actionClass = actionProxy.getAction().getClass();
                Validator validator = ValidatorUtils.getValidator();
                if (!validator.validate(actionClass, request, response)) {
                    return null;
                }
                Method actionMethod = actionClass.getDeclaredMethod(actionProxy.getMethod());
                if (!validator.validate(actionMethod, request, response)) {
                    return null;
                }
            } finally {
                WebContextThreadStack.disengageThread();
            }
        }
        return invocation.invoke();
    }
}
