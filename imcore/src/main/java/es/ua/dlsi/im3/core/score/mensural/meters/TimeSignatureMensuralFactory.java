package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.IM3RuntimeException;

public class TimeSignatureMensuralFactory {
    public static TimeSignatureMensuralFactory instance = null;

    public static final TimeSignatureMensuralFactory getInstance() {
        synchronized (TimeSignatureMensuralFactory.class) {
            if (instance == null) {
                instance = new TimeSignatureMensuralFactory();
            }
        }
        return instance;
    }

    public TimeSignatureMensural create(Perfection modusMaior, Perfection modusMinor, Perfection tempus, Perfection prolatio) {
        TimeSignatureMensural result;

        switch (tempus) {
            case imperfectum:
                if (prolatio == Perfection.imperfectum) {
                    result = new TempusImperfectumCumProlationeImperfecta();
                } else if (prolatio == Perfection.perfectum) {
                    result = new TempusImperfectumCumProlationePerfecta();
                } else {
                    throw new IM3RuntimeException("Invalid prolatio: " + prolatio);
                }
                break;
            case perfectum:
                if (prolatio == Perfection.imperfectum) {
                    result = new TempusPerfectumCumProlationeImperfecta();
                } else if (prolatio == Perfection.perfectum) {
                    result = new TempusPerfectumCumProlationePerfecta();
                } else {
                    throw new IM3RuntimeException("Invalid prolatio: " + prolatio);
                }
                break;
            default:
                throw new IM3RuntimeException("Invalid tempus: " + tempus);
        }

        if (modusMaior != null) {
            result.setModusMaior(modusMaior);
        }
        if (modusMinor != null) {
            result.setModusMinor(modusMinor);
        }
        return result;
    }

}
