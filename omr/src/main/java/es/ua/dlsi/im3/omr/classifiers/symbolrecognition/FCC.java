package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;


import es.ua.dlsi.im3.omr.model.entities.Point;

import java.util.List;

/**
 * Freeman Code distance for strokes distance
 * @autor jcalvo Slightly modified by drizo
 */
public class FCC {
    private static final char BREAK = '0';
    private static final int BREAK_ADDITIONAL_WEIGHT = 0;
    private static double maxValue = 1;


    public double chainCodeDistance(char[] str1, char[] str2) {
        int [][]distance = new int[str1.length+1][str2.length+1];

        for(int i=0;i<=str1.length;i++)
        {
            distance[i][0]=i;
        }
        for(int j=0;j<=str2.length;j++)
        {
            distance[0][j]=j;
        }
        for(int i=1;i<=str1.length;i++)
        {
            for(int j=1;j<=str2.length;j++)
            {
                int insert = distance[i-1][j]+1;
                int deletion = distance[i][j-1]+1;
                int substitution = distance[i-1][j-1];

                if(str1[i-1] != str2[j-1]) {
                    substitution++;

                    if(str1[i-1] == BREAK || str2[j-1] == BREAK) {
                        substitution += BREAK_ADDITIONAL_WEIGHT;
                    }
                }


                distance[i][j]= Math.min(insert,Math.min(deletion,substitution));
            }
        }
        return distance[str1.length][str2.length];
    }


    public static String getChainCode(List<Point> points) {
        StringBuilder chainCode = new StringBuilder();
        Point previous = null;
        for(Point point : points) {
            if(previous == null) {
                chainCode.append(BREAK);
            } else if(!point.equals(previous)) {

                double s_x = previous.getX();
                double s_y = previous.getY();

                double e_x = point.getX();
                double e_y = point.getY();


                // Los puntos iniciales se marcan
                // True todo lo que este en la interpolacion
                double ACC = Math.max( 1 , Math.max( Math.abs(e_x-s_x) , Math.abs(e_y-s_y) ));

                Point a = previous;
                for(int T = 0; T <= ACC; T++) {
                    double t = T/ACC;
                    double cX = (s_x + t*(e_x-s_x));
                    double cY = (s_y + t*(e_y-s_y));

                    Point b = new Point(0, cX, cY);

                    if(!a.equals(b)) {
                        chainCode.append(chainCode(a,b));
                    }
                    a = b;
                }
            }

            previous = point;
        }

        return chainCode.toString();
    }

    private static int chainCode(Point a, Point b) {
        double Ax = b.getX() - a.getX();
        double Ay = b.getY() - a.getY();

        int value = 0;
        if(Ax > 0) {
            value = 1;
        } else if(Ax < 0) {
            value = 2;
        }

        if(Ay > 0) {
            value += 3;
        } else if(Ay < 0)  {
            value += 6;
        }

        return value;
    }

    public double distance(char [] str1, char [] str2) {
        double cdistance = levenshteinDistance(str1,str2);
        maxValue = Math.max(cdistance, maxValue);
        return cdistance;
    }

    public double distance(List<Point> a, List<Point> b) {
        char [] str1 = getChainCode(a).toCharArray();
        char [] str2 = getChainCode(b).toCharArray();
        return distance(str1, str2);
    }


    public int levenshteinDistance (char [] lhs, char [] rhs) {
        int len0 = lhs.length + 1;
        int len1 = rhs.length + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs[i - 1] == rhs[j - 1]) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}