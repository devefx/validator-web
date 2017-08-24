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

import java.lang.reflect.Array;
import java.util.Collection;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script
public class Size implements ConstraintValidator {

    @InitParam
    private int min;
    @InitParam
    private int max;

    public Size(int min, int max) {
        this.min = min;
        this.max = max;
        validateParameters();
    }
    
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        try {
            int length = 1;
            if (value instanceof Collection) {
                length = ((Collection<?>)value).size();
            } else if (value instanceof Object[]) {
                length = Array.getLength(value);
            }
            return length >= min && length <= max;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void validateParameters() {
        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
        if (max < min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
