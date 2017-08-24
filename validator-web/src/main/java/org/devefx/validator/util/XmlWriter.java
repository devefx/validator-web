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

package org.devefx.validator.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class XmlWriter {

    private static int convertDepth = 15;
    private static String timestampPattern = "yyyy-MM-dd HH:mm:ss";
    private static String datePattern = "yyyy-MM-dd";
    private static boolean ignoreNull = true;

    public static void setConvertDepth(int convertDepth) {
        if (convertDepth < 2)
            throw new IllegalArgumentException("convert depth can not less than 2.");
        XmlWriter.convertDepth = convertDepth;
    }

    public static void setTimestampPattern(String timestampPattern) {
        if (timestampPattern == null || "".equals(timestampPattern.trim()))
            throw new IllegalArgumentException("timestampPattern can not be blank.");
        XmlWriter.timestampPattern = timestampPattern;
    }

    public static void setDatePattern(String datePattern) {
        if (datePattern == null || "".equals(datePattern.trim()))
            throw new IllegalArgumentException("datePattern can not be blank.");
        XmlWriter.datePattern = datePattern;
    }

    public static void setIgnoreNull(boolean ignoreNull) {
        XmlWriter.ignoreNull = ignoreNull;
    }
    
    
    public static String beanToXml(Object bean) {
        return beanToXml(bean, convertDepth);
    }
    
    public static String beanToXml(Object bean, int depth) {
        if (bean == null) {
            return "</null>";
        }
        
        Class<?> beanClass = bean.getClass();
        String rootName = beanClass.getSimpleName();
        
        StringBuilder sb = new StringBuilder();
        appendBegin(rootName, sb);
        
        if (bean instanceof Map) {
            mapToXml((Map) bean, sb, depth);
        } else {
            mapToXml(MapUtils.beanToMap(bean), sb, depth);
        }
        
        appendEnd(rootName, sb);
        return sb.toString();
    }
    
    private static void mapToXml(Map<String, ?> map, StringBuilder sb, int depth) {
        if (map == null || (depth--) < 0) {
            return;
        }
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (ignoreNull && value == null) {
                continue;
            }
            appendBegin(key, sb);
            if (value != null) {
                if (value instanceof CharSequence ||
                        ClassUtils.isPrimitiveWrapper(value.getClass())) {
                    escape(value.toString(), sb);
                } else if (value instanceof Date) {
                    if (value instanceof java.sql.Timestamp)
                        sb.append(new SimpleDateFormat(timestampPattern).format(value));
                    if (value instanceof java.sql.Time)
                        sb.append(value);
                    else
                        sb.append(new SimpleDateFormat(datePattern).format(value));
                } else {
                    if (value instanceof Map) {
                        mapToXml((Map) value, sb, depth);
                    } else {
                        mapToXml(MapUtils.beanToMap(value), sb, depth);
                    }
                }
            }
            appendEnd(key, sb);
        }
    }
    
    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     */
    private static void escape(String s, StringBuilder sb) {
        for(int i=0; i<s.length(); i++){
            char ch = s.charAt(i);
            switch(ch){
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String str = Integer.toHexString(ch);
                        sb.append("\\u");
                        for(int k=0; k<4-str.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(str.toUpperCase());
                    }
                    else{
                        sb.append(ch);
                    }
            }
        }
    }
    
    private static void appendBegin(String s, StringBuilder sb) {
        sb.append("<").append(s).append(">");
    }
    
    private static void appendEnd(String s, StringBuilder sb) {
        sb.append("</").append(s).append(">");
    }
}
