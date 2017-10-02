package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.TestFileUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ProbabilisticAutomatonTest {
    @Test
    public void verySimplePA() throws Exception {
        HashSet<State> states = new HashSet<>();
        State s1 = new State(1, "S1");
        State s2 = new State(2, "S2");
        State s3 = new State(3, "S3");
        states.add(s1);
        states.add(s2);
        states.add(s3);

        HashMap<State, Fraction> startProbabilities = new HashMap<>();
        startProbabilities.put(s1, Fraction.ONE);
        startProbabilities.put(s2, Fraction.ZERO);
        startProbabilities.put(s3, Fraction.ZERO);

        HashSet<State> acceptStates = new HashSet<>(Arrays.asList(s3));
        Set<Transition<State, String>> deltas = new HashSet<>();
        deltas.add(new Transition<>(s1, "a", s2, Fraction.ONE));
        deltas.add(new Transition<>(s2, "b", s3, Fraction.ONE));

        Alphabet<String> alphabet = new Alphabet<>(new HashSet<>(Arrays.asList("a", "b", "c")));

        ProbabilisticAutomaton automaton = new ProbabilisticAutomaton(states, startProbabilities, acceptStates, alphabet, deltas);

        automaton.writeDot(TestFileUtils.createTempFile("pa.dot"));
        //equals(Fraction.ONE, automaton.run());

    }

}