package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.TestFileUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class NonDeterministicProbabilisticAutomatonTest {
    @Test
    public void verySimplePA() throws Exception {
        HashSet<State> states = new HashSet<>();
        State s1 = new State(1, "S1");
        State s2_1 = new State(2, "S2.1");
        State s2_2 = new State(3, "S2.2");
        State s2_3 = new State(4, "S2.3");
        State s3 = new State(5, "S3");
        states.add(s1);
        states.add(s2_1);
        states.add(s2_2);
        states.add(s2_3);
        states.add(s3);

        HashMap<State, Fraction> startProbabilities = new HashMap<>();
        startProbabilities.put(s1, Fraction.ONE);
        startProbabilities.put(s2_1, Fraction.ZERO);
        startProbabilities.put(s2_2, Fraction.ZERO);
        startProbabilities.put(s2_3, Fraction.ZERO);
        startProbabilities.put(s3, Fraction.ZERO);

        HashMap<State, Fraction> endProbabilities = new HashMap<>();
        endProbabilities.put(s1, Fraction.ZERO);
        endProbabilities.put(s2_1, Fraction.ZERO);
        endProbabilities.put(s2_2, Fraction.ZERO);
        endProbabilities.put(s2_3, Fraction.ZERO);
        endProbabilities.put(s3, Fraction.ONE);

        Set<Transition<State, StringSymbolType>> deltas = new HashSet<>();
        deltas.add(new Transition<>(s1, new StringSymbolType("a"), s2_1, new Fraction(1,8)));
        deltas.add(new Transition<>(s1, new StringSymbolType("a"), s2_2, new Fraction(5,8)));
        deltas.add(new Transition<>(s1, new StringSymbolType("a"), s2_3, new Fraction(2,8)));
        deltas.add(new Transition<>(s2_1, new StringSymbolType("b"), s3, Fraction.ONE));
        deltas.add(new Transition<>(s2_2, new StringSymbolType("b"), s3, Fraction.ONE));
        deltas.add(new Transition<>(s2_3, new StringSymbolType("b"), s3, Fraction.ONE));

        Alphabet<StringSymbolType> alphabet = new Alphabet<>(new HashSet<>(Arrays.asList(
                new StringSymbolType("a"),
                new StringSymbolType("b"),
                new StringSymbolType("c"))));

        NonDeterministicProbabilisticAutomaton automaton = new NonDeterministicProbabilisticAutomaton(states, s1, endProbabilities, alphabet, deltas);
        automaton.normalizeProbabilities();
        automaton.writeDot(TestFileUtils.createTempFile("ndpa.dot"));
        List<Token<StringSymbolType>> sequence = Arrays.asList(new Token<>(new StringSymbolType("a")), new Token<>(new StringSymbolType("b")));
        Transduction okTransduction = automaton.probabilityOf(sequence, new SingleTransductionFactory());
        assertEquals(5, okTransduction.getProbability().getNumeratorAsLong());
        assertEquals(8, okTransduction.getProbability().getDenominatorAsLong());

        assertEquals(2, okTransduction.getAcceptedTokensCount());

        List<Token<StringSymbolType>> incorrectSequence = Arrays.asList(new Token<>(new StringSymbolType("a")),
                new Token<>(new StringSymbolType("c")),
                new Token<>(new StringSymbolType("b")));

        Transduction incorrectTransduction = automaton.probabilityOf(incorrectSequence, new SingleTransductionFactory());
        assertEquals("Incorrect transduction p", 0, incorrectTransduction.getProbability().getNumeratorAsLong());
        System.out.println(incorrectTransduction.getErrorMessage());
    }



}
