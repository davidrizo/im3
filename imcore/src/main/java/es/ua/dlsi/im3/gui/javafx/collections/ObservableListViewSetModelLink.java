package es.ua.dlsi.im3.gui.javafx.collections;

import javafx.collections.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * It contains a sorted list linked to the set in the model
 * @autor drizo
 */
public class ObservableListViewSetModelLink<ModelObjectType, ViewObjectType extends Comparable<ViewObjectType>> extends ObservableListViewModelLink<ModelObjectType, ObservableSet<ModelObjectType>, ViewObjectType> {

    public ObservableListViewSetModelLink(ObservableSet<ModelObjectType> modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction) {
        super(modelCollection, viewFactoryFunction);
    }

    @Override
    protected void initChangeListener(ObservableSet<ModelObjectType> modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction) {
        // then listen to changes
        modelCollection.addListener(new SetChangeListener<ModelObjectType>() {
            @Override
            public void onChanged(Change<? extends ModelObjectType> change) {
                if (change.wasRemoved()) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Removing view from model object {0}", change.getElementRemoved());
                    ModelObjectType modelObject = change.getElementRemoved();
                    ViewObjectType view = mapModelViews.get(modelObject);
                    if (view == null) {
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "No view mapped to model object {0}", modelObject);
                    } else {
                        mapModelViews.remove(modelObject);
                        views_data.remove(view);
                    }
                } else if (change.wasAdded()) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding view from model object {0}", change.getElementAdded());
                    ModelObjectType modelObject = change.getElementAdded();
                    ViewObjectType view = viewFactoryFunction.apply(modelObject);
                    add(modelObject, view);
                }
            }
        });

    }
}
