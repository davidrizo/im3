package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;

public interface ILayoutEngine {
    void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception;
}
