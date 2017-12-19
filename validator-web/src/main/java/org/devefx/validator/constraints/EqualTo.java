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
import org.devefx.validator.internal.util.BeanReader;
import org.devefx.validator.internal.util.ThreadContext;
import org.devefx.validator.script.annotation.Script;

@Script
public class EqualTo implements ConstraintValidator {
    
    @InitParam
    private String name;
    @InitParam
    private boolean ignoreCase;
    
    public EqualTo(String name) {
        this(name, false);
    }
    
    public EqualTo(String name, boolean ignoreCase) {
        this.name = name;
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        final BeanReader beanReader = new BeanReader(ThreadContext.getModel());
        final Object diffValue = beanReader.getProperty(this.name);
        if (diffValue == null) {
            return false;
        }
        final String diffString = diffValue.toString();
        return ignoreCase ? diffString.equalsIgnoreCase(value.toString())
                : diffString.equals(value);
    }

}
