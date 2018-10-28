package es.ua.dlsi.grfia.im3ws.muret;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "muret")
public class MURETConfiguration {
    public static final String MASTER_IMAGES = "masters";
    public static final String THUMBNAIL_IMAGES = "thumbnails";

    private String folder;
    private String url;
    private int thumbnailHeight;

    public MURETConfiguration() {
    }

    public MURETConfiguration(String folder, String url, int thumbnailHeight) {
        this.folder = folder;
        this.url = url;
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }




}
