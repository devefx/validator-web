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

import org.devefx.validator.beans.factory.annotation.Inject;
import org.devefx.validator.beans.factory.annotation.Value;
import org.devefx.validator.script.Compressor;

public abstract class JavaScriptHandler extends CachingHandler {
	
	protected Compressor compressor;
	
	protected boolean debug;
	
	protected String suffix = "";
	
	@Inject(required=false)
	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}
	
	@Value("${debug}")
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Set the suffix that gets appended to view names when building a URL.
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the suffix that gets appended to view names when building a URL.
	 */
	protected String getSuffix() {
		return this.suffix;
	}
	
	@Override
	public String generateCachableContent(String contextPath,
			String servletPath, String pathInfo) throws IOException {
		
		String javascript = generateJavaScript(contextPath, servletPath, pathInfo);
		
		if (debug || compressor == null || javascript == null) {
			return javascript;
		}
		
		try {
			return compressor.compressJavaScript(javascript);
		} catch (Exception ex) {
			if (log.isWarnEnabled()) {
				log.warn("Compression system (" + compressor.getClass().getSimpleName() +") failed to compress script", ex);
			}
			return javascript;
		}
	}
	
	protected abstract String generateJavaScript(String contextPath, String servletPath, String pathInfo) throws IOException;
}
