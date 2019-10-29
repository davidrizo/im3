package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.TestFileUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

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
        Transduction okTransduction = automaton.probabilityOf(sequence, new SingleTransductionFactory());
        assertEquals(1, okTransduction.getProbability().getNumeratorAsLong());
        assertEquals(2, okTransduction.getAcceptedTokensCount());

        List<Token<StringSymbolType>> incorrectSequence = Arrays.asList(new Token<>(new StringSymbolType("a")),
                new Token<>(new StringSymbolType("c")),
                new Token<>(new StringSymbolType("b")));

        Transduction incorrectTransduction = automaton.probabilityOf(incorrectSequence, new SingleTransductionFactory());
        assertEquals("Incorrect transduction p", 0, incorrectTransduction.getProbability().getNumeratorAsLong());
        System.out.println(incorrectTransduction.getErrorMessage());
        assertEquals(1, incorrectTransduction.getAcceptedTokensCount());

    }



}
