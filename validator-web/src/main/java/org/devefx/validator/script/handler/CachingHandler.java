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

package org.devefx.validator.script.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.factory.annotation.Value;
import org.devefx.validator.http.HttpConstants;
import org.devefx.validator.internal.engine.messageinterpolation.LocalizedMessage;
import org.devefx.validator.script.Handler;

public abstract class CachingHandler implements Handler {
    
    /** Logger that is available to subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /** Default content type. Overridable as bean property. */
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    
    /** Do we ignore all the Last-Modified/ETags blathering? */
    private boolean ignoreLastModified = false;
    
    /** The cache time unit of seconds */
    private int periodCacheableTime = -1;
    
    private String contentType = DEFAULT_CONTENT_TYPE;
    
    private String path;
    
    /** We cache the script output for speed */
    private final Map<LocalizedMessage, CachedResource> resourceCache = new HashMap<LocalizedMessage, CachedResource>();
    
    /**
     * @param ignoreLastModified The ignoreLastModified to set.
     */
    public void setIgnoreLastModified(boolean ignoreLastModified) {
        this.ignoreLastModified = ignoreLastModified;
    }
    
    /**
     * @param periodCacheableTime The cache time unit of seconds
     */
    @Value("${periodCacheableTime}")
    public void setPeriodCacheableTime(int periodCacheableTime) {
        this.periodCacheableTime = periodCacheableTime;
    }
    
