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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.Validator;

public abstract class ThreadContext {
	
    /**
     * Private internal log instance.
     */
    private static final Log log = LogFactory.getLog(ThreadContext.class);
    
    public static final String VALIDATOR_KEY = ThreadContext.class.getName() + "_VALIDATOR_KEY";
    
    public static final String MODEL_KEY = ThreadContext.class.getName() + "_MODEL_KEY";

	private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<>();
	
    /**
     * Default no-argument constructor.
     */
    protected ThreadContext() {
    }
    
    /**
     * Returns the ThreadLocal Map. This Map is used internally to bind objects
     * to the current thread by storing each object under a unique key.
     *
     * @return the map of bound resources
     */
    public static Map<Object, Object> getResources() {
        if (resources.get() == null){
            return Collections.emptyMap();
        } else {
            return new HashMap<Object, Object>(resources.get());
        }
    }
	
    /**
     * Allows a caller to explicitly set the entire resource map.  This operation overwrites everything that existed
     * previously in the ThreadContext - if you need to retain what was on the thread prior to calling this method,
     * call the {@link #getResources()} method, which will give you the existing state.
     *
     * @param newResources the resources to replace the existing {@link #getResources() resources}.
     */
    public static void setResources(Map<Object, Object> newResources) {
        if (newResources == null || newResources.isEmpty()) {
            return;
        }
        ensureResourcesInitialized();
        resources.get().clear();
        resources.get().putAll(newResources);
    }
    
    /**
     * Returns the value bound in the {@code ThreadContext} under the specified {@code key}, or {@code null} if there
     * is no value for that {@code key}.
     *
     * @param key the map key to use to lookup the value
     * @return the value bound in the {@code ThreadContext} under the specified {@code key}, or {@code null} if there
     *         is no value for that {@code key}.
     */
    private static Object getValue(Object key) {
        Map<Object, Object> perThreadResources = resources.get();
        return perThreadResources != null ? perThreadResources.get(key) : null;
    }
    
    private static void ensureResourcesInitialized(){
        if (resources.get() == null) {
        	resources.set(new HashMap<Object, Object>());
        }
    }
    
    /**
     * Returns the object for the specified <code>key</code> that is bound to
     * the current thread.
     *
     * @param key the key that identifies the value to return
     * @return the object keyed by <code>key</code> or <code>null</code> if
     *         no value exists for the specified <code>key</code>
     */
    public static Object get(Object key) {
        if (log.isTraceEnabled()) {
            String msg = "get() - in thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }

        Object value = getValue(key);
        if ((value != null) && log.isTraceEnabled()) {
            String msg = "Retrieved value of type [" + value.getClass().getName() + "] for key [" +
                    key + "] " + "bound to thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }
        return value;
    }
    
    /**
     * Binds <tt>value</tt> for the given <code>key</code> to the current thread.
     * <p/>
     * <p>A <tt>null</tt> <tt>value</tt> has the same effect as if <tt>remove</tt> was called for the given
     * <tt>key</tt>, i.e.:
     * <p/>
     * <pre>
     * if ( value == null ) {
     *     remove( key );
     * }</pre>
     *
     * @param key   The key with which to identify the <code>value</code>.
     * @param value The value to bind to the thread.
     * @throws IllegalArgumentException if the <code>key</code> argument is <tt>null</tt>.
     */
    public static void put(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        if (value == null) {
            remove(key);
            return;
        }

        ensureResourcesInitialized();
        resources.get().put(key, value);

        if (log.isTraceEnabled()) {
            String msg = "Bound value of type [" + value.getClass().getName() + "] for key [" +
                    key + "] to thread " + "[" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }
    }
    
    /**
     * Unbinds the value for the given <code>key</code> from the current
     * thread.
     *
     * @param key The key identifying the value bound to the current thread.
     * @return the object unbound or <tt>null</tt> if there was nothing bound
     *         under the specified <tt>key</tt> name.
     */
    public static Object remove(Object key) {
        Map<Object, Object> perThreadResources = resources.get();
        Object value = perThreadResources != null ? perThreadResources.remove(key) : null;

        if ((value != null) && log.isTraceEnabled()) {
            String msg = "Removed value of type [" + value.getClass().getName() + "] for key [" +
                    key + "]" + "from thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }

        return value;
    }
    
    /**
     * {@link ThreadLocal#remove Remove}s the underlying {@link ThreadLocal ThreadLocal} from the thread.
     * <p/>
     * This method is meant to be the final 'clean up' operation that is called at the end of thread execution to
     * prevent thread corruption in pooled thread environments.
     *
     */
    public static void remove() {
        resources.remove();
    }
    
    /**
     * Convenience method that simplifies retrieval of the application's Validator instance from the current
     * thread. If there is no Validator bound to the thread (probably because framework code did not bind it
     * to the thread), this method returns <tt>null</tt>.
     * <p/>
     * It is merely a convenient wrapper for the following:
     * <p/>
     * <code>return (Validator)get( VALIDATOR_KEY );</code>
     * <p/>
     * This method only returns the bound value if it exists - it does not remove it
     * from the thread.  To remove it, one must call {@link #unbindValidator() unbindValidator()} instead.
     * 
     * @return
     */
    public static Validator getValidator() {
    	return (Validator) get(VALIDATOR_KEY);
    }
    
    /**
     * Convenience method that simplifies binding the application's Validator instance to the ThreadContext.
     * @param validator
     */
    public static void bind(Validator validator) {
        if (validator != null) {
            put(VALIDATOR_KEY, validator);
        }
    }
    
    /**
     * Convenience method that simplifies removal of the application's Validator instance from the thread.
     * @return
     */
    public static Validator unbindValidator() {
        return (Validator) remove(VALIDATOR_KEY);
    }
    
    public static Object getModel() {
    	return get(MODEL_KEY);
    }
    
    public static void bindModel(Object model) {
    	if (model != null) {
    		put(MODEL_KEY, model);
    	}
    }
    
    public static Object unbindModel() {
    	return remove(MODEL_KEY);
    }
    
    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {
        @SuppressWarnings({"unchecked"})
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            } else {
                return null;
            }
        }
    }
}
