package es.ua.dlsi.grfia.im3ws.muret.controller;

import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.muret.entity.ManuscriptType;
import es.ua.dlsi.grfia.im3ws.muret.model.AgnosticSymbolFont;
import es.ua.dlsi.grfia.im3ws.muret.model.AgnosticSymbolFontSingleton;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequestMapping("/muret/agnostic")
@CrossOrigin("${angular.url}")
@RestController
public class AgnosticSymbolSVGController {

    @GetMapping(path = {"svg"})
    public String getAgnosticSymbolSVG(@RequestParam(name="notationType") NotationType notationType, @RequestParam(name="manuscriptType") ManuscriptType manuscriptType, @RequestParam(name="symbolType") String symbolType) throws IM3WSException {
            Objects.requireNonNull(notationType, "notationType cannot be null");
            Objects.requireNonNull(manuscriptType, "manuscriptType cannot be null");
            Objects.requireNonNull(symbolType, "symbolType cannot be null");
            AgnosticSymbolFont agnosticSymbolFont = AgnosticSymbolFontSingleton.getInstance().getLayoutFont(notationType, manuscriptType);
            Objects.requireNonNull(symbolType, "layoutFont for notationType " + notationType + " and manuscriptType " + manuscriptType + " has not been found");
        try {
            return agnosticSymbolFont.getSVGPathd(symbolType);
        } catch (IM3Exception e) {
            throw new IM3WSException("Cannot find a svg path for agnostic symbol '" + symbolType + "', notationType '" + notationType + "' and manuscriptType " + manuscriptType + "'");
        }
    }
}

