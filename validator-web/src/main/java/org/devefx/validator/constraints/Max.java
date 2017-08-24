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
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

import java.math.BigDecimal;
import java.math.BigInteger;

@Script
public class Max implements ConstraintValidator {

    @InitParam
    private long maxValue;
    
    public Max(long maxValue) {
        this.maxValue = maxValue;
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
            if ((Double) numValue == Double.NEGATIVE_INFINITY) {
                return true;
            } else if (Double.isNaN((Double) numValue) || (Double) numValue == Double.POSITIVE_INFINITY) {
                return false;
            }
        } else if (numValue instanceof Float) {
            if ((Float) numValue == Float.NEGATIVE_INFINITY) {
                return true;
            } else if (Float.isNaN((Float) numValue) || (Float) numValue == Float.POSITIVE_INFINITY) {
                return false;
            }
        }
        if (numValue instanceof BigDecimal) {
            return ((BigDecimal) numValue).compareTo(BigDecimal.valueOf( maxValue)) != 1;
        } else if (numValue instanceof BigInteger) {
            return ((BigInteger) numValue).compareTo(BigInteger.valueOf( maxValue)) != 1;
        } else {
            long longValue = numValue.longValue();
            return longValue <= maxValue;
        }
    }

}
