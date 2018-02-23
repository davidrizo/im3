package es.ua.dlsi.im3.core.adt.wordgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The classes contained in the wordgraph. This class indexes the classes
 * @param <ClassificationClassType> The type of the classification classes (may be a String), which must implement hashCode method
 */
public class ClassesIndex<ClassificationClassType> {
    /**
     * The index associated to each class
     */
    HashMap<ClassificationClassType, Integer> indexes;
    ArrayList<ClassificationClassType> classes;

    public ClassesIndex(ArrayList<ClassificationClassType> classes) {
        this.classes = classes;
        indexes = new HashMap<>();
        for (int i=0; i<classes.size(); i++) {
            indexes.put(classes.get(i), i);
        }
    }

    public HashMap<ClassificationClassType, Integer> getIndexes() {
        return indexes;
    }

    public ArrayList<ClassificationClassType> getClasses() {
        return classes;
    }
}
