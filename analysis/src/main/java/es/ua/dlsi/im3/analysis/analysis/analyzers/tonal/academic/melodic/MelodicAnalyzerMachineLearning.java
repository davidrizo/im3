package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;


import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.ImporterExporter;
import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.TonalAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.core.utils.SonoritySegmenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Poder aprender de más cosas que de un MusicXML
public abstract class MelodicAnalyzerMachineLearning extends MelodicAnalyzer {
    // only used from tests
    //static final String TRAINFOLDER = "traindata/placido";
    protected static final Logger logger = Logger.getLogger(MelodicAnalyzerMachineLearning.class.getName());
    protected MelodicAnalyzerFeaturesExtractor featuresExtractor;
    boolean learnt = false;

    protected MelodicAnalyzerMachineLearning(String name, double authority) {
        super(name, authority);
        featuresExtractor = new MelodicAnalyzerFeaturesExtractor(
                MelodicFeatures.PREV_INTERVAL_NAME, MelodicFeatures.PREV_INTERVAL_MODE, MelodicFeatures.PREV_INTERVAL_DIR,
                MelodicFeatures.NEXT_INTERVAL_NAME, MelodicFeatures.NEXT_INTERVAL_MODE, MelodicFeatures.NEXT_INTERVAL_DIR,
                MelodicFeatures.TIED_FROM_PREVIOUS,
                //20160314 MelodicFeatures.DURATION, 
                MelodicFeatures.RATIO,
                MelodicFeatures.INSTABILITY, MelodicFeatures.NEXT_INSTABILITY, MelodicFeatures.METER_NUMERATOR
        );
    }

    public MelodicAnalyzerFeaturesExtractor getFeaturesExtractor() {
        return featuresExtractor;
    }



    protected HashMap<AtomPitch, NoteMelodicAnalysisFeatures> computeFeatures(ScoreSong lsong, List<Segment> sonorities, TonalAnalysis tonalAnalysis) throws MelodicAnalysisException {
        return featuresExtractor.computeFeatures(lsong, sonorities, tonalAnalysis);
    }



    /**
     * It excludes the current song (used in constructor) of the learning
     * @param trainFolder
     * @param excludeSong Used for leave one out. It may be null
     * @throws MelodicAnalysisException
     * @throws ImportException
     */
    public void learn(File trainFolder, ScoreSong excludeSong) throws MelodicAnalysisException, ImportException, IOException {
        ArrayList<File> fileList = new ArrayList<>();
        FileUtils.readFiles(trainFolder, fileList, "xml");
        File [] v = new File[fileList.size()];
        for (int i=0; i<v.length; i++) {
            v[i] = fileList.get(i);
        }
        learn(v, excludeSong);
    }

    //TODO Que pueda importar y exportar de cualquier tipo
    /**
     * It excludes the current song (used in constructor) of the learning
     * @param fileList
     * * @param excludeSong Used for leave one out
     * @throws MelodicAnalysisException
     * @throws ImportException
     */
    public void learn(File [] fileList, ScoreSong excludeSong) throws MelodicAnalysisException, ImportException {
        initLearning();
        for (File file : fileList) {
            ImporterExporter io = new ImporterExporter();
            try {
                ScoreSong lsong = io.readMusicXML(file, false);
                // if (excludeSong != null) {
                //System.out.println(excludeSong.getTitle() + " vs " + lsong.getTitle());
                //}
                if (excludeSong != null && excludeSong.getTitle() != null && excludeSong.getTitle().equals(lsong.getTitle())) {
                    logger.log(Level.INFO, "Excluding {0} in the learn method", excludeSong.getTitle());
                } else {
                    //System.out.println(lsong.getTitle());
                    logger.log(Level.INFO, "Learning from ", lsong.getTitle());
                    TonalAnalysis tonalAnalysis = new TonalAnalysis(lsong);
                    tonalAnalysis.loadFromSong();
                    List<Segment> sonorities = new SonoritySegmenter().segmentSonorities(lsong);
                    HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = computeFeatures(lsong, sonorities, tonalAnalysis);
                    readTrainingFeatures(lsong, sonorities, features, tonalAnalysis);
                }
            } catch (Exception e) {
                throw new ImportException("Error reading file " + file.getAbsolutePath(), e);
            }
        }

        learnWithReadTrainingFeatures();
        learnt = true;
    }


