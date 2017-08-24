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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.devefx.validator.web.multipart.ImageMultipartFile;

public class CommonsImageMultipartFile extends CommonsMultipartFile implements ImageMultipartFile {

    private static final long serialVersionUID = -4497064398444297064L;

    private final BufferedImage image;
    
    public CommonsImageMultipartFile(FileItem fileItem) throws IOException {
        super(fileItem);
        this.image = readImage(fileItem);
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    protected BufferedImage readImage(FileItem file) throws IOException {
        InputStream in = file.getInputStream();
        if (in == null) {
            return null;
        }
        return ImageIO.read(in);
    }
}
