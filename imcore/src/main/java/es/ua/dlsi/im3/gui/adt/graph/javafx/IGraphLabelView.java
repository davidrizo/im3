package es.ua.dlsi.im3.gui.adt.graph.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public interface IGraphLabelView {

	Node getRoot();

	/**
	 * It is not drawn until this method is invoked
	 * @param scaleX
	 * @param scaleY
	 * @throws IM3Exception
	 */
	void paint(DoubleBinding scaleX, DoubleBinding scaleY) throws IM3Exception;

	ObjectProperty<Color> colorProperty();
}
