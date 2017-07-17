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

import org.devefx.validator.ValidationFactory;
import org.devefx.validator.Validator;
import org.devefx.validator.ValidatorConfig;
import org.devefx.validator.ValidatorFactory;
import org.devefx.validator.http.reader.FormHttpMessageReader;
import org.devefx.validator.http.reader.HttpMessageReader;
import org.devefx.validator.http.reader.json.FastJsonHttpMessageReader;
import org.devefx.validator.http.reader.json.MappingJackson2HttpMessageReader;
import org.devefx.validator.http.reader.multipart.MultipartFormHttpMessageReader;
import org.devefx.validator.http.reader.xml.MappingJackson2XmlHttpMessageReader;
import org.devefx.validator.messageinterpolation.MessageInterpolator;
import org.devefx.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidatorFactoryImpl implements ValidatorFactory {

    private static final boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", ValidatorFactoryImpl.class.getClassLoader()) &&
            ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", ValidatorFactoryImpl.class.getClassLoader());

    private static final boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", ValidatorFactoryImpl.class.getClassLoader());

    private static final boolean fastjsonPresent = ClassUtils.isPresent("com.alibaba.fastjson.JSON", ValidatorFactoryImpl.class.getClassLoader());
    
    private static final boolean commonsFileUploadPresent = ClassUtils.isPresent("org.apache.commons.fileupload.servlet.ServletFileUpload", ValidatorFactoryImpl.class.getClassLoader());
    
    private MessageInterpolator messageInterpolator;
    private List<HttpMessageReader<?>> messageReaders;
    private ValidationFactory validationFactory;
    private ValidatorConfig validatorConfig;
    private boolean initialized;
    
    public ValidatorFactoryImpl() {
    }

    public ValidatorFactoryImpl(ValidatorConfig validatorConfig) {
    	setValidatorConfig(validatorConfig);
    }

    @Override
    public MessageInterpolator getMessageInterpolator() {
		return this.messageInterpolator;
	}
    
    @Override
    public List<HttpMessageReader<?>> getMessageReaders() {
		return this.messageReaders;
	}
    
    @Override
    public ValidationFactory getValidationFactory() {
		return this.validationFactory;
	}
    
    @Override
    public ValidatorConfig getValidatorConfig() {
		return this.validatorConfig;
	}
    
    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
		this.messageInterpolator = messageInterpolator;
	}
    
    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
		this.messageReaders = messageReaders;
	}
    
    public void setValidationFactory(ValidationFactory validationFactory) {
		this.validationFactory = validationFactory;
	}
    
    public void setValidatorConfig(ValidatorConfig validatorConfig) {
    	Assert.notNull(validatorConfig, "validator config must not be null.");
		this.validatorConfig = validatorConfig;
	}
    
    public void init() {
    	if (this.initialized) {
    		return;
    	}
    	this.initialized = true;
    	if (this.validatorConfig == null) {
        	throw new IllegalArgumentException("validator config must not be null.");
        }
        if (this.messageInterpolator == null) {
            this.messageInterpolator = new ResourceBundleMessageInterpolator();
        }
        if (this.messageReaders == null) {
            this.messageReaders = new ArrayList<>();
            this.messageReaders.add(new FormHttpMessageReader());
            if (commonsFileUploadPresent) {
            	this.messageReaders.add(new MultipartFormHttpMessageReader());
            }
            if (jackson2Present) {
                this.messageReaders.add(new MappingJackson2HttpMessageReader());
            } else if (fastjsonPresent) {
            	this.messageReaders.add(new FastJsonHttpMessageReader());
            }
            if (jackson2XmlPresent) {
                this.messageReaders.add(new MappingJackson2XmlHttpMessageReader());
            }
        }
        if (this.validationFactory == null) {
            this.validationFactory = new DefaultValidationFactory();
        }
        if (this.validatorConfig.getInvalidHandler() == null) {
        	this.validatorConfig.setInvalidHandler(new DefaultInvalidHandler());
        }
        if (this.validatorConfig.getValidatorDelegate() == null) {
        	this.validatorConfig.setValidatorDelegate(new DefaultValidatorDelegate());
        }
    }

    @Override
    public Validator buildValidator() {
    	init();
    	return new ValidatorImpl(this);
    }
}
