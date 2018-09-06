package es.ua.dlsi.im3.gui.javafx.collections;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains a sorted list linked to the list in the model
 * @autor drizo
 */
public class ObservableListViewListModelLink<ModelObjectType, ViewObjectType extends Comparable<ViewObjectType>> extends ObservableListViewModelLink<ModelObjectType, ObservableList<ModelObjectType>, ViewObjectType> {


    public ObservableListViewListModelLink(ObservableList<ModelObjectType> modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction) {
        super(modelCollection, viewFactoryFunction);
    }

    @Override
    protected void initChangeListener(ObservableList<ModelObjectType> modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction) {
        // then listen to changes
        modelCollection.addListener(new ListChangeListener<ModelObjectType>() {
            @Override
            public void onChanged(Change<? extends ModelObjectType> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (ModelObjectType modelObject : c.getRemoved()) {
                            ViewObjectType view = mapModelViews.get(modelObject);
                            if (view == null) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "No view mapped to model object {0}", modelObject);
                            } else {
                                mapModelViews.remove(modelObject);
                                views_data.remove(view);
                            }
                        }
                    } else if (c.wasAdded()) {
                        for (ModelObjectType modelObject : c.getAddedSubList()) {
                            ViewObjectType view = viewFactoryFunction.apply(modelObject);
                            add(modelObject, view);
                        }
                    }
                }
            }
        });
    }
}
