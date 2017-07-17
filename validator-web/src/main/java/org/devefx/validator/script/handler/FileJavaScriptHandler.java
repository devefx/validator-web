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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;

import org.devefx.validator.util.ResourceUtils;

public class FileJavaScriptHandler extends JavaScriptHandler {
	
	public static final String PATH_RESOURCE = "META-INF/resources/validatorjs";
	
    /** The name of the resource in the classpath that we read our contents from */
    private final String resource;

    /** The name of the copyright file in the classpath that we read our contents from */
    private final String copyright;

    public FileJavaScriptHandler(String resource) {
    	this(resource, null);
	}
    
    public FileJavaScriptHandler(String resource, String copyright) {
    	this.resource = resource;
    	this.copyright = copyright;
	}
    
	@Override
	protected String generateJavaScript(String contextPath, String servletPath,
			String pathInfo) throws IOException {
		
		String javascript = ResourceUtils.getResourceAsString(resource);
		
		if (!debug && copyright != null) {
			javascript = ResourceUtils.getResourceAsString(copyright) + javascript;
		}
		return javascript;
	}

	@Override
	protected long getLastModifiedTime() {
		try {
			URL url = ResourceUtils.getResourceAsUrl(resource);
			if ("file".equals(url.getProtocol())) {
				File file = new File(url.getFile());
				return file.lastModified();
			}
		} catch (IOException ex) {
			// ignore
		}
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		long startTime = runtimeMXBean.getStartTime();
		// Container start time (browsers are only accurate to the second)
		return startTime - (startTime % 1000);
	}
}
