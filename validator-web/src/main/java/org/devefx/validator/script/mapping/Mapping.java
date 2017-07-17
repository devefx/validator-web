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

package org.devefx.validator.script.mapping;

import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.Validator;
import org.devefx.validator.ValidatorContext;
import org.devefx.validator.ValidatorUtils;

public class Mapping {

	private final Class<? extends Validation> validationClass;
	
	private ValidationContext.Accessor validationContext;
	
	public Mapping(Class<? extends Validation> validationClass) {
		this.validationClass = validationClass;
	}
	
	public Class<? extends Validation> getValidationClass() {
		return validationClass;
	}
	
	public ValidationContext.Accessor getValidationContext() {
		if (validationContext == null) {
			Validator validator = ValidatorUtils.getValidator();
			ValidatorContext validatorContext = validator.getValidatorContext();
			validationContext = validatorContext.getOrCreateValidationContext(validationClass);
		}
		return validationContext;
	}
}
