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

package org.devefx.validator;

public class ValidatorConfig {
	
	public static final boolean DEFAULT_FAIL_FAST = false;
	
	public static final boolean DEFAULT_THROW_EXCEPTION = false;
	
	private boolean failFast = DEFAULT_FAIL_FAST;
	
	private boolean throwException = DEFAULT_THROW_EXCEPTION;
	
	private ValidatorDelegate validatorDelegate;
	
	private InvalidHandler invalidHandler;

	public boolean isFailFast() {
		return failFast;
	}

	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	public boolean isThrowException() {
		return throwException;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}
	
	public ValidatorDelegate getValidatorDelegate() {
		return validatorDelegate;
	}
	
	public void setValidatorDelegate(ValidatorDelegate validatorDelegate) {
		this.validatorDelegate = validatorDelegate;
	}
	
	public InvalidHandler getInvalidHandler() {
		return invalidHandler;
	}
	
	public void setInvalidHandler(InvalidHandler invalidHandler) {
		this.invalidHandler = invalidHandler;
	}
}
