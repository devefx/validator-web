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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.factory.DisposableBean;
import org.devefx.validator.beans.factory.InitializingBean;
import org.devefx.validator.beans.factory.InjectBeanException;
import org.devefx.validator.beans.factory.NameAware;
import org.devefx.validator.beans.factory.NoSuchBeanException;
import org.devefx.validator.beans.factory.NoUniqueBeanException;
import org.devefx.validator.beans.factory.annotation.Inject;
import org.devefx.validator.beans.factory.annotation.Value;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.StringUtils;

public class DefaultContainer implements Container {
	public static final String[] EMPTY_ARRAY = new String[0];

	protected final Log log = LogFactory.getLog(DefaultContainer.class);
	
	protected Map<String, Object> beans = new LinkedHashMap<>();
	
	protected Properties properties = new Properties();
	
	@Override
	public Object getBean(String name) throws BeansException {
		Object bean = beans.get(name);
    	if (bean == null) {
    		throw new NoSuchBeanException(name);
    	}
    	return bean;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		Object bean = getBean(name);
        if (bean == null) {
        	throw new NoSuchBeanException(name);
        }
        Assert.isTrue(requiredType.isAssignableFrom(bean.getClass()),
                "Bean with id [" + name + "] is not of the required type [" + requiredType.getName() + "].");
        return (T) bean;
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		String[] beanNames = getBeanNamesForType(requiredType);
		if (beanNames.length == 1) {
    		return getBean(beanNames[0], requiredType);
    	} else if (beanNames.length > 1) {
    		throw new NoUniqueBeanException(requiredType, beanNames);
    	}
		throw new NoSuchBeanException(requiredType);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		Assert.notNull(type, "type mush not be null");
		List<String> names = new ArrayList<String>();
    	for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				names.add(entry.getKey());
			}
		}
    	return names.toArray(EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) {
		Assert.notNull(type, "type must not be null");
    	Map<String, T> beansOfType = new LinkedHashMap<String, T>();
    	for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
    		if (type.isInstance(entry.getValue())) {
    			beansOfType.put(entry.getKey(), (T) entry.getValue());
			}
    	}
    	return beansOfType;
	}
	
	@Override
	public boolean containsBean(String name) {
		return this.beans.containsKey(name);
	}
	
	@Override
	public String getProperty(String name) {
		return properties.getProperty(name);
	}
	
	public void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}
	
	public void addBean(String name, Object bean) {
		Assert.notNull(name, "name must not be null");
		if (bean == null) {
			removeBean(name);
		} else {
			if (bean instanceof NameAware) {
				((NameAware)bean).setName(name);
			}
			Object previousBean = this.beans.put(name, bean);
			if (previousBean != null) {
				callDestroyBean(previousBean);
			}
		}
	}
	
	public void addBeans(Map<String, Object> beans) {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			addBean(entry.getKey(), entry.getValue());
		}
	}
	
	public void removeBean(String name) {
		Object previousBean = this.beans.remove(name);
		if (previousBean != null) {
			callDestroyBean(previousBean);
		}
	}
	
	public int size() {
		return this.beans.size();
	}
	
	public void clear() {
		this.beans.clear();
	}
	
	public void setupFinished() {
		for (Object bean : this.beans.values()) {
			initializeBean(bean);
		}
		callInitializingBeans();
	}
	
	protected void initializeBean(Object bean) {
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(bean)) {
			Method writeMethod = pd.getWriteMethod();
			if (writeMethod == null) {
				continue;
			}
			injectBean(bean, pd);
			injectValue(bean, pd);
		}
	}
	
	protected void injectBean(Object bean, PropertyDescriptor pd) {
		Method writeMethod = pd.getWriteMethod();
		Inject inject = writeMethod.getAnnotation(Inject.class);
		if (inject == null) {
			return;
		}
		Class<?> propertyType = pd.getPropertyType();
		String name = StringUtils.clean(inject.value());
		Object value;
		try {
			value = (name != null ? getBean(name) :
				getBean(propertyType));
		} catch (NoSuchBeanException e) {
			value = null;
		}
		try {
			if (value == null && inject.required()) {
				String msg = "expected at least 1 bean which qualifies as inject candidate for this dependency. " +
						"Dependency annotations: {@org.devefx.validator.beans.factory.Inject(required=true)}";
				throw new NoSuchBeanException(propertyType, msg);
			}
			value = propertyType.cast(value);
			if (!writeMethod.isAccessible()) {
				writeMethod.setAccessible(true);
			}
			writeMethod.invoke(bean, value);
		} catch (Exception e) {
			throw new InjectBeanException("Could not inject method: " + writeMethod + "; nested exception is " + e, e);
		}
	}
	
	protected void injectValue(Object bean, PropertyDescriptor pd) {
		Method writeMethod = pd.getWriteMethod();
		Value valueAnnotate = writeMethod.getAnnotation(Value.class);
		if (valueAnnotate == null) {
			return;
		}
		String value = valueAnnotate.value();
		if (value.startsWith("${") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
			value = getProperty(value);
		}
		try {
			BeanUtils.setProperty(bean, pd.getName(), value);
		} catch (Exception e) {
			throw new InjectBeanException("Could not inject value: " + writeMethod + "; nested exception is " + e, e);
		}
	}

	protected void callInitializingBeans() {
		for (Object bean : this.beans.values()) {
			if (bean instanceof InitializingBean) {
				((InitializingBean)bean).afterSetup(this);
			}
		}
	}
	
	protected void callDestroyBean(Object bean) {
		if (bean instanceof DisposableBean) {
			try {
				((DisposableBean) bean).destroy();
			} catch (Exception e) {
				log.fatal("destroy failed", e);
			}
		}
	}
}
