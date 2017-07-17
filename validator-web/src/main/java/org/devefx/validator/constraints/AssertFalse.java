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

package org.devefx.validator.constraints;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.script.annotation.Script;

@Script
public class AssertFalse implements ConstraintValidator {

	@Override
	public boolean isValid(Object value) {
		//null values are valid
		if (value == null) {
			return true;
		}
		// converter type
		Boolean bool;
		if (value instanceof Boolean) {
			bool = (Boolean)value;
		} else if (value instanceof String) {
			bool = value.equals("true");
		} else {
			throw new IllegalArgumentException("Unsupported of type [" + value.getClass().getName() + "]");
		}
		return !bool;
	}

}
