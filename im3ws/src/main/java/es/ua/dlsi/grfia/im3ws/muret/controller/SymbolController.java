package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.entity.Region;
import es.ua.dlsi.grfia.im3ws.muret.entity.Symbol;
import es.ua.dlsi.grfia.im3ws.muret.service.RegionService;
import es.ua.dlsi.grfia.im3ws.muret.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author drizo
 */
@RequestMapping("/muret/symbol")
@RestController
public class SymbolController extends CRUDController<Symbol, Long, SymbolService> {
    @Autowired
    SymbolService symbolService;
    @Autowired
    RegionService regionService;

    @Override
    protected SymbolService initService() {
        return symbolService;
    }

    @GetMapping(path = {"/region/{id}"})
    public List<Symbol> findByRegionID(@PathVariable(name="id") Long regionID) throws IM3WSException {
        // TODO This could be improved using a native query - this one makes two sql queries
        Optional<Region> region =  regionService.findById(regionID);
        if (!region.isPresent()) {
            throw new IM3WSException("Cannot find a region with ID = " + regionID);
        }

        return region.get().getSymbols();
    }

}
