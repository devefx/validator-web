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

package org.devefx.validator.web.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.web.View;

public abstract class AbstractView implements View {
    
    /** Logger that is available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    /** Default content type. Overridable as bean property. */
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    
    /** Initial size for the temporary output byte array (if any) */
    private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;
    
    private String contentType = DEFAULT_CONTENT_TYPE;
    
    /** Map of static attributes, keyed by attribute name (String) */
    private final Map<String, Object> staticAttributes = new LinkedHashMap<String, Object>();
    
    /**
     * Set the content type for this view.
     * Default is "text/html;charset=ISO-8859-1".
     * <p>May be ignored by subclasses if the view itself is assumed
     * to set the content type, e.g. in case of JSPs.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Return the content type for this view.
     */
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    /**
     * Set static attributes for this view from a Map. This allows to set
     * any kind of attribute values, for example bean references.
     * <p>"Static" attributes are fixed attributes that are specified in
     * the View instance configuration. "Dynamic" attributes, on the other hand,
     * are values passed in as part of the model.
     * <p>Can be populated with a "map" or "props" element in XML bean definitions.
     * @param attributes Map with name Strings as keys and attribute objects as values
     */
    public void setAttributesMap(Map<String, ?> attributes) {
        if (attributes != null) {
            for (Map.Entry<String, ?> entry : attributes.entrySet()) {
                addStaticAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Allow Map access to the static attributes of this view,
     * with the option to add or override specific entries.
     * <p>Useful for specifying entries directly, for example via
     * "attributesMap[myKey]". This is particularly useful for
     * adding or overriding entries in child view definitions.
     */
    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    /**
     * Add static data to this view, exposed in each view.
     * <p>"Static" attributes are fixed attributes that are specified in
     * the View instance configuration. "Dynamic" attributes, on the other hand,
     * are values passed in as part of the model.
     * <p>Must be invoked before any calls to {@code render}.
     * @param name the name of the attribute to expose
     * @param value the attribute value to expose
     * @see #render
     */
    public void addStaticAttribute(String name, Object value) {
        this.staticAttributes.put(name, value);
    }
    
    /**
     * Return the static attributes for this view. Handy for testing.
     * <p>Returns an unmodifiable Map, as this is not intended for
     * manipulating the Map but rather just for checking the contents.
     * @return the static attributes in this view
     */
    public Map<String, Object> getStaticAttributes() {
        return Collections.unmodifiableMap(this.staticAttributes);
    }
    
    /**
     * Prepares the view given the specified model.
     * Delegates to renderMergedOutputModel for the actual rendering.
     * @see #renderMergedOutputModel
     */
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("Rendering view with static attributes " + this.staticAttributes);
        }
        prepareResponse(request, response);
        renderMergedOutputModel(this.staticAttributes, request, response);
    }
    
    /**
     * Prepare the given response for rendering.
     * <p>The default implementation applies a workaround for an IE bug
     * when sending download content via HTTPS.
     * @param request current HTTP request
     * @param response current HTTP response
     */
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        if (generatesDownloadContent()) {
            response.setHeader("Pragma", "private");
            response.setHeader("Cache-Control", "private, must-revalidate");
        }
    }
    
    /**
     * Return whether this view generates download content
     * (typically binary content like PDF or Excel files).
     * <p>The default implementation returns {@code false}. Subclasses are
     * encouraged to return {@code true} here if they know that they are
     * generating download content that requires temporary caching on the
     * client side, typically via the response OutputStream.
     * @see #prepareResponse
     * @see javax.servlet.http.HttpServletResponse#getOutputStream()
     * @return
     */
    protected boolean generatesDownloadContent() {
        return false;
    }
    
    /**
     * Subclasses must implement this method to actually render the view.
     * <p>The first step will be preparing the request: In the JSP case,
     * this would mean setting model objects as request attributes.
     * The second step will be the actual rendering of the view,
     * for example including the JSP via a RequestDispatcher.
     * @param model combined output Map (never {@code null}),
     * with dynamic values taking precedence over static attributes
     * @param request current HTTP request
     * @param response current HTTP response
     * @throws Exception if rendering failed
     */
    protected abstract void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Expose the model objects in the given map as request attributes.
     * Names will be taken from the model Map.
     * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
     * @param model Map of model objects to expose
     * @param request current HTTP request
     */
    protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String modelName = entry.getKey();
            Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
                if (logger.isDebugEnabled()) {
                    logger.debug("Added model object '" + modelName + "' of type [" + modelValue.getClass().getName() +
                            "] to request in view");
                }
            }
            else {
                request.removeAttribute(modelName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Removed model object '" + modelName +
                            "' from request in view");
                }
            }
        }
    }
    
    /**
     * Create a temporary OutputStream for this view.
     * <p>This is typically used as IE workaround, for setting the content length header
     * from the temporary stream before actually writing the content to the HTTP response.
     * @return
     */
    protected ByteArrayOutputStream createTemporaryOutputStream() {
        return new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
    }

    /**
     * Write the given temporary OutputStream to the HTTP response.
     * @param response current HTTP response
     * @param baos the temporary OutputStream to write
     * @throws IOException if writing/flushing failed
     */
    protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
        // Write content type and also length (determined via byte array).
        response.setContentType(getContentType());
        response.setContentLength(baos.size());

        // Flush byte array to servlet output stream.
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }
    
    /**
     * Set the content type of the response to the configured
     * {@link #setContentType(String) content type}
     */
    protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(getContentType());
    }
}
