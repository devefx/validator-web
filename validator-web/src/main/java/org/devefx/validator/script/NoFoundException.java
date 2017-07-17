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

package org.devefx.validator.script;

import org.devefx.validator.ValidationException;

public class NoFoundException extends ValidationException {
	
	private static final long serialVersionUID = -7468238397030495230L;

	public NoFoundException(String message) {
		super(message);
	}
	
	public NoFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
