package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.entity.*;
import es.ua.dlsi.grfia.im3ws.muret.model.ImageModel;
import es.ua.dlsi.grfia.im3ws.muret.service.ImageService;
import es.ua.dlsi.grfia.im3ws.muret.service.PageService;
import es.ua.dlsi.grfia.im3ws.muret.service.RegionService;
import es.ua.dlsi.grfia.im3ws.muret.service.SymbolService;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author drizo
 */
@RequestMapping("/muret/image")
@RestController
public class ImageController extends CRUDController<Image, Long, ImageService> {
    @Autowired
    ImageService imageService;

    @Autowired
    RegionService regionService;

    @Autowired
    PageService pageService;

    @Autowired
    SymbolService symbolService;


    @Override
    protected ImageService initService() {
        return imageService;
    }

    @Autowired
    private ImageModel imageModel;

    /**
     * It returns the new list of pages of the image
     * @param id
     * @param x
     * @return
     * @throws IM3WSException
     */
    @GetMapping(path = {"pageSplit/{id}/{x}"})
    public List<Page> pageSplit(@PathVariable("id") Long id, @PathVariable("x") Double x) throws IM3WSException {
        Optional<Image> image = imageService.findById(id);
        if (!image.isPresent()) {
            throw new IM3WSException("Cannot find an image with id " + id);
        }
        return imageModel.pageSplit(image.get(), x.intValue());
    }

    /**
     * It returns the new list of pages of the image
     * @param id
     * @param x
     * @return
     * @throws IM3WSException
     */
    @GetMapping(path = {"regionSplit/{id}/{x}/{y}"})
    public List<Page> regionSplit(@PathVariable("id") Long id, @PathVariable("x") Double x, @PathVariable("y") Double y) throws IM3WSException {
        Optional<Image> image = imageService.findById(id);
        if (!image.isPresent()) {
            throw new IM3WSException("Cannot find an image with id " + id);
        }
        return imageModel.regionSplit(image.get(), x.intValue(), y.intValue());
    }


    @GetMapping(path = {"documentAnalysisClear/{id}"})
    public List<Page>  documentAnalysisClear(@PathVariable("id") Long id) throws IM3WSException {
        Optional<Image> image = imageService.findById(id);
        if (!image.isPresent()) {
            throw new IM3WSException("Cannot find an image with id " + id);
        }
        return imageModel.leaveJustOnePageAndRegion(image.get());
    }

    @GetMapping(path = {"pageUpdate/{id}/{fromX}/{fromY}/{toX}/{toY}"})
    public Page pageUpdate(@PathVariable("id") Long id,
                             @PathVariable("fromX") Double fromX,
                             @PathVariable("fromY") Double fromY,
                             @PathVariable("toX") Double toX,
                             @PathVariable("toY") Double toY) throws IM3WSException {
        Optional<Page> page = pageService.findById(id);
        if (!page.isPresent()) {
            throw new IM3WSException("Cannot find a page with id " + id);
        }
        page.get().getBoundingBox().setFromX(fromX.intValue());
        page.get().getBoundingBox().setFromY(fromY.intValue());
        page.get().getBoundingBox().setToX(toX.intValue());
        page.get().getBoundingBox().setToY(toY.intValue());
        return pageService.update(page.get());
    }

    @GetMapping(path = {"regionUpdate/{id}/{fromX}/{fromY}/{toX}/{toY}"})
    public Region regionUpdate(@PathVariable("id") Long id,
                             @PathVariable("fromX") Double fromX,
                             @PathVariable("fromY") Double fromY,
                             @PathVariable("toX") Double toX,
                             @PathVariable("toY") Double toY) throws IM3WSException {
        Optional<Region> region = regionService.findById(id);
        if (!region.isPresent()) {
            throw new IM3WSException("Cannot find a page with id " + id);
        }
        region.get().getBoundingBox().setFromX(fromX.intValue());
        region.get().getBoundingBox().setFromY(fromY.intValue());
        region.get().getBoundingBox().setToX(toX.intValue());
        region.get().getBoundingBox().setToY(toY.intValue());
        return regionService.update(region.get());
    }

    @GetMapping(path = {"createSymbol/{regionID}/{fromX}/{fromY}/{toX}/{toY}"})
    public Symbol createSymbol(@PathVariable("regionID") Long regionID,
                               @PathVariable("fromX") Double fromX,
                               @PathVariable("fromY") Double fromY,
                               @PathVariable("toX") Double toX,
                               @PathVariable("toY") Double toY) throws IM3WSException, IM3Exception {
        Optional<Region> region = regionService.findById(regionID);
        if (!region.isPresent()) {
            throw new IM3WSException("Cannot find a page with id " + regionID);
        }

        AgnosticSymbol agnosticSymbol = imageModel.classifySymbolFromImageBoundingBox(region.get().getPage().getImage(),
                fromX.intValue(), fromY.intValue(), toX.intValue(), toY.intValue(), "TO-DO"); //TODO

        Symbol symbol = new Symbol(region.get(), agnosticSymbol,
                new BoundingBox(fromX.intValue(), fromY.intValue(), toX.intValue(), toY.intValue()),
                null, null);

        return symbolService.create(symbol);
    }
}
