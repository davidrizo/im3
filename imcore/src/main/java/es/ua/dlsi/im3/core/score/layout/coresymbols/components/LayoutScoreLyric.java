package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreLyric;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Text;

import java.util.ArrayList;

// TODO: 30/9/17 Comportamiento cuando hay distintos score lyrics para voces en el mismo pentagrama
public class LayoutScoreLyric extends Component<NotePitch> {
    Group group;
    ArrayList<Text> lyrics;
    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public LayoutScoreLyric(NotePitch parent, LayoutFont layoutFont, Coordinate position) {
        super(null, parent, position);
        group = new Group(this, InteractionElementType.lyrics);
        if (parent.getAtomPitch().getLyrics() != null) {
            lyrics = new ArrayList<>();
            for (ScoreLyric lyrics : parent.getAtomPitch().getLyrics().values()) {
                double y = LayoutConstants.SEPARATION_LYRICS_STAFF;
                y += (layoutFont.getTextHeightInPixels() + LayoutConstants.LYRICS_VERSE_SEPARATION) * lyrics.getIndex();

                Coordinate coordinate = new Coordinate(
                        position.getX(),
                        new CoordinateComponent(position.getY(), y));
                Text text = new Text(this, InteractionElementType.lyric, layoutFont, lyrics.getText(), coordinate);
                group.add(text);
            }
        }
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
