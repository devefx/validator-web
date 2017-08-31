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
}
