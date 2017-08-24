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

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script
public class Digits implements ConstraintValidator {

    @InitParam
    private int maxIntegerLength;
    @InitParam
    private int maxFractionLength;
    
    public Digits(int maxIntegerLength, int maxFractionLength) {
        this.maxIntegerLength = maxIntegerLength;
        this.maxFractionLength = maxFractionLength;
        validateParameters();
    }
    
    @Override
    public boolean isValid(Object value) {
        //null values are valid
        if (value == null) {
            return true;
        }
        // converter type
        Number num;
        if (value instanceof Number) {
            num = (Number)value;
        } else if (value instanceof String) {
            num = new BigDecimal((String)value);
        } else {
            throw new IllegalArgumentException("Unsupported of type [" + value.getClass().getName() + "]");
        }

        BigDecimal bigNum;
        if (num instanceof BigDecimal) {
            bigNum = (BigDecimal) num;
        }
        else {
            bigNum = new BigDecimal(num.toString()).stripTrailingZeros();
        }

        int integerPartLength = bigNum.precision() - bigNum.scale();
        int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

        return (maxIntegerLength >= integerPartLength && maxFractionLength >= fractionPartLength);
    }

    private void validateParameters() {
        if (maxIntegerLength < 0) {
            throw new IllegalArgumentException("The length of the integer part cannot be negative.");
        }
        if (maxFractionLength < 0) {
            throw new IllegalArgumentException("The length of the fraction part cannot be negative.");
        }
    }
}
