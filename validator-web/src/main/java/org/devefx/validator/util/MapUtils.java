package org.devefx.validator.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    
    public static Map<String, ?> beanToMap(Object model) {
        return beanToMap(model, true);
    }
    
    public static Map<String, ?> beanToMap(Object model, boolean ignoreNull) {
        Map<String, Object> map = new HashMap<>();
        for (Method method : model.getClass().getMethods()) {
            String attrName = null;
            String methodName = method.getName();
            int indexOfGet = methodName.indexOf("get");
            if (indexOfGet == 0 && methodName.length() > 3) {   // getter
                attrName = methodName.substring(3);
                if (attrName.equals("Class")) {
                    attrName = null;
                }
            } else {
                int indexOfIs = methodName.indexOf("is");
                if (indexOfIs == 0 && methodName.length() > 2) {    // is
                    attrName = methodName.substring(2);
                }
            }
            if (attrName != null && method.getParameterTypes().length == 0) {
                try {
                    Object value = method.invoke(model);
                    if (!ignoreNull || value != null) {
                        char firstChar = attrName.charAt(0);
                        if (firstChar >= 'A' && firstChar <= 'Z') {
                            char[] arr = attrName.toCharArray();
                            arr[0] += ('a' - 'A');
                            attrName = new String(arr);
                        }
                        map.put(attrName, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return map;
    }
}
