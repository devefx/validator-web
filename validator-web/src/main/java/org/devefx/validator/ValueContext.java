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

import org.devefx.validator.groups.Default;

public class ValueContext {
    
    private final Object currentBean;
    
    private final Class<?> currentBeanType;
    
    private Class<?> currentGroup;
    
    private Object currentValue;
    
    public ValueContext(Object currentBean, Class<?> currentBeanType) {
        this.currentBean = currentBean;
        this.currentBeanType = currentBeanType;
    }
    
    public final Object getCurrentBean() {
        return currentBean;
    }
    
    public Class<?> getCurrentBeanType() {
        return currentBeanType;
    }
    
    public final Class<?> getCurrentGroup() {
        return currentGroup;
    }
    
    public final Object getCurrentValidatedValue() {
        return currentValue;
    }
    
    public final void setCurrentGroup(Class<?> currentGroup) {
        this.currentGroup = currentGroup;
    }
    
    public final void setCurrentValidatedValue(Object currentValue) {
        this.currentValue = currentValue;
    }
    
    public final boolean validatingDefault() {
        return getCurrentGroup() != null && getCurrentGroup().getName().equals(Default.class.getName());
    }
    
}
