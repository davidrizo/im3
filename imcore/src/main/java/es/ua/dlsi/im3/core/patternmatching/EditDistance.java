package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.ArrayList;
import java.util.List;

public class EditDistance<ItemType> {
    private final IEditDistanceOperations<ItemType> editDistanceOperations;
    protected double z = 0;
    private EditScript editScript;
    private List<ItemType> from;
    private List<ItemType> to;
    double weightInsert;
    double weightDelete;
    private boolean traceEditScript;

    public EditDistance(IEditDistanceOperations<ItemType> editDistanceOperations) {
        super();
        this.editDistanceOperations = editDistanceOperations;
    }

    public void setTraceEditScript(boolean traceEditScript) {
        this.traceEditScript = traceEditScript;
    }

    /**
     * Minimum between 3 values
     * @param x
     * @param y
     * @param z
     * @return
     */
    static public double min3(double x, double y, double z){
        return  x<y? (x<z?x:z) : (y<z?y:z);
    }
    /**
     * Maximum between 3 values
     * @param x
     * @param y
     * @param z
     * @return
     */
    static public double max3(double x, double y, double z){
        return  x>y? (x>z?x:z) : (y>z?y:z);
    }

    /**
     *
     * @param from
     * @param to
     * @return Normalizado entre 0 y 1
     * @throws IM3Exception
     */
    public double computeDistance(List<ItemType> from, List<ItemType> to) throws IM3Exception {
        double res=0;
        double [][] m = new double[from.size()+1][to.size()+1];
        int i,j;

        this.from = from;
        this.to = to;

        if (from.size() == 0 && to.size() == 0) {
            return 0;
        }

        m[0][0] = z;

        for (i=1; i<=from.size(); i++)
            m[i][0]=m[i-1][0] + editDistanceOperations.deleteCost(from.get(i-1)); // pesoBorrado

        for (j=1; j<=to.size(); j++)
            m[0][j]=m[0][j-1] + editDistanceOperations.insertCost(to.get(j-1)); // pesoInsercion

        for (i=1; i<=from.size(); i++) {
            for (j=1; j<=to.size(); j++)
            {
                double sust = editDistanceOperations.substitutionCost(from.get(i-1), to.get(j-1));
                m[i][j] = min3(m[i-1][j-1] + sust, m[i][j-1] + editDistanceOperations.insertCost(to.get(j-1)), m[i-1][j] +editDistanceOperations. deleteCost(from.get(i-1)));
            }
        }

        res = m[from.size()][to.size()];

        // backtrace
        if (this.traceEditScript)
        {
            editScript = new EditScript();
            int ii=m.length-1;
            int jj=m[ii].length-1;
            while (ii>0 || jj>0) {
                //System.out.println(">"+ii + "<\t>" + jj + "<");
                EditOperation eo;
                if (ii>0 && jj>0 && m[ii-1][jj-1] <= m[ii-1][jj] && m[ii-1][jj-1] <= m[ii][jj-1]) {
                    // sust
                    eo = new EditOperation(ii-1, jj-1, m[ii][jj]-m[ii-1][jj-1]);
                    ii=ii-1;
                    jj=jj-1;
                } else if (jj>0 && ii==0 || jj>0 && m[ii][jj-1] <= m[ii-1][j-1] && m[ii][jj-1] <= m[ii-1][jj]) {
                    // ins
                    eo = new EditOperation(EditOperation.EMPTY, jj-1, m[ii][jj] - m[ii][jj-1]);
                    jj=jj-1;
                } else {
                    // del
                    eo = new EditOperation(ii-1, EditOperation.EMPTY, m[ii][jj] - m[ii-1][jj]);
                    ii=ii-1;
                }
                this.editScript.addEditOperation(eo);
            }
        }
        //double distance  = res / (double) (from.size() + to.size());
        return res;
    }

    public void printEditString() {
        System.out.println("Accumulated cost: " + editScript.getAccumulatedCost());
		/*System.out.println("For strings of sizes " + from.size() + " and " + to.size());
		System.out.println(editScript.toString());*/
        ArrayList<EditOperation> scr = editScript.getScript();
        for (int i=scr.size()-1; i>=0; i--) {
            EditOperation eo = scr.get(i);
            ItemType from = eo.getFrom()!=EditOperation.EMPTY?  this.from.get(eo.getFrom()):null;
            ItemType to = eo.getTo()!=EditOperation.EMPTY ? this.to.get(eo.getTo()):null;
            System.out.print(from!=null?from.toString():"-");
            System.out.print("\t");
            System.out.print(to!=null?to.toString():"-");
            System.out.print("\t");
            System.out.println(eo.getCost());
        }
    }
}
