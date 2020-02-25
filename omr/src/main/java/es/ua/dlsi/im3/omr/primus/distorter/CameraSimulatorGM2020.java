package es.ua.dlsi.im3.omr.primus.distorter;

import es.ua.dlsi.im3.core.utils.FileUtils;
import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.gm4java.im4java.GMBatchCommand;
import org.gm4java.im4java.GMOperation;
import org.im4java.core.GMOps;
import org.im4java.core.IM4JavaException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * It takes an image and distorts it as if it where taken from a mobile phone camera.
 * OLD: We use the ImageMagick library to do it
 * NOW: We use GraphicsMagick because it is much faster - USED for new polyphonic corpus in 2020
 * @autor drizo
 */
public class CameraSimulatorGM2020 {
    //TODO Poner como properties configurable

    private static final String TEXTURAS = "/Users/drizo/Documents/investigacion/PRIMUS/camera-distorted/texturas/";
    public static final String DISTORTED_JPG = "_distorted.jpg";
    public static final String DISTORTED_TXT = "_distorted.txt";
    public static final String SEP = "/";
    public static final String SYMBOLS = "symbols";
    private final String magicGraphicsBinPath;
    private final PooledGMService gmService;
    GMConnectionPoolConfig gmConnectionPoolConfig;
    /////private final String imageMagicBinPath;

    /**
     *
     * @param magicGraphicsBinPath
     */
    public CameraSimulatorGM2020(String magicGraphicsBinPath) {
        /////this.imageMagicBinPath=imageMagicBinPath;
        ///Users/drizo/apps/GraphicsMagick-1.3.28/bin/
        /*if (imageMagicBinPath != null) {
            ProcessStarter.setGlobalSearchPath(imageMagicBinPath);
        }*/

        this.magicGraphicsBinPath = magicGraphicsBinPath;
        gmConnectionPoolConfig = new GMConnectionPoolConfig();
        gmConnectionPoolConfig.setLifo(false);
        gmService = new PooledGMService(gmConnectionPoolConfig);

        /// gmConnectionPoolConfig.setMaxActive(100);
    }


    private double randDouble(Random random, double from, double to) {
        double rand = random.nextDouble();
        double result = rand  * (to-from);// - from;
        result += from;
        /*if (from < 0) {
            result += from;
        } else {
            result -= from;
        }*/
        return result;
    }

    private int randInt(Random random, int from, int to) {
        int result = random.nextInt(to-from);
        result -= from;
        if (from < 0) {
            result -= from;
        } else {
            result += from;
        }
        return result;
    }

    Random randomMotionBlur = new Random();
    Random randomMotionBlurVar1 = new Random();
    Random randomMotionBlurVar2 = new Random();
    Random randomMotionBlurVar3 = new Random();

    Random randomChop = new Random();
    Random randomChopVar1 = new Random();
    Random randomChopVar2 = new Random();
    Random randomChopVar3 = new Random();
    Random randomChopVar4 = new Random();

    Random randomMedian = new Random();
    Random randomMedianVar1 = new Random();

    Random randomRotate = new Random();
    Random randomRotateVar1 = new Random();

    Random randomImplode = new Random();
    Random randomImplodeVar1 = new Random();

    Random randomWave = new Random();
    Random randomWaveVar1 = new Random();
    Random randomWaveVar2 = new Random();

    Random randomShade = new Random();
    Random randomShadeVar1 = new Random();
    Random randomShadeVar2 = new Random();

    Random randomShear = new Random();
    Random randomShearVar1 = new Random();
    Random randomShearVar2 = new Random();

    Random randomSwirl = new Random();
    Random randomSwirlVar1 = new Random();

    Random randomNoise = new Random();
    Random randomNoiseVar1 = new Random();

    Random randomSpreadVerticalArtifacts = new Random();
    Random randomSpread = new Random();


    Random randomSketch = new Random();

    Random randomBarrel = new Random();
    Random randomBarrelVar1 = new Random();
    String barrel1 = "0.0001 0.0001 0.0001";
    String barrel2 = "0.0002 0.0002 0.0002";

    Random randomVignette = new Random();
    Random randomVignetteVar1 = new Random();
    Random randomVignetteVar2 = new Random();
    Random randomVignetteVar3 = new Random();
    Random randomVignetteVar4 = new Random();

    private void log(StringBuilder sb, String command, Object ... variables) {
        sb.append(command);
        for (Object var: variables) {
            sb.append(',');
            sb.append(var);
        }
        sb.append('\n');
    }

