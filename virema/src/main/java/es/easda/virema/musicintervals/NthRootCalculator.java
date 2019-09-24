package es.easda.virema.musicintervals;

// https://www.baeldung.com/java-nth-root
public class NthRootCalculator {
    public Double calculate(double base, double n) {
        return Math.pow(Math.E, Math.log(base)/n);
    }
}
