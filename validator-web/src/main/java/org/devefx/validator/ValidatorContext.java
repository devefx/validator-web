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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.devefx.validator.internal.engine.ConstraintViolationImpl;
import org.devefx.validator.internal.engine.ValidationContextAccessor;
import org.devefx.validator.internal.metadata.ConstraintMetaData;
import org.devefx.validator.internal.metadata.ConstraintMetaDataManager;
import org.devefx.validator.internal.resourceloading.PlatformResourceBundleLocator;
import org.devefx.validator.internal.resourceloading.ResourceBundleLocator;
import org.devefx.validator.messageinterpolation.MessageInterpolatorContext;
import org.devefx.validator.util.Assert;

public class ValidatorContext {

    private final ValidatorFactory validatorFactory;
    
    private final ConstraintMetaDataManager constraintMetaDataManager;
    
    private final Map<Class<? extends Validation>, ValidationContext.Accessor> validationContextCache;
    
    private final Map<Package, ResourceBundleLocator> resourceBundleLocatorCache;
    
    public ValidatorContext(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
        
        this.constraintMetaDataManager = new ConstraintMetaDataManager();
        this.validationContextCache = new ConcurrentHashMap<>();
        this.resourceBundleLocatorCache = new ConcurrentHashMap<>();
    }
    
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }
    
    public ConstraintMetaDataManager getConstraintMetaDataManager() {
        return constraintMetaDataManager;
    }
    
    public ConstraintViolation createConstraintViolation(ValidationContext.Accessor context, ValueContext valueContext, ConstraintDescriptor descriptor) {
        Map<String, Object> initParameters = getConstraintParameters(descriptor.getConstraintValidator());
        String messageTemplate = descriptor.getMessageTemplate();
        String interpolatedMessage = interpolate(
                messageTemplate,
                valueContext.getCurrentValidatedValue(),
                valueContext.getCurrentBeanType(),
                descriptor,
                initParameters,
                Collections.<String, Object>emptyMap(),
                context.getResourceBundleLocator()
        );
        return ConstraintViolationImpl.forParameterValidation(
                    descriptor.getName(),
                    messageTemplate,
                    interpolatedMessage,
                    initParameters,
                    valueContext.getCurrentBeanType(),
                    valueContext.getCurrentBean(),
                    valueContext.getCurrentValidatedValue(),
                    descriptor);
    }

    private String interpolate(String messageTemplate,
            Object validatedValue,
            Class<?> rootBeanType,
            ConstraintDescriptor descriptor,
            Map<String, Object> initParameters,
            Map<String, Object> messageParameters,
            ResourceBundleLocator validationResourceBundleLocator) {
        MessageInterpolatorContext context = new MessageInterpolatorContext(
                initParameters,
                validatedValue,
                rootBeanType,
                messageParameters);
        try {
            return this.validatorFactory.getMessageInterpolator().interpolate(
                    messageTemplate, 
                    context,
                    validationResourceBundleLocator);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ValidationException("An exception occurred during message interpolation", e);
        }
    }
    
    public Map<String, Object> getConstraintParameters(ConstraintValidator constraintValidator) {
        Assert.notNull(constraintValidator, "constraintValidator cannot be null.");
        ConstraintMetaData metaData = constraintMetaDataManager.getConstraintMetaData(constraintValidator.getClass());
        return metaData.getInitParams(constraintValidator);
    }
    
    public ValidationContext.Accessor getOrCreateValidationContext(Class<? extends Validation> validationClass) {
        ValidationContext.Accessor context = validationContextCache.get(validationClass);
        if (context == null) {
            context = createAndInitializeValidationContext(validationClass);
            validationContextCache.put(validationClass, context);
        }
        return context;
    }
    
    private ValidationContext.Accessor createAndInitializeValidationContext(Class<? extends Validation> validationClass) {
        Assert.notNull(validationClass, "validation class must not be null");
        ValidationFactory validationFactory = this.validatorFactory.getValidationFactory();
        ValidatorConfig validatorConfig = this.validatorFactory.getValidatorConfig();
        
        Validation validation;
        try {
            validation = validationFactory.getValidation(validationClass);
        } catch (Exception e) {
            throw new ValidationException("get validation instance failed in " + validationClass, e);
        }
        // Create ValidationContext and so the default configuration initialization
        ValidationContext.Accessor context = new ValidationContextAccessor(validation, this);
        context.setFailFast(validatorConfig.isFailFast());
        context.setThrowException(validatorConfig.isThrowException());
        
        validation.initialize(context);
        
        if (context.getValidatorDelegate() == null) {
            context.setValidatorDelegate(validatorConfig.getValidatorDelegate());
        }
        if (context.getInvalidHandler() == null) {
            context.setInvalidHandler(validatorConfig.getInvalidHandler());
        }
        if (context.getResourceBundleLocator() == null) {
            ResourceBundleLocator defaultResourceBundleLocator = getDefaultResourceBundleLocator(validationClass.getPackage());
            context.setResourceBundleLocator(defaultResourceBundleLocator);
        }
        return context;
    }
    
    private ResourceBundleLocator getDefaultResourceBundleLocator(Package classPackage) {
        ResourceBundleLocator resourceBundleLocator = resourceBundleLocatorCache.get(classPackage);
        if (resourceBundleLocator == null) {
            String defaultBundleName =  classPackage.getName().concat(".ValidationMessages");
            resourceBundleLocator = new PlatformResourceBundleLocator(defaultBundleName);
            resourceBundleLocatorCache.put(classPackage, resourceBundleLocator);
        }
        return resourceBundleLocator;
    }
}