    /**
     * Return the cache time unit of seconds
     */
    public int getPeriodCacheableTime() {
        return periodCacheableTime;
    }
    
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
    public String getContentType() {
        return this.contentType;
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        long lastModified = getLastModifiedTime();
        
        // Is the browser in sync with our latest?
        if (isUpToDate(request, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        
        // Is our cache up to date WRT the real resource?
        CachedResource resource;
        synchronized (resourceCache) {
            String url = getCachingKey(request);
            
            Locale locale = getLocale(request);
            
            LocalizedMessage localizedMessage = new LocalizedMessage(url, locale);
            resource = resourceCache.get(localizedMessage);
            
            if (resource == null || lastModified > resource.lastModifiedTime) {
                if (log.isDebugEnabled()) {
                    if (resource == null) {
                        log.debug("Generating contents for " + url + ". It is not currently cached." );
                    } else  {
                        log.debug("Generating contents for " + url + ". Resource modtime=" + lastModified + ". Cached modtime");
                    }
                }
                resource = new CachedResource();
                resource.contents = generateCachableContent(request.getContextPath(), request.getServletPath(), request.getPathInfo(), locale);
                resource.lastModifiedTime = lastModified;
                resourceCache.put(localizedMessage, resource);
            }
        }
        
        addCacheHeaders(response);
        
        response.setContentType(getContentType());
        response.setDateHeader(HttpConstants.HEADER_LAST_MODIFIED, lastModified);
        response.setDateHeader(HttpConstants.HEADER_ETAG, lastModified);
        
        if (resource.contents != null) {
            PrintWriter out = response.getWriter();
            out.write(resource.contents);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    protected Locale getLocale(HttpServletRequest request) {
        return Locale.getDefault();
    }
    
    protected void addCacheHeaders(HttpServletResponse response) {
        if (getPeriodCacheableTime() == 0) {
            addNoCacheHeaders(response);
            return;
        }
        int cacheSecs;
        if (getPeriodCacheableTime() < 0) {
            cacheSecs = 5 * 60; // 5 minutes
        } else {
            cacheSecs = getPeriodCacheableTime();
        }
        long expiry = new Date().getTime() + cacheSecs * 1000;
        // Set standard HTTP/1.1 cache headers.
        response.setHeader("Cache-Control", "public, max-age=" + cacheSecs);
        // Set to expire far in the past. Prevents caching at the proxy server
        response.setDateHeader("Expires", expiry);
    }

    /**
     * Add headers to prevent browsers and proxies from caching this reply.
     * @param resp The response to add headers to
     */
    protected void addNoCacheHeaders(HttpServletResponse response) {
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // Set to expire far in the past. Prevents caching at the proxy server
        response.setDateHeader("Expires", 0);
    }

    /**
     * Detect the last time, after which we are sure that the resource has not
     * changed
     * @return The last modification time
     */
    protected abstract long getLastModifiedTime();
    
    /**
     * Create a String which can be cached and sent as a 302. Returning null
     * signals that the resource doesn't exist and should result in a 404.
     * @param contextPath
     * @param servletPath
     * @param pathInfo
     * @param locale
     * @return The string to output for this resource
     * @throws IOException
     */
    public abstract String generateCachableContent(String contextPath, String servletPath, String pathInfo, Locale locale) throws IOException;
    
    /**
     * Do we need to send the content for this file
     * @param req The HTTP request
     * @return true iff the ETags and If-Modified-Since headers say we have not changed
     */
    protected boolean isUpToDate(HttpServletRequest req, long lastModified) {
        String etag = "\"" + lastModified + '\"';

        if (ignoreLastModified) {
            return false;
        }

        long modifiedSince = -1;
        try {
            modifiedSince = req.getDateHeader(HttpConstants.HEADER_IF_MODIFIED);
        } catch (RuntimeException ex) {
            // ignore
        }
        
        if (modifiedSince != -1) {
            // Browsers are only accurate to the second
            modifiedSince -= modifiedSince % 1000;
        }
        String givenEtag = req.getHeader(HttpConstants.HEADER_IF_NONE);
        String cachedPath = getCachingKey(req);
        
        // Deal with missing etags
        if (givenEtag == null) {
            // There is no ETag, just go with If-Modified-Since
            if (modifiedSince >= lastModified) {
                if (log.isDebugEnabled()) {
                    log.debug("Sending 304 for " + cachedPath + " If-Modified-Since=" + modifiedSince + ", Last-Modified=" + lastModified);
                }
                return true;
            }
            // There are no modified settings, carry on
            return false;
        }
        
        // Deal with missing If-Modified-Since
        if (modifiedSince == -1) {
            if (!etag.equals(givenEtag)) {
                // There is an ETag, but no If-Modified-Since
                if (log.isDebugEnabled()) {
                    log.debug("Sending 304 for " + cachedPath + ", If-Modified-Since=-1, Old ETag=" + givenEtag + ", New ETag=" + etag);
                }
                return true;
            }
            // There are no modified settings, carry on
            return false;
        }
        
        // Do both values indicate that we are in-date?
        if (etag.equals(givenEtag) && modifiedSince >= lastModified) {
            if (log.isDebugEnabled()) {
                log.debug("Sending 304 for " + cachedPath + ", If-Modified-Since=" + modifiedSince + ", Last Modified=" + lastModified + ", Old ETag=" + givenEtag + ", New ETag=" + etag);
            }
            return true;
        }
        log.debug("Sending content for " + cachedPath + ", If-Modified-Since=" + modifiedSince + ", Last Modified=" + lastModified + ", Old ETag=" + givenEtag + ", New ETag=" + etag);
        return false;
    }
    
    /**
     * Returns the caching key which is based on the servlet path
     * as well as the  cachedPath.
     *
     * @param request
     */
    protected String getCachingKey(HttpServletRequest request) {
        StringBuilder absolutePath = new StringBuilder();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        
        absolutePath.append(scheme);
        absolutePath.append("://");
        absolutePath.append(request.getServerName());
        
        if (port > 0 && (("http".equalsIgnoreCase(scheme) && port != 80) || ("https".equalsIgnoreCase(scheme) && port != 443))) {
            absolutePath.append(':');
            absolutePath.append(port);
        }
        
        absolutePath.append(request.getContextPath());
        absolutePath.append(request.getServletPath());
        
        if (request.getPathInfo() != null) {
            absolutePath.append(request.getPathInfo());
        }
        return absolutePath.toString();
    }
    
    class CachedResource {
        protected String contents;
        protected long lastModifiedTime;
    }
}
