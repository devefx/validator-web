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

package org.devefx.validator.internal.engine.scanner.filter;

import java.lang.annotation.Annotation;

import org.devefx.validator.internal.engine.scanner.TypeFilter;

public class AnnotationTypeFilter implements TypeFilter {

    private final Class<? extends Annotation> annotationType;
    
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }
    
    @Override
    public boolean match(Class<?> type) {
        if (type.getClass().getName().equals(Object.class.getName())) {
            return false;
        }
        return type.isAnnotationPresent(this.annotationType);
    }
}
