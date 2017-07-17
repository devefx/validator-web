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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.ConstraintViolation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.ValidatorContext;
import org.devefx.validator.ValidatorDelegate;
import org.devefx.validator.ValueContext;
import org.devefx.validator.groups.Default;
import org.devefx.validator.internal.engine.groups.Group;
import org.devefx.validator.internal.engine.groups.ValidationOrder;
import org.devefx.validator.internal.engine.groups.ValidationOrderGenerator;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.MultiValueMap;

public class DefaultValidatorDelegate implements ValidatorDelegate {
	
	private static final Log log = LogFactory.getLog(DefaultValidatorDelegate.class);

	/**
	 * The default group array used in case any of the validate methods is called without a group.
	 */
	private static final Collection<Class<?>> DEFAULT_GROUPS = Collections.<Class<?>>singletonList(Default.class);
	
	/**
	 * Used to resolve the group execution order for a validate call.
	 */
	private final transient ValidationOrderGenerator validationOrderGenerator;
	
	public DefaultValidatorDelegate() {
		this.validationOrderGenerator = new ValidationOrderGenerator();
	}
	
	@Override
	public List<ConstraintViolation> validate(ValueContext valueContext, ValidationContext.Accessor context, Class<?>... groups) {
		List<ConstraintViolation> failingConstraintViolations = new ArrayList<>();
		
		ValidationOrder validationOrder = determineGroupValidationOrder(groups);
		
		Iterator<Group> groupIterator = validationOrder.getGroupIterator();
		while (groupIterator.hasNext()) {
			Group group = groupIterator.next();
			valueContext.setCurrentGroup(group.getDefiningClass());
			validateConstraintsForCurrentGroup(context, valueContext, failingConstraintViolations);
			if (shouldFailFast(context, failingConstraintViolations)) {
				return failingConstraintViolations;
			}
		}
		return failingConstraintViolations;
	}
	
	protected void validateConstraintsForCurrentGroup(ValidationContext.Accessor context, ValueContext valueContext,
			List<ConstraintViolation> failingConstraintViolations) {

		for (ConstraintDescriptor descriptor : context.getConstraintDescriptors()) {
			if (isValidationRequired(valueContext, descriptor)) {
				if (valueContext.getCurrentBean() != null) {
					Object valueToValidate = getBeanProperty(
							valueContext.getCurrentBean(),
							descriptor.getName());
					valueContext.setCurrentValidatedValue(valueToValidate);
				}
				ConstraintViolation constraintViolation = validateConstraint(context, valueContext, descriptor);
				if (constraintViolation != null) {
					failingConstraintViolations.add(constraintViolation);
				}
			}
			if (shouldFailFast(context, failingConstraintViolations)) {
				return;
			}
		}
	}
	
	protected ConstraintViolation validateConstraint(ValidationContext.Accessor context, ValueContext valueContext, 
			ConstraintDescriptor descriptor) {
		boolean isValid = false;
		try {
			ConstraintValidator validator = descriptor.getConstraintValidator();
			isValid = validator.isValid(valueContext.getCurrentValidatedValue());
		} catch (RuntimeException e) {
			if (context.isThrowException()) {
				throw new RuntimeException("Unexpected exception during isValid call.", e);
			}
		}
		if (!isValid) {
			ValidatorContext validatorContext = context.getValidatorContext();
			return validatorContext.createConstraintViolation(context, valueContext, descriptor);
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked" })
	protected Object getBeanProperty(Object bean, String name) {
		if (bean instanceof Map) {
			if (bean instanceof MultiValueMap) {
				MultiValueMap<String, ?> valueMap = (MultiValueMap<String, ?>) bean;
				List<?> values = valueMap.get(name);
				if (values == null || values.isEmpty()) {
					return null;
				} else if (values.size() == 1) {
					return values.get(0);
				}
				return values;
			}
			return ((Map<String, ?>)bean).get(name);
		}
		try {
			return PropertyUtils.getProperty(bean, name);
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Property '" + name + "' does not exist for object of " +
	                    "type " + bean.getClass().getName() + ".");
			}
		}
		return null;
	}
	
	protected boolean isValidationRequired(ValueContext valueContext, ConstraintDescriptor descriptor) {
		if (descriptor.getGroups().contains(valueContext.getCurrentGroup())) {
			return true;
		}
		return false;
	}

	protected boolean shouldFailFast(ValidationContext.Accessor context, List<ConstraintViolation> failingConstraintViolations) {
		return context.isFailFast() && !failingConstraintViolations.isEmpty();
	}
	
	protected ValidationOrder determineGroupValidationOrder(Class<?>[] groups) {
		Assert.notNull(groups, "null passed as group name.");
		for (Class<?> clazz : groups) {
			if ( clazz == null ) {
				throw new IllegalArgumentException("null passed as group name.");
			}
		}
		Collection<Class<?>> resultGroups;
		// if no groups is specified use the default
		if (groups.length == 0) {
			resultGroups = DEFAULT_GROUPS;
		} else {
			resultGroups = Arrays.asList(groups);
		}
		return validationOrderGenerator.getValidationOrder(resultGroups);
	}
}
