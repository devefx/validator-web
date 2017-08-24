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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.devefx.validator.beans.DefaultContainer;
import org.devefx.validator.util.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

public class SpringContainer extends DefaultContainer {

    private final BeanFactory beanFactory;
    
    public SpringContainer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        Object bean;
        try {
            bean = beanFactory.getBean(name);
        } catch (BeansException e) {
            bean = super.getBean(name);
        }
        return bean;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        T bean;
        try {
            bean = beanFactory.getBean(name, requiredType);
        } catch (BeansException e) {
            bean = super.getBean(name, requiredType);
        }
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        T bean;
        try {
            bean = beanFactory.getBean(requiredType);
        } catch (BeansException e) {
            bean = super.getBean(requiredType);
        }
        return bean;
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        String[] beanNames = super.getBeanNamesForType(type);
        if (beanFactory instanceof ListableBeanFactory) {
            String[] array = ((ListableBeanFactory)beanFactory).getBeanNamesForType(type);
            if (!ObjectUtils.isEmpty(array)) {
                Set<String> uniqNames = new HashSet<String>(beanNames.length);
                uniqNames.addAll(Arrays.asList(beanNames));
                uniqNames.addAll(Arrays.asList(array));
                beanNames = uniqNames.toArray(EMPTY_ARRAY);
            }
        }
        return beanNames;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> beans = super.getBeansOfType(type);
        if (beanFactory instanceof ListableBeanFactory) {
            beans.putAll(
                    ((ListableBeanFactory)beanFactory).getBeansOfType(type));
        }
        return beans;
    }

    @Override
    public boolean containsBean(String name) {
        return beanFactory.containsBean(name) ||
                super.containsBean(name);
    }
    
    @Override
    public String getProperty(String name) {
        Collection<InitPropertyPlaceholderConfigurer> configurers =
                getBeansOfType(InitPropertyPlaceholderConfigurer.class).values();
        for (InitPropertyPlaceholderConfigurer configurer : configurers) {
            String propValue = configurer.getProperty(name);
            if (propValue != null) {
                return propValue;
            }
        }
        return super.getProperty(name);
    }
}
