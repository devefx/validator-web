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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script
public class DecimalMin implements ConstraintValidator {

	@InitParam
	private BigDecimal minValue;
	@InitParam
	private boolean inclusive;
	
	public DecimalMin(String value) {
		this(value, true);
	}
	
	public DecimalMin(String minValue, boolean inclusive) {
		try {
			this.minValue = new BigDecimal(minValue);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException(minValue + " does not represent a valid BigDecimal format.", nfe);
		}
		this.inclusive = inclusive;
	}
	
	@Override
	public boolean isValid(Object value) {
		// null values are valid
		if (value == null) {
			return true;
		}
		
		// converter type
		Number numValue;
		if (value instanceof Number) {
			numValue = (Number)value;
		} else if (value instanceof String) {
			numValue = new BigDecimal((String)value);
		} else {
			throw new IllegalArgumentException("Unsupported of type [" + value.getClass().getName() + "]");
		}
		
		// handling of NaN, positive infinity and negative infinity
		if (numValue instanceof Double) {
			if ((Double) numValue == Double.POSITIVE_INFINITY) {
				return true;
			}
			else if (Double.isNaN((Double) numValue) || (Double) numValue == Double.NEGATIVE_INFINITY) {
				return false;
			}
		} else if (numValue instanceof Float) {
			if ((Float) numValue == Float.POSITIVE_INFINITY) {
				return true;
			}
			else if (Float.isNaN((Float) numValue) || (Float) numValue == Float.NEGATIVE_INFINITY) {
				return false;
			}
		}
		
		int comparisonResult;
		if (numValue instanceof BigDecimal) {
			comparisonResult = ((BigDecimal) numValue).compareTo(minValue);
		}
		else if (value instanceof BigInteger) {
			comparisonResult = (new BigDecimal((BigInteger) numValue)).compareTo(minValue);
		}
		else if (value instanceof Long) {
			comparisonResult = (BigDecimal.valueOf(numValue.longValue()).compareTo(minValue));
		}
		else {
			comparisonResult = (BigDecimal.valueOf(numValue.doubleValue()).compareTo(minValue));
		}
		return inclusive ? comparisonResult >= 0 : comparisonResult > 0;
	}

}
