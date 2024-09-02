package com.github.codeby5to.gcloud4j.client.Util;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

public class MimeTypeUtil {

    private final TikaConfig config;
    private final Tika tika;

    public MimeTypeUtil() {
        this.config = TikaConfig.getDefaultConfig();
        this.tika = new Tika();

    }

    public String getMimeTypeFromFile(byte[] file) {
        return tika.detect(file);
    }

    public String getExtensionFromMimeType(String mimeType) {

        MimeType mimeTypeObject;

        try {
            mimeTypeObject = config.getMimeRepository().forName(mimeType);
        } catch (MimeTypeException e) {
            throw new RuntimeException("Error getting MimeType object", e);
        }

        if(mimeTypeObject == null) {
            return "";
        }

        return mimeTypeObject.getExtension();
    }

}
