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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.beans.factory.annotation.Value;
import org.devefx.validator.script.NoFoundException;
import org.devefx.validator.script.mapping.Mapping;

public class RemoteValidateHandler extends BaseValidationHandler {
	
	public static final String PARAMETER_ID = "id";
	public static final String PARAMETER_VALUE = "value";
	
	@Value("${RemoteValidateHandler.path}")
	@Override
	public void setPath(String path) {
		super.setPath(path);
	}
	
	@Value("${RemoteValidateHandler.suffix}")
	@Override
	public void setSuffix(String suffix) {
		super.setSuffix(suffix);
	}
	
	@Override
	public int getPeriodCacheableTime() {
		return 0;
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String content = null;
		
		String scriptName = super.generateJavaScript(request.getContextPath(),
				request.getServletPath(), request.getPathInfo());
		
		if (scriptName != null) {
			try {
				boolean result = validateSingleConstraint(request, scriptName);
				content = String.valueOf(result);
			} catch (NoFoundException e) {
				// send 404
			}
		}
		
		if (content != null) {
			response.setContentType(getContentType());
			PrintWriter out = response.getWriter();
			out.write(content);
        	out.flush();
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Override
	protected String generateValidationScript(String contextPath,
			String servletPath, String scriptName) {
		// return the script name
		return scriptName;
	}
	
	protected boolean validateSingleConstraint(HttpServletRequest request, String name) 
			throws NoFoundException {
		
		Mapping mapping = mappingManager.getMapping(name);
		
		if (mapping == null) {
			throw new NoFoundException("not found mapping: " + name);
		}
		
		String id = request.getParameter(PARAMETER_ID);
		
		ConstraintValidator validator = lookupConstraintValidator(mapping.getValidationContext(), id);
		
		if (validator == null) {
			throw new NoFoundException("not found constraint: " + id);
		}
		
		try {
			String value = request.getParameter(PARAMETER_VALUE);
			return validator.isValid(value);
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	private ConstraintValidator lookupConstraintValidator(ValidationContext.Accessor context, String id) {
		for (ConstraintDescriptor descriptor : context.getConstraintDescriptors()) {
			ConstraintValidator constraintValidator = descriptor.getConstraintValidator();
			if (Integer.toHexString(constraintValidator.hashCode()).equals(id)) {
				return constraintValidator;
			}
		}
		return null;
	}

	@Override
	protected long getLastModifiedTime() {
		/** unused */
		return 0;
	}
}
