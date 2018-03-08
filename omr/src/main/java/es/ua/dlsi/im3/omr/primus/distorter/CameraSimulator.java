package es.ua.dlsi.im3.omr.primus.distorter;

import es.ua.dlsi.im3.core.utils.FileUtils;
import org.gm4java.engine.GMException;
import org.gm4java.engine.GMServiceException;
import org.im4java.core.*;
import org.im4java.process.ProcessStarter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * It takes an image and distorts it as if it where taken from a mobile phone camera.
 * OLD: We use the ImageMagick library to do it
 * NOW: We use GraphicsMagick because it is much faster
 * @autor drizo
 */
public class CameraSimulator {
    //TODO Poner como properties configurable

    private static final String TEXTURAS = "/Users/drizo/Documents/investigacion/PRIMUS/camera-distorted/texturas/";
    public static final String DISTORTED_JPG = "_distorted.jpg";
    public static final String DISTORTED_TXT = "_distorted.txt";
    public static final String SEP = "/";
    public static final String SYMBOLS = "symbols";
    private final String magicGraphicsBinPath;
    private final String imageMagicBinPath;

    /**
     *
     * @param magicGraphicsBinPath
     * @param imageMagicBinPath Just if ImageMagic is used
     */
    public CameraSimulator(String magicGraphicsBinPath, String imageMagicBinPath) {
        this.imageMagicBinPath=imageMagicBinPath;
        ///Users/drizo/apps/GraphicsMagick-1.3.28/bin/
        if (imageMagicBinPath != null) {
            ProcessStarter.setGlobalSearchPath(imageMagicBinPath);
        }

        this.magicGraphicsBinPath = magicGraphicsBinPath;
    }


    private void imageInfo(String image) throws InfoException {
        /*Info imageInfo = new Info(image,true);
        System.out.println("Format: " + imageInfo.getImageFormat());
        System.out.println("Width: " + imageInfo.getImageWidth());
        System.out.println("Height: " + imageInfo.getImageHeight());
        System.out.println("Geometry: " + imageInfo.getImageGeometry());
        System.out.println("Depth: " + imageInfo.getImageDepth());
        System.out.println("Class: " + imageInfo.getImageClass());*/

    }
   /* private void motionBlur(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("Motion Blur");
        // create command
        ConvertCmd cmd = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.addImage(image);
        op.motionBlur(20.0, 5.0, 10.0);
        op.addImage("/tmp/distortions/motionblur.jpg");
        // execute the operation
        cmd.run(op);
    }*/

    private void motionBlur(String image) throws InterruptedException, IOException, IM4JavaException, GMServiceException, GMException {
        for (double var1 = -7; var1 <= 7; var1+=2) {
            for (double var2 = -7; var2 <= 7; var2+=2) {
                for (double var3 = -7; var3 <= 7; var3+=2) {
                    System.out.println("MB " + var1 + " " + var2 + " " + var3);
                    ConvertCmd cmd = createCommand();
                    GMOperation op = new GMOperation();
                    op.addImage(image);
                    //op.motionBlur(20.0, 5.0, 10.0);
                    op.motionBlur(var1, var2, var3);
                    op.addImage("/tmp/distortions/motionblur_" + var1 + "_" + var2  + "_" + var3 + ".jpg");
                    // execute the operation
                    cmd.run(op);
                }
            }
        }
    }

    private void chop(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("Chop");
        // create command
        //ConvertCmd cmd = new ConvertCmd();
        //IMOperation op = new IMOperation();
        /*for (int a=1; a<7; a+=2) {
            for (int b=1; b<7; b+=2) {
                for (int c=1; c<30; c+=3) {
                    for (int d=1; d<30; d+=3) {
                        ConvertCmd cmd = createCommand();
                        GMOperation op = new GMOperation();

                        op.addImage(image);

                        op.chop(a, // izq
                                b, // top
                                c, // corta izq
                                d); // separa pentagrama diferente
                        op.addImage("/tmp/distortions/chop_" + a + "_" + b + "_" + c + "_" + d + ".jpg");
                        cmd.run(op);
                    }
                }

            }
        }*/

        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();

        op.addImage(image);

        /*op.chop(5, // izq
                5, // top
                50, // corta izq
                60); // separa pentagrama diferente*/


        op.chop(5, // izq
                5, // top
                290, // corta izq
                100); // separa pentagrama diferente


        op.addImage("/tmp/distortions/chop.jpg");
        // execute the operation
        cmd.run(op);
    }

   /*private void charcoal(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("Charcoal");
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.charcoal(5);
        op.addImage(image);
        op.addImage("/tmp/distortions/charcoal.jpg");
        // execute the operation
        cmd.run(op);
    }*/

