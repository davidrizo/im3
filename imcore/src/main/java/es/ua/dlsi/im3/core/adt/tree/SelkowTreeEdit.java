package es.ua.dlsi.im3.core.adt.tree;

/**
 * This class computes the selkow editing distance between two trees
 * @author drizo
 */
public class SelkowTreeEdit {
	/**
	 * String for trace
	 */
	private static final String TRACE_REMOVE = "\n->remove subtree rooted at ";
	/**
	 * String for trace
	 */
	private static final String TRACE_INSERT = "\n->insert subtree rooted at ";
	/**
	 * String for trace
	 */
	private static final String TRACE_SUBST = "\n->subst-";
	/**
	 * Left param
	 */
	private static final String LEFT_PAR = "(";
	/**
	 * Comma
	 */
	private static final String COMMA = ",";
	/**
	 * Position
	 */
	private static final String POSITION = " position ";
	/**
	 * Level
	 */
	private static final String LEVEL = " level ";
	/**
	 * In
	 */
	private static final String IN = " in ";
	/**
	 * In
	 */
	private static final String COST = " cost ";
	/**
	 * Right param
	 */
	private static final String RIGHT_PAR = ")";
	/**
	 * The substitution cost
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	protected double substitutionCost(ITreeLabel a, ITreeLabel b) {
		if (a.equals(b)) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * Compute the tree editing distance
	 * @param a first tree
	 * @param b second tree
	 * @return double tempo of distance
	 * @throws Exception 
	 */
	public double treeEditDistance(Tree<?> a, Tree<?> b) throws Exception {
		// only precompute the size in nodes of both trees. We do it this way to avoid the
		// recursive calculation of the same tempo several times
		a.computeSizeAndIndex();
		b.computeSizeAndIndex();
		return computeTreeEditDistance(a, b);
	}
	/**
	 * Compute the tree editing distance and return a string explaining the trace
	 * @param a first tree
	 * @param b second tree
	 * @param trace Buffer where the trace will be put
	 * @return double tempo of distance
	 * @throws Exception 
	 */
	public  double treeEditDistance(Tree<?> a, Tree<?> b, StringBuffer trace) throws Exception {
		// only precompute the size in nodes of both trees. We do it this way to avoid the
		// recursive calculation of the same tempo several times
		a.computeSizeAndIndex();
		b.computeSizeAndIndex();
		return computeTreeEditDistance(a, b, trace);
	}
	
	/**
	 * Min among three numbers
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static final double min(double x, double y, double z) {
		return  x<y? (x<z?x:z) : (y<z?y:z);
	}
	/**
	 * Perform the edit distance
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	private double computeTreeEditDistance(Tree<?> a, Tree<?> b) throws Exception {
		int arityA = a.getNumChildren();
		int arityB = b.getNumChildren();
		double [][] d = new double[arityA + 1][arityB + 1];
		
		d[0][0] = substitutionCost(a.getLabel(), b.getLabel());
		/*for (int i=1; i<=arityA; i++) {
			d[i][0] = d[i-1][0] + a.getChild(i-1).getPrecomputedSize(); // delete a (cost = num.nodes a)			
		}

		for (int j=1; j<=arityB; j++) {
			d[0][j] = d[0][j-1] + b.getChild(j-1).getPrecomputedSize();	// insert b (cost = num.nodes b)
		}
		
		for (int i=1; i<=arityA; i++) {
			for (int j=1; j<=arityB; j++) {
				d[i][j] = min(d[i-1][j-1] + computeTreeEditDistance(a.getChild(i-1), b.getChild(j-1)),
						d[i][j-1] + b.getChild(j-1).getPrecomputedSize(),
						d[i-1][j] + a.getChild(i-1).getPrecomputedSize());
			}
		}*/
		
		for (int i=0; i<=arityA; i++) {
			for (int j=0; j<=arityB; j++) {
				double cins = Double.MAX_VALUE;
				double csust = Double.MAX_VALUE;
				double cdel = Double.MAX_VALUE;
				
				if (i>=1) {
					cdel = d[i-1][j] + a.getChild(i-1).getPrecomputedSize(); // delete a (cost = num.nodes a)
				}
				if (j>=1) {
					cins = d[i][j-1] + b.getChild(j-1).getPrecomputedSize();	// insert b (cost = num.nodes b)
				}
				if (i>=1 && j>=1) {
					csust = d[i-1][j-1] + computeTreeEditDistance(a.getChild(i-1), b.getChild(j-1));
				}
				if (i>=1 || j>=1) {
					d[i][j] = min(cdel, cins, csust);
				}
			}
		}	
		/*System.out.println("------" + arityA + " * " + arityB);
		for (int i=0; i<=arityA; i++) {
			for (int j=0; j<=arityB; j++) {
				System.out.print(d[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println("--------------------------");*/
		