    private void randomEffects(String image, String output, File distortionDescriptionOutputFile) throws InterruptedException, IOException, IM4JavaException {
        StringBuilder effects = new StringBuilder();
        GMOperation op = new GMOperation();
        op.addImage(image);

        File tmpFile = File.createTempFile(FileUtils.getFileWithoutPathOrExtension(image), "_dist.jpg");
        tmpFile.deleteOnExit();

        GMOps gmOps = op;

        if (randomImplode.nextGaussian() < 1) {
            double var1 = randDouble(randomImplodeVar1, 0, 0.07);
            log(effects,"Implode", var1);
            gmOps = gmOps.implode(var1);
        }

        if (randomChop.nextGaussian() < 1) {
            int var1 =randInt(randomChopVar1, 1, 5); // izq;
            int var2 = randInt(randomChopVar2, 1, 6); // top
            int var4 = randInt(randomChopVar3, 1, 300);
            int var3 = randInt(randomChopVar4, 1, 50);

            gmOps = gmOps.chop(var1,  var2,
                    var4-var3, // corta izq
                    var3); // separa pentagrama diferente

            log(effects,"Chop", var1, var2, var3, var4);
        }

        if (randomSwirl.nextGaussian() < 1) {
            double var1 = randDouble(randomSwirlVar1, -3.0, 3.0);
            log(effects,"Swirl", var1);
            gmOps = gmOps.swirl(var1);
        }

        if (randomSpreadVerticalArtifacts.nextGaussian() < 1) {
            log(effects,"Spread negative - vertical artifacts");
            gmOps = gmOps.spread(-2);
        }

        if (randomShear.nextGaussian() < 1) {
            double var1 = randDouble(randomShearVar1, -5.0, 5.0);
            double var2 = randDouble(randomShearVar2, -1.5, 1.5);
            log(effects,"Shear", var1, var2);
            gmOps = gmOps.shear(var1, var2);
        }

        if (randomShade.nextGaussian() < 1) {
            double var1 = randDouble(randomShadeVar1, 0, 120);
            double var2 = randDouble(randomShadeVar2, 80, 110);
            log(effects,"Shade", var1, var2);
            gmOps = gmOps.p_shade(var1, var2);
        }

        if (randomWave.nextGaussian() < 1) {
            double var1 = randDouble(randomWaveVar1, 0, 0.5);
            double var2 = randDouble(randomWaveVar2, 0, 0.4);
            log(effects,"Wave", var1, var2);
            gmOps = gmOps.wave(var1, var2);
        }

        if (randomSpread.nextGaussian() < 1) {
            log(effects,"Spread");
            gmOps = gmOps.spread(1);
        }

        if (randomRotate.nextGaussian() < 1) {
            double var1 = randDouble(randomRotateVar1, 0, 0.3);
            log(effects,"Rotate", var1);
            gmOps = gmOps.rotate(var1);
        }

        if (randomNoise.nextGaussian() < 1) {
            double var1 = randDouble(randomNoiseVar1, 0, 1.2);
            log(effects,"Noise", var1);
            gmOps = gmOps.noise(var1);
        }

        if (randomWave.nextGaussian() < 1) {
            double var1 = randDouble(randomWaveVar1, 0, 0.5);
            double var2 = randDouble(randomWaveVar2, 0, 0.4);
            log(effects,"Wave", var1, var2);
            gmOps = gmOps.wave(var1, var2);
        }

        if (randomMotionBlur.nextGaussian() < 1) {
            double var1 = randDouble(randomMotionBlurVar1, -7, 5);
            double var2 = randDouble(randomMotionBlurVar2, -7, 7);
            double var3 = randDouble(randomMotionBlurVar3, -7, 6);
            log(effects,"Motion blur", var1, var2, var3);
            gmOps = gmOps.motionBlur(var1, var2, var3);
        }


        if (randomMedian.nextGaussian() < 1) {
            double var1 = randDouble(randomMedianVar1, 0, 1.1);
            log(effects,"Median", var1);
            gmOps = gmOps.median(var1);
        }

        //System.out.println(effects.toString());
        gmOps.addImage(output);
        //op.addImage(output);
        // execute the operation
        GMBatchCommand command = new GMBatchCommand(gmService, "convert");
        command.run(gmOps);

        if (distortionDescriptionOutputFile != null) {
            PrintStream ps = new PrintStream(distortionDescriptionOutputFile);
            ps.println(effects.toString());
            ps.close();
        }
    }

    private void run(String input) throws Exception {
        File inputFolder = new File(input);
        if (!inputFolder.exists()) {
            throw new Exception("File " + inputFolder.getAbsolutePath() + " does not exist");
        }

        ArrayList<File> pngFiles = new ArrayList<>();
        FileUtils.readFiles(inputFolder, pngFiles,"png");
        int i=1;
        int n = pngFiles.size();

        ExecutorService executor = Executors.newWorkStealingPool();
        List<Callable<String>> callables = new ArrayList<>();

        for (File inputFile: pngFiles) {
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String s = "Processing ";
                    //System.out.println(s + i + SEP + n);
                    //i++;

                    String name = FileUtils.getFileNameWithoutExtension(inputFile.getName());
                    if (!name.endsWith(SYMBOLS)) {
                        File output = new File(inputFile.getParent(), name + DISTORTED_JPG);
                        File outputDescription = new File(inputFile.getParent(), name + DISTORTED_TXT);

                        //System.out.println("----- " + inputFile.getName() + "-------");
                        boolean notGenerated = true;
                        int retries = 0;
                        while (notGenerated && retries < 5) {
                            try {
                                randomEffects(inputFile.getAbsolutePath(), output.getAbsolutePath(), outputDescription);
                                notGenerated = false;
                            } catch (FileNotFoundException e) {
                                System.err.println("Cannot generate because of " + e.getMessage());
                                System.exit(1);
                            } catch (Exception e) {
                               // e.printStackTrace();
                                System.err.println("Regenerating because of " + e.getMessage());
                            }
                            retries++;
                        }
                        if (retries == 5) {
                            System.err.println("NOT GENERATED, too many retries");
                        }
                    }
                    return inputFile.getName();
                }
            };
            callables.add(callable);
        }
        AtomicInteger pending = new AtomicInteger(n-1);
        executor.invokeAll(callables)
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    }
                    catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(s -> {
                            System.out.println("Remaining " + (pending.getAndDecrement()));
                        }
                );
    }


    public static final void main(String [] args) throws Exception {
        //new CameraSimulatorGM2020("/usr/local/bin/gm").run("/tmp/xx/imagenes");

        if (args.length != 2) {
            System.err.println("Usage: <GraphicsMagick bin path of executable (usually /usr/local/bin/gm) > <images folder>");
            return;
        }

        new CameraSimulatorGM2020(args[0]).run(args[1]);
    }
}
