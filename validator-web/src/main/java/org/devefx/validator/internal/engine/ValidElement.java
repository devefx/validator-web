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

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.devefx.validator.GroupMatchException;
import org.devefx.validator.Valid;
import org.devefx.validator.Validation;
import org.devefx.validator.Valid.Group;
import org.devefx.validator.Valid.Group.GroupMapping;
import org.devefx.validator.internal.util.BeanReader;
import org.devefx.validator.util.ObjectUtils;

public class ValidElement {
    
    public static final ValidElement NULL_VALID = new ValidElement(null);
    
    public static final Class<?>[] EMPTY_GROUPS = new Class<?>[0];
    
    private Valid annotation;
    
    public static ValidElement by(AnnotatedElement annotatedElement) {
        Valid annotation = annotatedElement.getAnnotation(Valid.class);
        if (annotation == null) {
            return NULL_VALID;
        }
        return new ValidElement(annotation);
    }
    
    public ValidElement(Valid annotation) {
        this.annotation = annotation;
    }
    
    public Class<?>[] getGroups(Object bean) throws GroupMatchException {
        if (bean == null) {
            return getGroups();
        }
        Group[] annotatedGroups = annotation.groupsFormRequest();
        if (ObjectUtils.isEmpty(annotatedGroups)) {
            return getGroups();
        }
        BeanReader beanReader = new BeanReader(bean);
        
        Set<Class<?>> groups = newGroups();
        for (Group annotatedGroup : annotatedGroups) {
            Class<?> group = null;
            Object value = beanReader.getProperty(annotatedGroup.name());
            if (value != null) {
                for (GroupMapping mapping : annotatedGroup.mappings()) {
                    if (mapping.name().equals(value)) {
                        group = mapping.group();
                        break;
                    }
                }
            }
            if (group == null) {
                if (annotatedGroup.required()) {
                    throw new GroupMatchException(String.valueOf(value),
                            "[" + annotatedGroup.name() + "]");
                }
                group = annotatedGroup.defaultGroup();
            }
            groups.add(group);
        }
        return groups.toArray(EMPTY_GROUPS);
    }
    
    protected Set<Class<?>> newGroups() {
        Set<Class<?>> groups = new HashSet<>();
        if (!ObjectUtils.isEmpty(getGroups())) {
            groups.addAll(Arrays.asList(getGroups()));
        }
        return groups;
    }
    
    public Class<?>[] getGroups() {
        return annotation.groups();
    }
    
    public Class<? extends Validation> getValidationClass() {
        return annotation.value();
    }
    
    public Class<?> getRequestType() {
        return annotation.requestType();
    }
    
    public boolean isConstrained() {
        return annotation != null;
    }
}
