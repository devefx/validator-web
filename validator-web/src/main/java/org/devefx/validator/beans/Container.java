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

package org.devefx.validator.beans;

import java.util.Map;

public interface Container {

	Object getBean(String name) throws BeansException;
	
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	
	<T> T getBean(Class<T> requiredType) throws BeansException;
	
	String[] getBeanNamesForType(Class<?> type);
	
	<T> Map<String, T> getBeansOfType(Class<T> type);
	
	boolean containsBean(String name);
	
	String getProperty(String name);
	
}
