package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.entity.Image;
import es.ua.dlsi.grfia.im3ws.muret.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author drizo
 */
@RequestMapping("/muret/image")
@RestController
public class ImageController extends CRUDController<Image, Long, ImageService> {
    @Autowired
    ImageService imageService;

    @Override
    protected ImageService initService() {
        return imageService;
    }
}
