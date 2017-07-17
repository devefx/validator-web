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

import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintValidator;

import java.util.Collection;
import java.util.Set;

public class ConstraintDescriptorImpl implements ConstraintDescriptor {

    private final String name;
    private final String messageTemplate;
    private final Collection<Class<?>> groups;
    private final ConstraintValidator constraintValidator;

    public ConstraintDescriptorImpl(String name,
                                    String messageTemplate,
                                    Set<Class<?>> groups,
                                    ConstraintValidator constraintValidator) {
        this.name = name;
        this.messageTemplate = messageTemplate;
        this.groups = groups;
        this.constraintValidator = constraintValidator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public Collection<Class<?>> getGroups() {
        return groups;
    }

    @Override
    public ConstraintValidator getConstraintValidator() {
        return constraintValidator;
    }
}
