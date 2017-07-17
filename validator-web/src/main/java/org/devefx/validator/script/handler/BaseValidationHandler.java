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
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.factory.annotation.Inject;
import org.devefx.validator.script.mapping.DefaultMappingManager;
import org.devefx.validator.util.ScriptUtil;

public abstract class BaseValidationHandler extends JavaScriptHandler {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected DefaultMappingManager mappingManager;

	@Inject
	public void setMappingManager(DefaultMappingManager mappingManager) {
		this.mappingManager = mappingManager;
	}
	
	@Override
	protected String generateJavaScript(String contextPath, String servletPath,
			String pathInfo) throws IOException {
		
		String scriptName = pathInfo;
		
		if (!scriptName.startsWith(getPath()) || !scriptName.endsWith(getSuffix())) {
			return null;
		}
		
		scriptName = scriptName.substring(getPath().length());
		scriptName = scriptName.substring(0, scriptName.length() - getSuffix().length());
		
		if (!ScriptUtil.isValidScriptName(scriptName)) {
			log.debug("Throwing at request for script with name: '" + scriptName + "'");
            throw new SecurityException("Illegal script name.");
		}
		
		if (scriptName.contains("/")) {
			Pattern p = Pattern.compile(scriptName.replaceAll("/", "[/\\.]"));
			String match = null;
			for (String name : mappingManager.getMappingNames()) {
				if (p.matcher(name).matches()) {
					if (match == null) {
						match = name;
					} else {
						throw new IllegalArgumentException("Script name '" + scriptName + "' matches several validations.");
					}
				}
			}
			if (match != null) {
				scriptName = match;
			}
		}
		
		// Generate script
		return generateValidationScript(contextPath, servletPath, scriptName);
	}
	
	protected abstract String generateValidationScript(String contextPath, String servletPath, String scriptName);
	
}
