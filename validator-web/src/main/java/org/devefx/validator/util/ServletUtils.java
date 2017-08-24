package org.devefx.validator.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void extractUrlParams(MultiValueMap multiValueMap, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry: parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                multiValueMap.add(key, value);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static MultiValueMap extractUrlParams(HttpServletRequest request) {
        MultiValueMap valueMap = new LinkedMultiValueMap<>();
        extractUrlParams(valueMap, request);
        return valueMap;
    }
}
