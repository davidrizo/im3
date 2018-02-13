package es.ua.dlsi.im3.omr.transduction;

public class AgnosticToSemanticTransducerFactory {
    private static AgnosticToSemanticTransducerFactory ourInstance = new AgnosticToSemanticTransducerFactory();

    public static AgnosticToSemanticTransducerFactory getInstance() {
        return ourInstance;
    }

    private AgnosticToSemanticTransducerFactory() {
    }

    public IAgnosticToSemanticTransducer create() {
        //TODO NotationType...
        return new DummySpanishMensuralAgnosticToSemanticTransducer();
    }
}
