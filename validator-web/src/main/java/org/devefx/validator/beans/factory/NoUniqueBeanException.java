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

package org.devefx.validator.beans.factory;

import java.util.Arrays;
import java.util.Collection;

import org.devefx.validator.beans.BeansException;
import org.devefx.validator.util.StringUtils;

public class NoUniqueBeanException extends BeansException {

    private static final long serialVersionUID = -7811666048083937858L;

    private Class<?> beanType;
    
    private int numberOfBeansFound;

    /**
     * Create a new {@code NoUniqueBeanException}.
     * @param type required type of the non-unique bean
     * @param numberOfBeansFound the number of matching beans
     * @param message detailed message describing the problem
     */
    public NoUniqueBeanException(Class<?> type, int numberOfBeansFound, String message) {
        super("No bean named '" + type.getName() + "' is defined");
        this.beanType = type;
        this.numberOfBeansFound = numberOfBeansFound;
    }

    /**
     * Create a new {@code NoUniqueBeanException}.
     * @param type required type of the non-unique bean
     * @param beanNamesFound the names of all matching beans (as a Collection)
     */
    public NoUniqueBeanException(Class<?> type, Collection<String> beanNamesFound) {
        this(type, beanNamesFound.size(), "expected single matching bean but found " + beanNamesFound.size() + ": " +
                StringUtils.collectionToCommaDelimitedString(beanNamesFound));
    }

    /**
     * Create a new {@code NoUniqueBeanException}.
     * @param type required type of the non-unique bean
     * @param beanNamesFound the names of all matching beans (as an array)
     */
    public NoUniqueBeanException(Class<?> type, String... beanNamesFound) {
        this(type, Arrays.asList(beanNamesFound));
    }

    /**
     * Return the required type of the missing bean, if it was a lookup <em>by type</em> that failed.
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }
    
    /**
     * Return the number of beans found when only one matching bean was expected.
     * For a NoUniqueBeanException, this will usually be higher than 1.
     * @see #getBeanType()
     */
    public int getNumberOfBeansFound() {
        return this.numberOfBeansFound;
    }
}
