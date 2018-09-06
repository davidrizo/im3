package es.ua.dlsi.im3.gui.javafx.collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.function.Function;

//TODO Llevarlo a IMCore
/**
 * It contains a sorted list linked to the list in the model
 * @autor drizo
 */
public abstract class ObservableListViewModelLink<ModelObjectType, ModelCollectionType extends Collection<ModelObjectType>, ViewObjectType extends Comparable<ViewObjectType>> {
    ObservableList<ViewObjectType> views_data;
    HashMap<ModelObjectType, ViewObjectType> mapModelViews;

    public ObservableListViewModelLink(ModelCollectionType modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction) {
        mapModelViews = new HashMap<>();
        views_data = FXCollections.observableList(new LinkedList<>());
        /*views = new SortedList<ViewObjectType>(FXCollections.observableList(views_data), new Comparator<ViewObjectType>() {
            @Override
            public int compare(ViewObjectType o1, ViewObjectType o2) {
                return o1.compareTo(o2);
            }
        });*/

        // first add model objects
        for (ModelObjectType modelObject: modelCollection) {
            ViewObjectType view = viewFactoryFunction.apply(modelObject);
            add(modelObject, view);
        }

        initChangeListener(modelCollection, viewFactoryFunction);
    }

    protected void add(ModelObjectType modelObjectType, ViewObjectType viewObject) {
        views_data.add(viewObject);
        mapModelViews.put(modelObjectType, viewObject);
    }

    public ObservableList<ViewObjectType> getViews() {
        return views_data;
    }

    protected abstract void initChangeListener(ModelCollectionType modelCollection, Function<ModelObjectType, ViewObjectType> viewFactoryFunction);
}
