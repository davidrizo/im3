package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.List;

public interface ISemanticToScoreSongTransducer {
    /**
     * It transduces the tokens and leaves them into the staff and scoreLayer
     * @param tokens
     * @param scoreStaff
     * @param scoreLayer
     * @throws IM3Exception
     */
    void transduceInto(List<SemanticToken> tokens, Staff scoreStaff, ScoreLayer scoreLayer) throws IM3Exception;
}
