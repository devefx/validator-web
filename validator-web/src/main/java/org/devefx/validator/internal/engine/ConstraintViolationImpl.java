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

import java.io.Serializable;
import java.util.Map;

import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintViolation;

public class ConstraintViolationImpl implements ConstraintViolation, Serializable {
    private static final long serialVersionUID = 4264900453931994813L;
    
    private final String name;
    private final String messageTemplate;
    private final String interpolatedMessage;
    private final Map<String, Object> initParameters;
    private final Class<?> rootBeanClass;
    private final Object rootBean;
    private final Object value;
    private final ConstraintDescriptor constraintDescriptor;

    public static ConstraintViolationImpl forParameterValidation(String name,
                                                                 String messageTemplate,
                                                                 String interpolatedMessage,
                                                                 Map<String, Object> initParameters,
                                                                 Class<?> rootBeanClass,
                                                                 Object rootBean,
                                                                 Object value,
                                                                 ConstraintDescriptor constraintDescriptor) {
        return new ConstraintViolationImpl(name,
                messageTemplate,
                interpolatedMessage,
                initParameters,
                rootBeanClass,
                rootBean,
                value,
                constraintDescriptor);
    }
    
    private ConstraintViolationImpl(String name,
             String messageTemplate,
             String interpolatedMessage,
             Map<String, Object> initParameters,
             Class<?> rootBeanClass,
             Object rootBean,
             Object value,
             ConstraintDescriptor constraintDescriptor) {
        this.name = name;
        this.messageTemplate = messageTemplate;
        this.interpolatedMessage = interpolatedMessage;
        this.initParameters = initParameters;
        this.rootBeanClass = rootBeanClass;
        this.rootBean = rootBean;
        this.value = value;
        this.constraintDescriptor = constraintDescriptor;
    }
    
    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getMessage() {
        return interpolatedMessage;
    }

    @Override
    public final String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public final Class<?> getRootBeanClass() {
        return rootBeanClass;
    }
    
    @Override
    public final Object getRootBean() {
        return rootBean;
    }
    
    @Override
    public final Object getInvalidValue() {
        return value;
    }

    @Override
    public final ConstraintDescriptor getConstraintDescriptor() {
        return constraintDescriptor;
    }
    
    public Map<String, Object> getInitParameters() {
        return initParameters;
    }
}