    private void median(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("median");
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.median(1.1);
        op.addImage("/tmp/distortions/median.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void blur(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("Blur");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.blur(15.0, 2.0);
        op.addImage("/tmp/distortions/blur.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void implode(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("implode");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.implode(0.05);
        op.addImage("/tmp/distortions/implode.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void rotate(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("rotate");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.rotate(0.5);
        op.addImage("/tmp/distortions/rotate.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void wave(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("wave");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.wave(0.4, 0.2);
        op.addImage("/tmp/distortions/wave.jpg");
        // execute the operation
        cmd.run(op);
    }



    private void shade(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("shade");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.p_shade(22.0, 120.0);
        op.addImage("/tmp/distortions/shade.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void shear(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("shear");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.shear(-6.0, -1.5);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/shear.jpg");
        // execute the operation
        cmd.run(op);
    }


    private void modulate(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("modulate");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.modulate(120.0, 90.0, 114.0);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/modulate.jpg");
        // execute the operation
        cmd.run(op);
    }


    private void lat(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("lat");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.lat(10, 20);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/lat.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void swirl(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("swirl");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.swirl(-3.0);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/swirl.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void noise(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("noise");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.noise(0.5);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/noise1.jpg");
        // execute the operation
        cmd.run(op);
    }



    private void spread(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("spread");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.spread(-10); // negative creates interesting artefacts like vertical lines
        //op.spread(1);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/spread.jpg");
        // execute the operation
        cmd.run(op);
    }


    private void sketch(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("sketch");
        // create command
        ConvertCmd cmd = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.addImage(image);
        op.sketch(1.0);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/sketch.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void distortBarrel(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("distortBarrel");
        // create command
        ConvertCmd cmd = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.addImage(image);
        //op.distort("barrel", "0.001181 -0.005581 0.001");

        //op.distort("barrel", "-0.00001 -0.00001 -0.00001");
        op.distort("barrel", "0.0001 0.0001 0.0001");
        op.addImage("/tmp/distortions/distortBarrel.jpg");
        // execute the operation
        cmd.run(op);
    }

    private void vignette(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("vignette");
        // create command
        ConvertCmd cmd = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.addImage(image);
        op.vignette(500.0, 500.0);
        op.contrastStretch(2, 4);
        op.addImage("/tmp/distortions/vignette.jpg");
        // execute the operation
        cmd.run(op);
    }


    /*private void texture(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("texture");
        IMOperation op = new IMOperation();
        op.dissolve(50);
        op.addImage("/tmp/distortions/distortBarrel.jpg");
        op.addImage("/tmp/distortions/texture.jpg");
        //op.blend(50);
        //op.addImage("[300x200+0+0]");  // read and crop first image
        //op.addImage("[300x200+0+0]");  // read and crop second image
        //op.addImage();                 // output image

        CompositeCmd composite = new CompositeCmd();
        //composite.setCommand("/Users/drizo/apps/ImageMagick-7.0.7/bin/composite");
        //composite.run(op,image, "/tmp/distortions/paper1.jpg" ,"/tmp/distortions/texture.jpg");


        composite.run(op);
        //composite.run(op);
    }*/



    private void individualExperiments(String image) throws Exception {
        // imagemagick
        //sketch(image);
        //distortBarrel(image);
        //vignette(image);

        //graphicsmagick
        //motionBlur(image);
        //chop(image);
        //median(image);
       // blur(image);
        //implode(image);
        //wave(image);
        //shade(image);
        //shear(image);
        //modulate(image);
        //swirl(image);
        //noise(image);
        //spread(image);


        //textureGM(image);

        /*
        imageInfo(image);
        bleedThrough(image);
        ///charcoal(image);



        rotate(image);

        //
        //lat(image);
*/
    }

    class DissolveOp extends GMOps {

        public DissolveOp dissolve(String texture, Double percentage) {
            getCmdArgs().add("-dissolve");
            getCmdArgs().add(percentage.toString());
            getCmdArgs().add(texture);
            //this.iCmdArgs.add("-texture");
            //if (var1 != null) {
            //    var3.append(var1.toString());
            //}
            return this;
        }
    }
    private void textureGM(String image) throws InterruptedException, IOException, IM4JavaException {
        CompositeCmd cmd = new CompositeCmd(true);
        //cmd.setCommand("composite -dissolve");
        //MontageCmd cmd = new MontageCmd(true);
        cmd.setSearchPath(magicGraphicsBinPath);
        DissolveOp op = new DissolveOp();
        op.dissolve(TEXTURAS + "paper1.jpg", 40.0);
        op.addImage(image);
        //op.texture(TEXTURAS + "paper1.jpg");
        op.addImage("/tmp/distortions/texture.jpg");
        cmd.run(op);
    }


    private void bleedThrough(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("bleedThrough");
        // create command
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.flop();
        op.noise(2.4);
        op.median(1.3);
        //op.shear(-2.5, -1.1);
        op.addImage("/tmp/distortions/flop.jpg");
        // execute the operation
        cmd.run(op);


        CompositeCmd cc = new CompositeCmd(true);
        //cmd.setCommand("composite -dissolve");
        //MontageCmd cmd = new MontageCmd(true);
        cc.setSearchPath("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/");
        DissolveOp opd = new DissolveOp();
        opd.dissolve("/tmp/distortions/flop.jpg", 3.0);
        opd.addImage(image);
        opd.addImage("/tmp/distortions/bleedThrough.jpg");
        cc.run(opd);
    }

    /*private void experimentsGM(String image) throws InterruptedException, IOException, IM4JavaException {
        System.out.println("---");
        //GraphicsMagickCmd cmd = new GraphicsMagickCmd("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/gmss");
        //GraphicsMagickCmd cmd = new GraphicsMagickCmd("convert");
        ConvertCmd cmd = new ConvertCmd(true);
        cmd.setSearchPath("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/");
        GMOperation op = new GMOperation();
        op.addImage(image);
        op.texture(TEXTURAS + "paper1.jpg");
        op.addImage("/tmp/distortions/sketch.jpg");
        // execute the operation
        cmd.run(op);
    }*/


    private ConvertCmd createCommand() {
        ConvertCmd cmd = new ConvertCmd(true);
        cmd.setSearchPath("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/");
        return cmd;
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
        // first apply ImageMagic filters
        ConvertCmd imcmd = new ConvertCmd();
        IMOperation imop = new IMOperation();
        imop.addImage(image);

        File tmpFile = File.createTempFile(FileUtils.getFileWithoutPathOrExtension(image), "_dist.jpg");
        tmpFile.deleteOnExit();

        boolean imapplied = false;
        /*if (randomSketch.nextGaussian() < 1) {
            imop.sketch(1.0);
            imapplied = true;
            log(effects, "Sketch");
        }

        if (randomBarrel.nextGaussian() < 1) {
            if (randomBarrelVar1.nextGaussian() < 1) {
                imop.distort("barrel", barrel1);
                log(effects, "Sketch", 0.001);
            } else {
                imop.distort("barrel", barrel2);
                log(effects, "Sketch", 0.002);
            }
            imapplied = true;
        }

        if (randomVignette.nextGaussian() < 1) {
            double var1 = randDouble(randomVignetteVar1, 250, 500);
            double var2 = randDouble(randomVignetteVar2, 250, 500);
            int var3 = randInt(randomVignetteVar3, 1, 2);
            int var4 = randInt(randomVignetteVar4, 2, 4);
            log(effects,"Vignette", var1, var2, var3, var4);

            imop.vignette(var1, var2);
            imop.contrastStretch(var3, var4);
            imapplied = true;
        }

*/
        String input;
        if (imapplied) {
            input = tmpFile.getAbsolutePath();
            imop.addImage(input);
            imcmd.run(imop);
            Thread.sleep(100);
        } else {
            input = image;
        }

        // next apply GraphicsMagic
        ConvertCmd cmd = createCommand();
        GMOperation op = new GMOperation();
        op.addImage(input);


        if (randomImplode.nextGaussian() < 1) {
            double var1 = randDouble(randomImplodeVar1, 0, 0.07);
            log(effects,"Implode", var1);
            op.implode(var1);
        }


        if (randomChop.nextGaussian() < 1) {
            int var1 =randInt(randomChopVar1, 1, 5); // izq;
            int var2 = randInt(randomChopVar2, 1, 6); // top
            int var4 = randInt(randomChopVar3, 1, 300);
            int var3 = randInt(randomChopVar4, 1, 50);

            op.chop(var1,  var2,
                    var4-var3, // corta izq
                    var3); // separa pentagrama diferente

            log(effects,"Chop", var1, var2, var3, var4);
        }

        if (randomSwirl.nextGaussian() < 1) {
            double var1 = randDouble(randomSwirlVar1, -3.0, 3.0);
            log(effects,"Swirl", var1);
            op.swirl(var1);
        }

        if (randomSpreadVerticalArtifacts.nextGaussian() < 1) {
            log(effects,"Spread negative - vertical artifacts");
            op.spread(-2);
        }

        if (randomShear.nextGaussian() < 1) {
            double var1 = randDouble(randomShearVar1, -5.0, 5.0);
            double var2 = randDouble(randomShearVar2, -1.5, 1.5);
            log(effects,"Shear", var1, var2);
            op.shear(var1, var2);
        }

        /*if (randomSketch.nextGaussian() < 1) {
            double var1 = randDouble(randomSketchVar1, 0, 7.0);
            log(effects,"Sketch", var1);
            op.ske(var1, var2);
        }      */


        if (randomShade.nextGaussian() < 1) {
            double var1 = randDouble(randomShadeVar1, 0, 120);
            double var2 = randDouble(randomShadeVar2, 80, 110);
            log(effects,"Shade", var1, var2);
            op.p_shade(var1, var2);
        }

        if (randomWave.nextGaussian() < 1) {
            double var1 = randDouble(randomWaveVar1, 0, 0.5);
            double var2 = randDouble(randomWaveVar2, 0, 0.4);
            log(effects,"Wave", var1, var2);
            op.wave(var1, var2);
        }

        if (randomSpread.nextGaussian() < 1) {
            log(effects,"Spread");
            op.spread(1);
        }

        if (randomRotate.nextGaussian() < 1) {
            double var1 = randDouble(randomRotateVar1, 0, 0.3);
            log(effects,"Rotate", var1);
            op.rotate(var1);
        }

        if (randomNoise.nextGaussian() < 1) {
            double var1 = randDouble(randomNoiseVar1, 0, 1.2);
            log(effects,"Noise", var1);
            op.noise(var1);
        }

        if (randomWave.nextGaussian() < 1) {
            double var1 = randDouble(randomWaveVar1, 0, 0.5);
            double var2 = randDouble(randomWaveVar2, 0, 0.4);
            log(effects,"Wave", var1, var2);
            op.wave(var1, var2);
        }

        if (randomMotionBlur.nextGaussian() < 1) {
            double var1 = randDouble(randomMotionBlurVar1, -7, 5);
            double var2 = randDouble(randomMotionBlurVar2, -7, 7);
            double var3 = randDouble(randomMotionBlurVar3, -7, 6);
            log(effects,"Motion blur", var1, var2, var3);
            op.motionBlur(var1, var2, var3);
        }


        if (randomMedian.nextGaussian() < 1) {
            double var1 = randDouble(randomMedianVar1, 0, 1.1);
            log(effects,"Median", var1);
            op.median(var1);
        }

        //System.out.println(effects.toString());

        op.addImage(output);
        // execute the operation
        cmd.run(op);

        if (distortionDescriptionOutputFile != null) {
            PrintStream ps = new PrintStream(distortionDescriptionOutputFile);
            ps.println(effects.toString());
            ps.close();
        }
    }


    public void simulate() throws Exception {
        individualExperiments("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/prueba.png");

        boolean notGenerated = true;
        while (notGenerated) {
            try {
                randomEffects("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/prueba.png", "/tmp/distorted.jpg", null);
                notGenerated = false;
            } catch (Exception e) {
                System.err.println("Regenerating because of " + e.getMessage());
            }
        }
    }


    private void run(String input) throws Exception {
        File inputFolder = new File(input);
        if (!inputFolder.exists()) {
            throw new Exception("File " + inputFolder.getAbsolutePath() + " does not exist");
        }

        ArrayList<File> pngFiles = new ArrayList<>();
        FileUtils.readFiles(inputFolder, pngFiles,"png");
        int i=0;
        int n = pngFiles.size();

        for (File inputFile: pngFiles) {
            String s = "Processing ";
            System.out.println(s + i + SEP + n);
            i++;

            String name = FileUtils.getFileNameWithoutExtension(inputFile.getName());
            if (!name.endsWith(SYMBOLS)) {
                File output = new File(inputFile.getParent(), name + DISTORTED_JPG);
                File outputDescription = new File(inputFile.getParent(), name + DISTORTED_TXT);

                //System.out.println("----- " + inputFile.getName() + "-------");
                boolean notGenerated = true;
                while (notGenerated) {
                    try {
                        randomEffects(inputFile.getAbsolutePath(), output.getAbsolutePath(), outputDescription);
                        notGenerated = false;
                    } catch (Exception e) {
                        System.err.println("Regenerating because of " + e.getMessage());
                    }
                }
            }
        }
    }


    public static final void main(String [] args) throws Exception {
        /*try {
            new CameraSimulator("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/", "/Users/drizo/apps/ImageMagick-7.0.7/bin").simulate();
        } catch (CommandException commandException) {
            System.err.println(commandException.toString());
            System.err.println("Set the environment parameter: DYLD_LIBRARY_PATH=<path to ImageMagick lib>");

        }*/

        if (args.length != 1) {
            System.err.println("Set input folder");
            return;
        }

        new CameraSimulator("/Users/drizo/apps/GraphicsMagick-1.3.28/bin/",null ).run(args[0]);
    }
}
