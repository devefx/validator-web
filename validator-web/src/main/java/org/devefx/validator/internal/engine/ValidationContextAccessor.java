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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.InvalidHandler;
import org.devefx.validator.Validation;
import org.devefx.validator.ValidatorContext;
import org.devefx.validator.ValidatorDelegate;
import org.devefx.validator.ValidationContext.Accessor;
import org.devefx.validator.internal.engine.messageinterpolation.parser.TokenCollector;
import org.devefx.validator.internal.resourceloading.ResourceBundleLocator;
import org.devefx.validator.util.Assert;

public class ValidationContextAccessor implements Accessor {
    
    private static final String MESSAGE_SUFFIX = ".message";
    
    private boolean failFast;
    private boolean throwException;
    private ResourceBundleLocator resourceBundleLocator;
    private ValidatorDelegate validatorDelegate;
    private InvalidHandler invalidHandler;
    private final List<ConstraintDescriptor> constraintDescriptors;
    private final Validation validation;
    private final ValidatorContext validatorContext;
    
    public ValidationContextAccessor(Validation validation, ValidatorContext validatorContext) {
        this.validation = validation;
        this.validatorContext = validatorContext;
        this.constraintDescriptors = new ArrayList<>();
    }
    
    @Override
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    @Override
    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    @Override
    public void setResourceBundleLocator(
            ResourceBundleLocator resourceBundleLocator) {
        this.resourceBundleLocator = resourceBundleLocator;
    }

    @Override
    public void setValidatorDelegate(ValidatorDelegate delegate) {
        this.validatorDelegate = delegate;
    }

    @Override
    public void setInvalidHandler(InvalidHandler invalidHandler) {
        this.invalidHandler = invalidHandler;
    }

    @Override
    public boolean isFailFast() {
        return failFast;
    }

    @Override
    public boolean isThrowException() {
        return throwException;
    }

    @Override
    public ResourceBundleLocator getResourceBundleLocator() {
        return resourceBundleLocator;
    }

    @Override
    public ValidatorDelegate getValidatorDelegate() {
        return validatorDelegate;
    }

    @Override
    public InvalidHandler getInvalidHandler() {
        return invalidHandler;
    }
    
    @Override
    public List<ConstraintDescriptor> getConstraintDescriptors() {
        return constraintDescriptors;
    }
    
    @Override
    public Validation getValidation() {
        return validation;
    }
    
    @Override
    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }
    
    @Override
    public void constraint(String name, String message,
            ConstraintValidator constraintValidator, Class<?>... groups) {
        Assert.notNull(name, "name cannot be null.");
        Assert.notNull(constraintValidator, "constraintValidator cannot be null.");
        
        Set<Class<?>> resultGroups;
        if (groups.length == 0) {
            resultGroups = DEFAULT_GROUPS;
        } else {
            resultGroups = new HashSet<>(Arrays.asList(groups));
        }
        ConstraintDescriptor constraintDescriptor = new ConstraintDescriptorImpl(name, 
                message,
                resultGroups,
                constraintValidator);
        constraintDescriptors.add(constraintDescriptor);
    }

    @Override
    public void constraint(String name,
            ConstraintValidator constraintValidator, Class<?>... groups) {
        Assert.notNull(name, "name cannot be null.");
        Assert.notNull(constraintValidator, "constraintValidator cannot be null.");
        
        String messageTemplate = getDefaultMessagetTemplate(constraintValidator);
        this.constraint(name, 
                messageTemplate,
                constraintValidator,
                groups);
    }
    
    private static String getDefaultMessagetTemplate(ConstraintValidator constraintValidator) {
        Class<?> constraintClass = constraintValidator.getClass();
        String className = constraintClass.getName();
        
        StringBuilder sb = new StringBuilder().append(TokenCollector.BEGIN_TERM);
        sb.append(className).append(MESSAGE_SUFFIX).append(TokenCollector.END_TERM);
        return sb.toString();
    }
}
