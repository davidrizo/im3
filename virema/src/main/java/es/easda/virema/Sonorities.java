package es.easda.virema;

import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;

import java.util.List;

public class Sonorities {
    ScoreSong scoreSong;
    List<Segment> segments;

    public Sonorities(ScoreSong scoreSong, List<Segment> segments) {
        this.scoreSong = scoreSong;
        this.segments = segments;
    }

    public ScoreSong getScoreSong() {
        return scoreSong;
    }

    public List<Segment> getSegments() {
        return segments;
    }
}
