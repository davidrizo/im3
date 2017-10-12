package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.Symbol;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.StrokeView;
import es.ua.dlsi.im3.omr.traced.Stroke;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class OMRSymbol<SymbolType> extends Group {
    private static final double DEFAULT_WIDTH = 2;
    private final SimpleDoubleProperty strokeWidth;
    private final ObjectProperty<Symbol<SymbolType>> symbol;
    private StrokeView currentStrokeView;
    private ImageView imageView;
    private final ObjectProperty<Color> color;
    private final Color unselectedColor;

    public OMRSymbol(Symbol<SymbolType> symbol, Color color) throws IM3Exception {
        this.strokeWidth = new SimpleDoubleProperty(DEFAULT_WIDTH);
        this.symbol = new SimpleObjectProperty<>(symbol);
        currentStrokeView = null;
        this.color = new SimpleObjectProperty<>(color);
        unselectedColor = color;

        List<Stroke> strokes = symbol.getStrokes();
        for (Stroke stroke : strokes) {
            addStroke(stroke);
        }

        createImageView();
        createInteraction();
    }

    private void createImageView() {
        if (symbol.get().getSymbolImage() != null) {
            WritableImage fxImage = new WritableImage((int) symbol.get().getWidth(), (int) symbol.get().getHeight());
            imageView = new ImageView(SwingFXUtils.toFXImage(symbol.get().getSymbolImage(), fxImage));
        }
    }

    public void addNewStroke() {
        Stroke stroke = new Stroke();
        symbol.get().addStroke(stroke);
        addStroke(stroke);
    }

    public final void addStroke(Stroke stroke) {
        currentStrokeView = createStrokeView(stroke);
        StrokeView miniaturePath = createStrokeView(stroke);
        getChildren().add(currentStrokeView);
    }

    private StrokeView createStrokeView(Stroke stroke) {
        StrokeView result = new StrokeView(stroke);
        result.strokeWidthProperty().bind(strokeWidth);
        result.strokeProperty().bind(color);

        return result;
    }
    private void createInteraction() {
        this.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                color.set(Color.GREENYELLOW);

            }
        });
        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                color.set(unselectedColor);
            }
        });

    }

}
