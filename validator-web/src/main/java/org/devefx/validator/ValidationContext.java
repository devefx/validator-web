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
import java.util.List;
import java.util.Set;

import org.devefx.validator.groups.Default;
import org.devefx.validator.internal.resourceloading.ResourceBundleLocator;

public interface ValidationContext {
	
	Set<Class<?>> DEFAULT_GROUPS = Collections.<Class<?>>singleton(Default.class);

    void setFailFast(boolean failFast);

    void setThrowException(boolean throwException);

    void setResourceBundleLocator(ResourceBundleLocator resourceBundleLocator);
    
    void setValidatorDelegate(ValidatorDelegate delegate);

    void setInvalidHandler(InvalidHandler invalidHandler);

    void constraint(String name, String message, ConstraintValidator constraintValidator, Class<?>... groups);

    void constraint(String name, ConstraintValidator constraintValidator, Class<?>... groups);
    
    interface Accessor extends ValidationContext {
    	
    	boolean isFailFast();
    	
    	boolean isThrowException();
    	
    	ResourceBundleLocator getResourceBundleLocator();
    	
    	ValidatorDelegate getValidatorDelegate();
    	
    	InvalidHandler getInvalidHandler();
    	
    	List<ConstraintDescriptor> getConstraintDescriptors();
    	
    	Validation getValidation();
    	
    	ValidatorContext getValidatorContext();
    }
}
