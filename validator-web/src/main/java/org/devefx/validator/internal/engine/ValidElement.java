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

package org.devefx.validator.internal.engine;

import java.lang.reflect.AnnotatedElement;

import org.devefx.validator.Valid;
import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;

public class ValidElement {

	private final Class<? extends Validation> validationClass;
	private final Class<?>[] groups;
	private final Class<?> requestType;
	private final boolean hasConstrained;
	private ValidationContext.Accessor validationContext;
	
	public static ValidElement by(AnnotatedElement annotatedElement) {
		if (!annotatedElement.isAnnotationPresent(Valid.class)) {
			return new ValidElement();
		}
		Valid valid = annotatedElement.getAnnotation(Valid.class);
		return new ValidElement(valid.value(), valid.groups(), valid.requestType());
	}
	
	private ValidElement() {
		this(null, null, null, false);
	}
	
	private ValidElement(Class<? extends Validation> validationClass,
			Class<?>[] groups,
			Class<?> requestType) {
		this(validationClass, groups, requestType, true);
	}
	
	private ValidElement(Class<? extends Validation> validationClass,
			Class<?>[] groups,
			Class<?> requestType,
			boolean hasConstrained) {
		this.validationClass = validationClass;
		this.groups = groups;
		this.requestType = requestType;
		this.hasConstrained = hasConstrained;
	}
	
	public Class<?>[] getGroups() {
		return groups;
	}
	
	public Class<? extends Validation> getValidationClass() {
		return validationClass;
	}
	
	public Class<?> getRequestType() {
		return requestType;
	}
	
	public boolean isConstrained() {
		return hasConstrained;
	}
	
	public ValidationContext.Accessor getValidationContext() {
		return validationContext;
	}
	
	public void setValidationContext(ValidationContext.Accessor validationContext) {
		this.validationContext = validationContext;
	}
}
