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

package org.devefx.validator.http.reader;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devefx.validator.http.MediaType;

public interface HttpMessageReader<T> {

    /**
     * Indicates whether the given class can be read by this converter.
     * @param clazz the class to test for readability
     * @param mediaType the media type to read, can be {@code null} if not specified.
     * Typically the value of a {@code Content-Type} header.
     * @return {@code true} if readable; {@code false} otherwise
     */
    boolean canRead(Class<?> clazz, MediaType mediaType);
    
    /**
     * Return the list of {@link MediaType} objects supported by this converter.
     * @return the list of supported media types
     */
    List<MediaType> getSupportedMediaTypes();
    
    /**
     * Read an object of the given type form the given input message, and returns it.
     * @param clazz the type of object to return. This type must have previously been passed to the
     * {@link #canRead canRead} method of this interface, which must have returned {@code true}.
     * @param request the HTTP request object
     * @return the converted object
     * @throws IOException in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    T read(Class<? extends T> clazz, HttpServletRequest request)
            throws IOException, HttpMessageNotReadableException;
}