    public void learn(List<ScoreSong> songs, ScoreSong excludeSong) throws ImportException, MelodicAnalysisException {
        initLearning();
        for (ScoreSong lsong: songs) {
            ImporterExporter io = new ImporterExporter();
            try {
                //if (excludeSong != null) {
                //		System.out.println(excludeSong.getTitle() + " vs " + lsong.getTitle());
                //}
                if (excludeSong != null && excludeSong.getTitle() != null && excludeSong.getTitle().equals(lsong.getTitle())) {
                    logger.log(Level.INFO, "Excluding {0} in the learn method", excludeSong.getTitle());
                } else {
                    logger.log(Level.INFO, "Learning from ", lsong.getTitle());
                    TonalAnalysis tonalAnalysis = new TonalAnalysis(lsong);
                    tonalAnalysis.loadFromSong();
                    List<Segment> sonorities = new SonoritySegmenter().segmentSonorities(lsong);
                    HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = computeFeatures(lsong, sonorities, tonalAnalysis);
                    readTrainingFeatures(lsong, sonorities, features, tonalAnalysis);
                }
            } catch (Exception e) {
                throw new ImportException("Error with song " + lsong.getTitle(), e);
            }
        }

        learnWithReadTrainingFeatures();
        learnt = true;
    }


    /**
     * It excludes the current song (used in constructor) of the learning
     * @param fileList
     * * @param excludeSong Used for leave one out
     * @throws MelodicAnalysisException
     * @throws IM3Exception
     * @throws ImportException
     */
    public void learn(List<File> fileList, File excludeSong) throws MelodicAnalysisException, IM3Exception, ImportException {
        initLearning();
        for (File file : fileList) {
            ImporterExporter io = new ImporterExporter();
            try {
                ScoreSong lsong = io.readMusicXML(file, false);
                if (excludeSong != null) {
                    System.out.println(excludeSong.getName() + " vs " + file.getName());
                }
                if (excludeSong != null && excludeSong.equals(file)) {
                    logger.log(Level.INFO, "Excluding {0} in the learn method", excludeSong.getName());
                } else {
                    System.out.println(lsong.getTitle());
                    TonalAnalysis tonalAnalysis = new TonalAnalysis(lsong);
                    tonalAnalysis.loadFromSong();

                    List<Segment> sonorities = new SonoritySegmenter().segmentSonorities(lsong);
                    HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = computeFeatures(lsong, sonorities, tonalAnalysis);
                    readTrainingFeatures(lsong, sonorities, features, tonalAnalysis);
                }
            } catch (Exception e) {
                throw new ImportException("Error reading file " + file.getAbsolutePath(), e);
            }
        }

        learnWithReadTrainingFeatures();
        learnt = true;
    }

    @Override
    public MelodicAnalysis melodicAnalysis(ScoreSong song, IProgressObserver o, List<Segment> alreadyComputedSonorities) throws MelodicAnalysisException {
		/*logger.info("Training using the default training database in "+ TRAINFOLDER);
		try {
			if (o != null) {
				o.logText("Learning model from traindata");
			}
			learn(new File(TRAINFOLDER));
		} catch (ImportException e) {
			throw new MelodicAnalysisException(e);
		} */
        //2017 if (recomputeFeatures) 
        //{
            TonalAnalysis tonalAnalysis = new TonalAnalysis(song);
            HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = computeFeatures(song, alreadyComputedSonorities, tonalAnalysis);
        //}


        MelodicAnalysis ma = doMelodicAnalysis(song, alreadyComputedSonorities, features, o);

        if (o!=null) {
            o.onEnd();
        }
        return ma;
    }
    protected abstract void initLearning();
    protected abstract void readTrainingFeatures(ScoreSong lsong, List<Segment> sonorities, HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features, TonalAnalysis tonalAnalysis) throws IM3Exception, MelodicAnalysisException;
    protected abstract void learnWithReadTrainingFeatures() throws MelodicAnalysisException;
    protected abstract MelodicAnalysis doMelodicAnalysis(ScoreSong song, List<Segment> sonorities, HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features, IProgressObserver o) throws MelodicAnalysisException;

    public abstract void saveLearntModel(File file) throws MelodicAnalysisException;
    public abstract void loadLearntModel(File file) throws MelodicAnalysisException;
}
