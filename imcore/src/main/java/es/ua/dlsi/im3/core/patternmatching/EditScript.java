package es.ua.dlsi.im3.core.patternmatching;

import java.util.ArrayList;

/**
 *
 * @author drizo
 */
public class EditScript {
    ArrayList<EditOperation> script;

    public EditScript(EditScript previous, EditScript current) {
        if (previous == null) {
            script = new ArrayList<EditOperation>();
        } else {
            script = (ArrayList<EditOperation>) previous.script.clone();
        }
        this.script.addAll(current.script);
    }

    public ArrayList<EditOperation> getScript() {
        return script;
    }

    public EditScript() {
        script = new ArrayList<EditOperation>();
    }
    public EditScript(EditScript previous, EditOperation e) throws CloneNotSupportedException {
        if (previous == null) {
            script = new ArrayList<EditOperation>();
        } else {
            script = (ArrayList<EditOperation>) previous.script.clone();
        }
        script.add(e);
    }
    public void addEditOperation(EditOperation e) {
        script.add(e);
    }
    public EditScript(EditScript previous, int from, int to, double cost) throws CloneNotSupportedException {
        if (previous == null) {
            script = new ArrayList<EditOperation>();
        } else {
            script = (ArrayList<EditOperation>) previous.script.clone();
        }
        EditOperation e = new EditOperation(from, to, cost);
        script.add(e);
    }
    public EditScript(int from, int to, double cost) throws CloneNotSupportedException {
        script = new ArrayList<EditOperation>();
        EditOperation e = new EditOperation(from, to, cost);
        script.add(e);
    }
    public void addEditOperation(int from, int to, double cost) {
        EditOperation e = new EditOperation(from, to, cost);
        script.add(e);
    }
    @Override
    public Object clone() {
        EditScript res = new EditScript();
        res.script = (ArrayList<EditOperation>) this.script.clone();
        return res;
    }

    @Override
    public String toString() {
        return script.toString();
    }

    public double getAccumulatedCost() {
        double cost = 0;
        for (int i=0; i<this.script.size(); i++) {
            cost += this.script.get(i).getCost();
        }
        return cost;
    }

}

