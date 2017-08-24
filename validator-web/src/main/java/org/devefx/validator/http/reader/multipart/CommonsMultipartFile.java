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

package org.devefx.validator.http.reader.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.web.multipart.MultipartFile;

public class CommonsMultipartFile implements MultipartFile, Serializable {

    private static final long serialVersionUID = 4129054616837906339L;
    
    protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);
    
    private final FileItem fileItem;

    private final long size;

    /**
     * Create an instance wrapping the given FileItem.
     * @param fileItem the FileItem to wrap
     */
    public CommonsMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }
    
    /**
     * Return the underlying {@code org.apache.commons.fileupload.FileItem}
     * instance. There is hardly any need to access this.
     */
    public final FileItem getFileItem() {
        return this.fileItem;
    }
    
    @Override
    public String getName() {
        return fileItem.getFieldName();
    }

    @Override
    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            // Should never happen.
            return "";
        }
        // check for Unix-style path
        int pos = filename.lastIndexOf("/");
        if (pos == -1) {
            // check for Windows-style path
            pos = filename.lastIndexOf("\\");
        }
        if (pos != -1)  {
            // any sort of path separator found
            return filename.substring(pos + 1);
        }
        else {
            // plain name
            return filename;
        }
    }

    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return (this.size == 0);
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return (bytes != null ? bytes : new byte[0]);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        InputStream inputStream = this.fileItem.getInputStream();
        return (inputStream != null ? inputStream : new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }

        if (dest.exists() && !dest.delete()) {
            throw new IOException(
                    "Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
        }

        try {
            this.fileItem.write(dest);
            if (logger.isDebugEnabled()) {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = isAvailable() ? "copied" : "moved";
                }
                logger.debug("Multipart file '" + getName() + "' with original filename [" +
                        getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
                        action + " to [" + dest.getAbsolutePath() + "]");
            }
        } catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage());
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Could not transfer to file", ex);
            throw new IOException("Could not transfer to file: " + ex.getMessage());
        }
    }
    
    /**
     * Determine whether the multipart content is still available.
     * If a temporary file has been moved, the content is no longer available.
     */
    protected boolean isAvailable() {
        // If in memory, it's available.
        if (this.fileItem.isInMemory()) {
            return true;
        }
        // Check actual existence of temporary file.
        if (this.fileItem instanceof DiskFileItem) {
            return ((DiskFileItem) this.fileItem).getStoreLocation().exists();
        }
        // Check whether current file size is different than original one.
        return (this.fileItem.getSize() == this.size);
    }
    
    /**
     * Return a description for the storage location of the multipart content.
     * Tries to be as specific as possible: mentions the file location in case
     * of a temporary file.
     */
    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        }
        else if (this.fileItem instanceof DiskFileItem) {
            return "at [" + ((DiskFileItem) this.fileItem).getStoreLocation().getAbsolutePath() + "]";
        }
        else {
            return "on disk";
        }
    }
}
