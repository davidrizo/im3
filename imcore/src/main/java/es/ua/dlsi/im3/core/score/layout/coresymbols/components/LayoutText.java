package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.score.ScoreLyric;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Text;

import java.util.ArrayList;

/**
 * Not a lyric - for lyrics use LayoutScoreLyric
 */
public class LayoutText<ParentType extends NotationSymbol> extends Component<ParentType> {
    Text text;
    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public LayoutText(ParentType parent, LayoutFont layoutFont, Coordinate position, String string) {
        super(null, parent, position);

        text = new Text(this, InteractionElementType.barline, layoutFont, string, position);
    }

    @Override
    public GraphicsElement getGraphics() {
        return text;
    }

    @Override
    protected void doLayout() {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
