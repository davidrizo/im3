package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.Simultaneity;

import java.util.*;

// TODO: 19/9/17 Test unitario
public class Simultaneities {
    TreeSet<Simultaneity> simultaneities;

    public Simultaneities() {
        simultaneities = new TreeSet<>();
    }

    public void add(LayoutSymbolInStaff symbol) {
        Simultaneity simultaneity = new Simultaneity(symbol);
        Simultaneity greatestEqualOrGreaterSimulaneity = simultaneities.ceiling(simultaneity);

        if (greatestEqualOrGreaterSimulaneity == null || !greatestEqualOrGreaterSimulaneity.equals(simultaneity)) {
            // when not found a simultaneity with same time and order a new simultaneity must be inserted
            simultaneities.add(simultaneity);
        } else {
            greatestEqualOrGreaterSimulaneity.add(symbol);
        }
    }

    void printDebug() {
        System.out.println("----SIMULTANEITIES-----");
        for (Simultaneity s: simultaneities) {
            System.out.println(s.toString());
        }
    }

    public TreeSet<Simultaneity> getSimiltaneities() {
        return simultaneities;
    }
}
