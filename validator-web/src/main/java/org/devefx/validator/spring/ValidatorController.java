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

package org.devefx.validator.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.internal.util.FakeServletConfig;
import org.devefx.validator.internal.util.StartupUtil;
import org.devefx.validator.internal.util.WebContextThreadStack;
import org.devefx.validator.script.UrlProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;

public class ValidatorController extends AbstractController implements BeanNameAware, BeanFactoryAware, InitializingBean {
    
    private static final Log log = LogFactory.getLog(ValidatorController.class);
    
    private String name;
    
    private SpringContainer container;
    
    private ServletConfig servletConfig;
    
    private UrlProcessor processor;
    
    @Override
    public void setBeanName(String name) {
        this.name = name;
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.container = new SpringContainer(beanFactory);
    }
    
    public void setupUrlMapping(SimpleUrlHandlerMapping urlHandlerMapping) {
        Map<String, Object> urlMap = new HashMap<String, Object>();
        for (String url : this.processor.getUrlMapping().keySet()) {
            if (url.endsWith("/")) {
                url = url + "**";
            }
            urlMap.put(url, this);
        }
        urlHandlerMapping.setUrlMap(urlMap);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        ServletContext servletContext = getServletContext();
        Assert.notNull(servletContext, "The servlet context has not been set on the controller");
        
        this.servletConfig = new FakeServletConfig(this.name, getServletContext());
        
        try {
            StartupUtil.setupDefaultContainer(this.container, this.servletConfig);
        } catch (Exception e) {
            log.fatal("init failed", e);
        }
        
        this.processor = this.container.getBean(UrlProcessor.class);
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            // set up the web context and delegate to the processor
            WebContextThreadStack.engageThread(servletConfig, request, response);
            this.processor.handle(request, response);
        } finally {
            WebContextThreadStack.disengageThread();
        }
        // return null to inform the dispatcher servlet the request has already been handled
        return null;
    }
}
