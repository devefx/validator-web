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

package org.devefx.validator.spring;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

public class ValidatorHandlerMapping extends SimpleUrlHandlerMapping {
    
    @Override
    public void initApplicationContext() throws BeansException {
        Collection<ValidatorController> validatorControllers = getApplicationContext()
                .getBeansOfType(ValidatorController.class).values();
        Assert.notEmpty(validatorControllers, "No Validator Controller bean definition found.");
        ValidatorController validatorController = validatorControllers.iterator().next();
        validatorController.setupUrlMapping(this);
        super.initApplicationContext();
    }
}
