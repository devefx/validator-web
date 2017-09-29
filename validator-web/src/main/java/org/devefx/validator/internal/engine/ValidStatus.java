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

import java.util.List;

import org.devefx.validator.ConstraintViolation;

public class ValidStatus {
    
    public static final String VALID_RESULT_KEY = "org.devefx.validator.internal.engine.ValidStatus#VALID_RESULT_KEY";

    private final Class<?>[] validGroups;
    
    private final List<ConstraintViolation> violations;
    
    public ValidStatus(Class<?>[] validGroups, List<ConstraintViolation> violations) {
        this.validGroups = validGroups;
        this.violations = violations;
    }
    
    public Class<?>[] getValidGroups() {
        return validGroups;
    }
    
    public List<ConstraintViolation> getViolations() {
        return violations;
    }
}
