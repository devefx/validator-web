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

package org.devefx.validator.internal.metadata;

import org.devefx.validator.internal.util.ConcurrentReferenceHashMap;

import static org.devefx.validator.internal.util.ConcurrentReferenceHashMap.Option.IDENTITY_COMPARISONS;
import static org.devefx.validator.internal.util.ConcurrentReferenceHashMap.ReferenceType.SOFT;

import java.util.EnumSet;

public class ConstraintMetaDataManager {
    /**
     * The default initial capacity for this cache.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this cache.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The default concurrency level for this cache.
     */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * Used to cache the constraint meta data for validated entities
     */
    private final ConcurrentReferenceHashMap<Class<?>, ConstraintMetaData> constraintMetaDataCache;

    public ConstraintMetaDataManager() {
        this.constraintMetaDataCache = new ConcurrentReferenceHashMap<>(
                DEFAULT_INITIAL_CAPACITY,
                DEFAULT_LOAD_FACTOR,
                DEFAULT_CONCURRENCY_LEVEL,
                SOFT,
                SOFT,
                EnumSet.of(IDENTITY_COMPARISONS)
        );
    }

    public ConstraintMetaData getConstraintMetaData(Class<?> constraintClass) {
        return getOrCreateConstraintMetaData(constraintClass);
    }

    public void clear() {
        constraintMetaDataCache.clear();
    }

    public int numberOfCachedConstraintMetaDataInstances() {
        return constraintMetaDataCache.size();
    }

    private ConstraintMetaData createConstraintMetaData(Class<?> constraintClass) {
        return new ConstraintMetaDataImpl(constraintClass);
    }

    private ConstraintMetaData getOrCreateConstraintMetaData(Class<?> constraintClass) {
        ConstraintMetaData constraintMetaData = constraintMetaDataCache.get(constraintClass);

        if (constraintMetaData == null) {
            constraintMetaData = createConstraintMetaData(constraintClass);

            final ConstraintMetaData cachedConstraintMetaData = constraintMetaDataCache.putIfAbsent(
                    constraintClass,
                    constraintMetaData
            );
            if (cachedConstraintMetaData != null) {
                constraintMetaData = cachedConstraintMetaData;
            }
        }
        return constraintMetaData;
    }
}
