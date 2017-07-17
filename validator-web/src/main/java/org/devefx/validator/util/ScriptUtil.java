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

public final class ScriptUtil {
	
	/**
	 * Prevent instantiation
	 */
	private ScriptUtil() {
	}
    
    /**
     * Determines whether the supplied string is a valid script name to use for
     * remoted classes.
     *
     * @param test
     * @return true if the string is a valid script name
     */
    public static boolean isValidScriptName(String test) {
        return isSafeHierarchicalIdentifierInBrowser(test);
    }
    
    /**
     * Tests if a string contains only characters that will allow safe use
     * inside html element attributes and url:s, and is a valid hierarchical
     * identifier wrt to dot ("package") segments.
     *
     * @param test
     * @return true if string is safe
     */
    public static boolean isSafeHierarchicalIdentifierInBrowser(String test) {
        if (test.endsWith("/")) {
            return false;
        }
        String[] segments = test.split("\\.");
        for (String segment : segments) {
            if (segment.equals("")) {
                return false;
            }
            if (!isSafeIdentifierInBrowser(segment)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tests if a string contains only characters that will allow safe use
     * inside html element attributes and url:s.
     *
     * @param test
     * @return true if string is safe
     */
    public static boolean isSafeIdentifierInBrowser(String test) {
        for(int i=0; i<test.length(); i++) {
            char ch = test.charAt(i);

            // Disallow characters that may change parsing mode in HTML
            if ("<>&'\"".indexOf(ch) >= 0) {
                return false;
            }

            // Disallow characters that may break URL handling
            //   ;  delimits path parameters
            //   ?  delimits query string
            //   #  delimits anchor string
            if (";?#%".indexOf(ch) >= 0) {
                return false;
            }

            // Disallow characters outside the normal-characters ascii range that
            // are not letters or digits
            if ((ch < 32 || 126 < ch) && !Character.isLetterOrDigit(ch)) {
                return false;
            }
        }
        return true;
    }
}
