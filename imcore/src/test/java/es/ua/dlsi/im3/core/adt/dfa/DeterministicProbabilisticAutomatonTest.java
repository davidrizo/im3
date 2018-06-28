package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.TestFileUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;

public class DeterministicProbabilisticAutomatonTest {
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

        HashMap<State, Fraction> endProbabilities = new HashMap<>();
        endProbabilities.put(s1, Fraction.ZERO);
        endProbabilities.put(s2, Fraction.ZERO);
        endProbabilities.put(s3, Fraction.ONE);

        Set<Transition<State, StringSymbolType>> deltas = new HashSet<>();
        deltas.add(new Transition<>(s1, new StringSymbolType("a"), s2, Fraction.ONE));
        deltas.add(new Transition<>(s2, new StringSymbolType("b"), s3, Fraction.ONE));

        Alphabet<StringSymbolType> alphabet = new Alphabet<>(new HashSet<>(Arrays.asList(
                new StringSymbolType("a"),
                new StringSymbolType("b"),
                new StringSymbolType("c"))));

        DeterministicProbabilisticAutomaton automaton = new DeterministicProbabilisticAutomaton(states, s1, endProbabilities, alphabet, deltas);
        automaton.normalizeProbabilities();
        automaton.writeDot(TestFileUtils.createTempFile("pa.dot"));
        List<Token<StringSymbolType>> sequence = Arrays.asList(new Token<>(new StringSymbolType("a")), new Token<>(new StringSymbolType("b")));
        assertEquals(1, automaton.probabilityOf(sequence, new SingleTransductionFactory()).getProbability().getNumeratorAsLong());
    }

}