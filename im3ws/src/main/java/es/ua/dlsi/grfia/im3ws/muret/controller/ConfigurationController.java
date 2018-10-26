package es.ua.dlsi.grfia.im3ws.muret.controller;

import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author drizo
 */
@RestController
public class ConfigurationController {
    @Autowired
    private MURETConfiguration muretConfiguration;

    @GetMapping("/muret/imagesurl")
    public String getImagesURL() {
        return muretConfiguration.getUrl();
    }
}
