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

package org.devefx.validator.internal.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.MultiValueMap;

public class BeanReader {
    
    private static final Log log = LogFactory.getLog(BeanReader.class);

    private final Object bean;
    
    public BeanReader(Object bean) {
        Assert.notNull(bean, "bean must not be null.");
        this.bean = bean;
    }
    
    @SuppressWarnings({ "unchecked" })
    public Object getProperty(String name) {
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setProperty(String name, Object value) {
    	if (bean instanceof Map) {
    		if (bean instanceof MultiValueMap) {
    			MultiValueMap valueMap = (MultiValueMap) bean;
    			valueMap.set(name, value);
    			return;
    		}
    		Map map = (Map) bean;
    		map.put(name, value);
    		return;
    	}
    	try {
    		PropertyUtils.setProperty(bean, name, value);
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
                log.warn("Property '" + name + "' can't setter for object of " +
                        "type " + bean.getClass().getName() + ".");
            }
		}
    }
}
