package es.ua.dlsi.grfia.im3ws.muret;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "muret")
public class MURETConfiguration {
    private String folder;
    private String url;

    public MURETConfiguration() {
    }

    public MURETConfiguration(String folder, String url) {
        this.folder = folder;
        this.url = url;
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
}
