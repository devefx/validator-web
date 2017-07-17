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

package org.devefx.validator.external.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.Validation;
import org.devefx.validator.internal.engine.DefaultValidationFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringValidationFactory extends DefaultValidationFactory implements ApplicationContextAware {

    private final Log logger = LogFactory.getLog(SpringValidationFactory.class);

    private ApplicationContext applicationContext;

    @Override
    public Validation getValidation(Class<? extends Validation> validationClass) throws Exception {
        try {
            return applicationContext.getBean(validationClass);
        } catch (BeansException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("No qualifying bean of type [" + validationClass.getName() + "] is defined");
            }
        }
        return super.getValidation(validationClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
