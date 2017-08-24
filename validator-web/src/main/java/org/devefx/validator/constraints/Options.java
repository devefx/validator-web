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
import org.devefx.validator.util.Assert;

@Script
public class Options implements ConstraintValidator {

    @InitParam
    private ConstraintValidator[] subValidators;
    
    public Options(ConstraintValidator firstValidator, ConstraintValidator secondValidator,
            ConstraintValidator... subValidators) {
        Assert.notNull(firstValidator, "firstValidator cannot be null.");
        Assert.notNull(secondValidator, "secondValidator cannot be null.");
        
        this.subValidators = new ConstraintValidator[2 + subValidators.length];
        this.subValidators[0] = firstValidator;
        this.subValidators[1] = secondValidator;
        if (subValidators.length != 0) {
            System.arraycopy(subValidators, 0, this.subValidators, 2, subValidators.length);
        }
    }
    
    @Override
    public boolean isValid(Object value) {
        for (ConstraintValidator validator : subValidators) {
            if (validator != null && validator.isValid(value)) {
                return true;
            }
        }
        return false;
    }
}
