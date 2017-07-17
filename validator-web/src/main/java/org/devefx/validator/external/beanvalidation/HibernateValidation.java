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

package org.devefx.validator.external.beanvalidation;

import org.devefx.validator.ConstraintViolation;
import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.ValidatorDelegate;
import org.devefx.validator.ValueContext;
import org.devefx.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HibernateValidation implements Validation, ValidatorDelegate {

    protected Validator validator;

    @Override
    public void initialize(ValidationContext context) {
        context.setValidatorDelegate(this);
        
        HibernateValidatorConfiguration configure = javax.validation.Validation.byProvider(HibernateValidator.class).configure();
        if (context instanceof ValidationContext.Accessor) {
        	configure.failFast(((ValidationContext.Accessor) context).isFailFast());
        }
        ValidatorFactory factory = configure.buildValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Override
    public List<ConstraintViolation> validate(ValueContext valueContext, ValidationContext.Accessor context, Class<?>... groups) {
        Set<javax.validation.ConstraintViolation<Object>> violations = validator.validate(valueContext.getCurrentBean(), groups);
        if (violations.size() > 0) {
            List<ConstraintViolation> constraintViolations = new ArrayList<>(violations.size());
            for (javax.validation.ConstraintViolation<Object> violation : violations) {
            	ConstraintViolation constraintViolation = ConstraintViolationImpl.forParameterValidation(violation.getPropertyPath().toString(),
            			violation.getMessageTemplate(), violation.getMessage(),
            			Collections.<String, Object> emptyMap(),
            			violation.getRootBeanClass(),
            			violation.getRootBean(),
            			violation.getInvalidValue(),
            			null);
                constraintViolations.add(constraintViolation);
            }
            return constraintViolations;
        }
        return Collections.emptyList();
    }
}
