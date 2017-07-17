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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class FakeServletConfig implements ServletConfig {

	/**
     * The servlet name
     */
    private final String name;

    /**
     * The servlet deployment information
     */
    private final ServletContext servletContext;

    /**
     * Initialization parameters
     */
    private final Map<String, String> initParameters;
	
    /**
     * @param name The servlet name
     * @param servletContext The ServletContext
     */
    public FakeServletConfig(String name, ServletContext servletContext) {
        this(name, servletContext, null);
    }
    
    /**
     * @param name The servlet name
     * @param servletContext The ServletContext
     * @param initParameters Optional init parameters (can be null)
     */
    public FakeServletConfig(String name, ServletContext servletContext, Map<String, String> initParameters) {
        this.name = name;
        this.servletContext = servletContext;
        this.initParameters = (initParameters != null) ? Collections.unmodifiableMap(initParameters) :
        	Collections.<String, String>emptyMap();
    }
    
    /**
     * Copy the values from another {@link ServletConfig} so we can modify them.
     */
    public FakeServletConfig(ServletConfig servletConfig) {
    	this.name = servletConfig.getServletName();
        this.servletContext = servletConfig.getServletContext();
        this.initParameters = getInitParametersInServletConfig(servletConfig);
    }
    
	@Override
	public String getServletName() {
		return name;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public String getInitParameter(String name) {
		return initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(initParameters.keySet());
	}

    @SuppressWarnings("unchecked")
	private Map<String, String> getInitParametersInServletConfig(ServletConfig servletConfig) {
    	Map<String , String> initParameters = new HashMap<String, String>();
    	Enumeration<String> params = servletConfig.getInitParameterNames();
        while (params.hasMoreElements()) {
        	String name = params.nextElement();
        	String value = servletConfig.getInitParameter(name);
        	initParameters.put(name, value);
		}
        return Collections.unmodifiableMap(initParameters);
    }
}
