package es.ua.dlsi.im3.omr.imageprocessing;

/**
 * @autor drizo
 */
public class StaffNormalizerFactory {
    private static StaffNormalizerFactory instance = null;

    private StaffNormalizerFactory() {
    }

    public static StaffNormalizerFactory getInstance() {
        if (instance == null) {
            instance = new StaffNormalizerFactory();
        }
        return instance;
    }

    public IStaffNormalizer create() {
        //return new JCalvoStaffNormalizer();
        return new CardosoStaffNormalizer();
    }
}
