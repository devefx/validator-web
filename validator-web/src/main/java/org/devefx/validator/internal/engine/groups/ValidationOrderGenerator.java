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

package org.devefx.validator.internal.engine.groups;

import java.util.Arrays;
import java.util.Collection;

import org.devefx.validator.ValidationException;
import org.devefx.validator.groups.Default;

public class ValidationOrderGenerator {
    
    private final DefaultValidationOrder validationOrderForDefaultGroup;
    
    public ValidationOrderGenerator() {
        validationOrderForDefaultGroup = new DefaultValidationOrder();
        validationOrderForDefaultGroup.insertGroup(Group.DEFAULT_GROUP);
    }
    
    /**
     * Creates a {@link ValidationOrder} for the given validation group.
     *
     * @param group the group to get as order
     * @param expand whether the given group should be expanded (i.e. flattened it
     * to its members if it is a sequence or group extending another
     * group) or not
     *
     * @return a {@link ValidationOrder} for the given validation group
     */
    public ValidationOrder getValidationOrder(Class<?> group, boolean expand) {
        if (expand) {
            return getValidationOrder(Arrays.<Class<?>>asList(group));
        } else {
            DefaultValidationOrder validationOrder = new DefaultValidationOrder();
            validationOrder.insertGroup(new Group(group));
            return validationOrder;
        }
    }
    
    /**
     * Generates a order of groups and sequences for the specified validation groups.
     *
     * @param groups the groups specified at the validation call
     *
     * @return an instance of {@code ValidationOrder} defining the order in which validation has to occur
     */
    public ValidationOrder getValidationOrder(Collection<Class<?>> groups) {
        if (groups == null || groups.size() == 0) {
            throw new IllegalArgumentException("At least one group has to be specified.");
        }
        
        if (groups.size() == 1 && groups.contains(Default.class)) {
            return validationOrderForDefaultGroup;
        }
        
        for (Class<?> clazz : groups) {
            if (!clazz.isInterface()) {
                throw new ValidationException("A group has to be an interface. " + clazz + " is not.");
            }
        }
        
        DefaultValidationOrder validationOrder = new DefaultValidationOrder();
        for (Class<?> clazz : groups) {
            if (Default.class.equals(clazz)) {
                validationOrder.insertGroup(Group.DEFAULT_GROUP);
            } else {
                Group group = new Group(clazz);
                validationOrder.insertGroup(group);
                insertInheritedGroups(clazz, validationOrder);
            }
        }
        
        return validationOrder;
    }
    
    /**
     * Recursively add inherited groups into the group chain.
     *
     * @param clazz the group interface
     * @param chain the group chain we are currently building
     */
    private void insertInheritedGroups(Class<?> clazz, DefaultValidationOrder chain) {
        for (Class<?> inheritedGroup : clazz.getInterfaces()) {
            Group group = new Group(inheritedGroup);
            chain.insertGroup(group);
            insertInheritedGroups(inheritedGroup, chain);
        }
    }
}
