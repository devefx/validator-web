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

import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class ConfigPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		
		if (isEmpty(beanFactory.getBeansOfType(ValidatorController.class))) {
			BeanDefinition beanDefinition = new RootBeanDefinition(ValidatorController.class);
			String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
			registry.registerBeanDefinition(beanName, beanDefinition);
		}
		
		if (isEmpty(beanFactory.getBeansOfType(ValidatorHandlerMapping.class))) {
			BeanDefinition beanDefinition = new RootBeanDefinition(ValidatorHandlerMapping.class);
			String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
			registry.registerBeanDefinition(beanName, beanDefinition);
		}
	}
	
}