		return d[arityA][arityB];
	}
	
	/**
	 * Trace explanation
	 * @param t
	 * @return
	 */
	private  String traceRemove(Tree<?> t, double cost) {
		StringBuffer sb = new StringBuffer();
		sb.append(TRACE_REMOVE);
		sb.append(LEFT_PAR);
		sb.append(t.getLabel().toString());
		sb.append(LEVEL);
		sb.append(t.getLevel());
		sb.append(RIGHT_PAR);
		sb.append(COST);
		sb.append(cost);
		return sb.toString();
	}
	/**
	 * Trace explanation
	 * @param t
	 * @return
	 */
	private  String traceInsert(Tree<?> where, int position, Tree<?> t, double cost) {
		StringBuffer sb = new StringBuffer();
		sb.append(TRACE_INSERT);
		sb.append(LEFT_PAR);
		sb.append(t.getLabel().toString());
		sb.append(LEVEL);
		sb.append(t.getLevel());
		sb.append(RIGHT_PAR);
		sb.append(IN);
		sb.append(LEFT_PAR);
		sb.append(where.getLabel().toString());
		sb.append(RIGHT_PAR);
		sb.append(POSITION);
		sb.append(position);
		sb.append(COST);
		sb.append(cost);
		return sb.toString();
	}
	/**
	 * Trace explanation
	 * @param t
	 * @return
	 */
	private  String traceSubst(Tree<?> t, Tree<?> t2, double cost) {
		StringBuffer sb = new StringBuffer();
		sb.append(TRACE_SUBST);
		sb.append(LEFT_PAR);
		sb.append(t.getLabel().toString());
		sb.append(COMMA);
		sb.append(t2.getLabel().toString());
		sb.append(LEVEL);
		sb.append(t2.getLevel());
		sb.append(RIGHT_PAR);
		sb.append(COST);
		sb.append(cost);
		return sb.toString();
	}

	/**
	 * Perform the edit distance with trace
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	private  double computeTreeEditDistance(Tree<?> a, Tree<?> b, StringBuffer outputTrace) throws Exception {
		int arityA = a.getNumChildren();
		int arityB = b.getNumChildren();
		double [][] d = new double[arityA + 1][arityB + 1];
		String [][] trace = new String[arityA + 1][arityB + 1];
		d[0][0] = substitutionCost(a.getLabel(), b.getLabel());
		trace[0][0] = traceSubst(a, b, d[0][0]);
		
		for (int i=1; i<=arityA; i++) {
			d[i][0] = d[i-1][0] + a.getChild(i-1).getPrecomputedSize(); // delete a (cost = num.nodes a)
			trace[i][0] = trace[i-1][0] + traceRemove(a.getChild(i-1), a.getChild(i-1).getPrecomputedSize()); 
		}

		for (int j=1; j<=arityB; j++) {
			d[0][j] = d[0][j-1] + b.getChild(j-1).getPrecomputedSize();	// insert b (cost = num.nodes b)
			trace[0][j] = trace[0][j-1] + traceInsert(b, 0, b.getChild(j-1), b.getChild(j-1).getPrecomputedSize());
		}
		
		for (int i=1; i<=arityA; i++) {
			for (int j=1; j<=arityB; j++) {
				StringBuffer sb = new StringBuffer();
				double change = d[i-1][j-1] + computeTreeEditDistance(a.getChild(i-1), b.getChild(j-1), sb);
				double delete = d[i-1][j] + a.getChild(i-1).getPrecomputedSize();
				double insert = d[i][j-1] + b.getChild(j-1).getPrecomputedSize(); 
				if (change <= delete && change <= insert) {
					d[i][j] = change;
					trace[i][j] = trace[i-1][j-1] + sb.toString();
				} else if (delete <= insert) {
					d[i][j] = delete;
					trace[i][j] = trace[i-1][j] + traceRemove(a.getChild(i-1), a.getChild(i-1).getPrecomputedSize());
				} else {
					d[i][j] = insert;
					trace[i][j] = trace[i][j-1] + traceInsert(b, i, b.getChild(j-1), b.getChild(j-1).getPrecomputedSize());
				}
			}
		}
		outputTrace.append(trace[arityA][arityB]);
		return d[arityA][arityB];
	}	
}
