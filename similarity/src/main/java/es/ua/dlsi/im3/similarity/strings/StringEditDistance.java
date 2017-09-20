package es.ua.dlsi.im3.similarity.strings;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.midrepresentations.sequences.Sequence;

import java.util.ArrayList;


/**
@author drizo
@date 14/07/2008
 **/
public class StringEditDistance<Type, TypeComparer extends ISymbolComparer<Type>> {
	private final TypeComparer symbolComparer;
	protected double z = 0;
	private Type [] vquery;
	private Type [] vdocument;
	protected EditScript editScript = null;
	protected boolean traceEditScript = false;
	protected double[][] dynammicProgrammicTable;
	protected boolean normalizeResult;

	
	public StringEditDistance(TypeComparer symbolComparer) {
		this.symbolComparer = symbolComparer;
	}
	
	public StringEditDistance(TypeComparer symbolComparer, boolean traceEditScript) {
		this.symbolComparer = symbolComparer;
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
	
	public double distance(Sequence<Type> query, Sequence<Type> document) throws IM3Exception {
		vquery = query.getItemsAsArray();
		vdocument = document.getItemsAsArray();
		
		double res=0;
		double [][] m = new double[vquery.length+1][vdocument.length+1];
		int i,j;
		
		if (vquery.length == 0 && vdocument.length == 0) {
			return 0;
		}
		
		m[0][0] = z;

		for (i=1; i<=vquery.length; i++)
			//m[i][0]=f(m[i-1][0], deleteCost(vquery.get(i-1))); // pesoBorrado
			m[i][0]=m[i-1][0] + deleteCost(vquery[i-1]); // pesoBorrado

		for (j=1; j<=vdocument.length; j++)
			m[0][j]=m[0][j-1] +insertCost(vdocument[j-1]); // pesoInsercion

		//System.out.println("alenght:" + a.length + ", blenght=" + b.length);
		for (i=1; i<=vquery.length; i++)
			for (j=1; j<=vdocument.length; j++)
			{
				//System.out.println(a[i-1] + "    " + b[i-1]) ;
				//double sust = a[i-1].equals(b[j-1])?0:2;
				double sust = distance(vquery[i-1], vdocument[j-1]);
				//System.out.println(sust);
				/*m[i][j] = g(f(m[i-1][j-1],sust),   //pesoSustitucion(m1[i-1],m2[j-1]),
					f(m[i][j-1], insertCost(vdocument.get(j-1))), //pesoInsercion(m2[j-1]),
					f(m[i-1][j], deleteCost(vquery.get(i-1)))); //pesoBorrado(m1[i-1]));*/
				m[i][j] = min3(m[i-1][j-1] + sust, m[i][j-1] + insertCost(vdocument[j-1]), m[i-1][j] +deleteCost(vquery[i-1]));
				/*} else {
					double _sus = m[i-1][j-1] + sust;
					double _inscost = insertCost(vdocument.get(j-1));
					double _ins = m[i][j-1] + _inscost;
					double _delcost = deleteCost(vquery.get(i-1));
					double _del = m[i-1][j] +_delcost;
					
					EditOperation eo; 
					if (_sus <= _del && _sus <= _ins) {
						m[i][j] = _sus;
						eo = new EditOperation(i-1, j-1, sust);
					} else if (_del <= _sus && _del <=_ins) {
						m[i][j] = _del;
						eo = new EditOperation(i-1, EditOperation.EMPTY, _delcost);
					} else {
						m[i][j] = _ins;
						eo = new EditOperation(EditOperation.EMPTY, j-1, _inscost);
					}					
					this.editScript.addEditOperation(eo);
				}*/
					
			}		
				
		if (this.isNormalizeResult()) {
			res = m[vquery.length][vdocument.length]/(double)(Math.max(vquery.length,vdocument.length));
		} else {
			res = m[vquery.length][vdocument.length];
		}
		
		// backtrace
		if (this.traceEditScript) {
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
		
		//res = m[vquery.size()][vdocument.size()]/(double)(vquery.size() + vdocument.size());
		//res = m[a.size()][b.size()];
		//res = m[vquery.size()][vdocument.size()];

		
		/*System.err.println(vquery.toString());
		System.err.println(vdocument.toString());
		System.err.println("Dist = " + res);
		
		for (i=0; i<m.length; i++) {
			for (j=0; j<m[i].length; j++) {
				System.out.print(m[i][j] + "\t");
			}
			System.out.println();
		}*/
		return -res;
	
	}

    protected double distance(Type a, Type b) throws IM3Exception {
        return this.symbolComparer.computeSymbolDistance(a, b);
    }


    protected double insertCost(Type symbol) throws IM3Exception {
        return symbolComparer.computeInsertCost(symbol);
    }
    protected double deleteCost(Type symbol) throws IM3Exception {
        return symbolComparer.computeDeleteCost(symbol);
    }

    public EditScript getEditScript() {
        return this.editScript;
    }

    public double[][] getDynammicProgrammicTable() {
        return dynammicProgrammicTable;
    }


    public boolean isNormalizeResult() {
        return normalizeResult;
    }

    public void setNormalizeResult(boolean normalizeResult) {
        this.normalizeResult = normalizeResult;
    }

    public void printEditString() {
		if (!this.traceEditScript) {
			System.out.println("The trace is not on");
		} else {			
			System.out.println("Accumulated cost: " + editScript.getAccumulatedCost());
			/*System.out.println("For strings of sizes " + vquery.size() + " and " + vdocument.size());
			System.out.println(editScript.toString());*/
			ArrayList<EditOperation> scr = editScript.getScript();
			for (int i=scr.size()-1; i>=0; i--) {
				EditOperation eo = scr.get(i);				
				Type from = eo.getFrom()!=EditOperation.EMPTY?  this.vquery[eo.getFrom()]:null;
				Type to = eo.getTo()!=EditOperation.EMPTY ? this.vdocument[eo.getTo()]:null;
				System.out.print(from!=null?from.toString():"-");
				System.out.print("\t");
				System.out.print(to!=null?to.toString():"-");
				System.out.print("\t");
				System.out.println(eo.getCost());			
			}
		}
	}
}
