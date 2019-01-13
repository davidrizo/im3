package es.ua.dlsi.grfia.im3ws.scripts;

import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.entity.*;
import es.ua.dlsi.grfia.im3ws.muret.model.ProjectModel;
import es.ua.dlsi.grfia.im3ws.muret.service.*;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.core.utils.ImageUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Image;
import es.ua.dlsi.im3.omr.model.io.XMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// IMPORTANT: IN order to execute it, remove spring-boot-starter-tomcat
/**
 * It migrates MuRET XML files to the online version
 */
@ComponentScan("es.ua.dlsi.grfia.im3ws")
@EnableJpaRepositories("es.ua.dlsi.grfia.im3ws.muret.repository")
@EntityScan("es.ua.dlsi.grfia.im3ws.muret.entity")
public class MigrateMuretXML implements CommandLineRunner {
    @Autowired
    ProjectService projectService;
    @Autowired
    ImageService imageService;
    @Autowired
    PageService pageService;
    @Autowired
    RegionService regionService;
    @Autowired
    SymbolService symbolService;
    @Autowired
    ProjectModel projectModel;



    MURETConfiguration muretConfiguration;

    public static void main(String[] args)  {
        SpringApplication.run(MigrateMuretXML.class, args);
    }
    @Override
    public void run(String... args) throws IOException {
        muretConfiguration = new MURETConfiguration("/Applications/MAMP/htdocs/muret", "http://localhost:8888/muret", null, 200, 720);

        /*String path = "/Users/drizo/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/";
        importMuRETXML(path + "B-3.28/B-3.28.mrt");
        importMuRETXML(path + "B-50.747/B-50.747.mrt");
        importMuRETXML(path + "B-53.781/B-53.781.mrt");
        importMuRETXML(path + "B-59.850-completo/B-59.850-completo.mrt");*/

        String path = "/Users/drizo/GCLOUDUA/HISPAMUS/muret/catedral_barcelona";
        ArrayList<File> mrts = new ArrayList<>();
        FileUtils.readFiles(new File(path), mrts, "mrt");
        for (File file: mrts) {
            importMuRETXML(file.getAbsolutePath()); //TODO Importar tipo de region
        }

        System.out.println("Finished");
        ConfigurableApplicationContext ctx = SpringApplication.run(MigrateMuretXML.class, args);
        SpringApplication.exit(ctx);
    }

    private void importMuRETXML(String xmlFileName) {
        try {
            System.out.println("Loading " + xmlFileName);
            XMLReader muretXMLReader = new XMLReader(AgnosticVersion.v2);

            File xmlFile = new File(xmlFileName);
            File xmlProjectPath = xmlFile.getParentFile();
            es.ua.dlsi.im3.omr.model.entities.Project xmlProject = muretXMLReader.load(xmlFile);

            Project project = new Project();
            project.setName(FileUtils.getFileWithoutPathOrExtension(xmlFile));
            project.setComposer(xmlProject.getComposer());
            project.setComments(xmlProject.getComments());
            project.setNotationType(xmlProject.getNotationType());
            project.setManuscriptType(ManuscriptType.eHandwritten); // not all of them are handwritten
            // use new obtained project object
            project = projectModel.newProject(project);

            System.out.println("\tProject created and inserted, working with images");
            File xmlImagesPath = new File(xmlProjectPath, "images");

            File projectPath = new File(muretConfiguration.getFolder(), project.getPath());

            for (es.ua.dlsi.im3.omr.model.entities.Image xmlImage : xmlProject.getImages()) {
                es.ua.dlsi.grfia.im3ws.muret.entity.Image image = importImage(xmlImage, xmlImagesPath, project, projectPath);

                for (es.ua.dlsi.im3.omr.model.entities.Page xmlPage : xmlImage.getPages()) {
                    es.ua.dlsi.grfia.im3ws.muret.entity.Page page = importPage(xmlPage, image);
                    
                    for (es.ua.dlsi.im3.omr.model.entities.Region xmlRegion: xmlPage.getRegions()) {
                        es.ua.dlsi.grfia.im3ws.muret.entity.Region region = importRegion(xmlRegion, page);

                        for (es.ua.dlsi.im3.omr.model.entities.Symbol xmlSymbol: xmlRegion.getSymbols()) {
                            es.ua.dlsi.grfia.im3ws.muret.entity.Symbol symbol = importSymbol(xmlSymbol, region);


                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Symbol importSymbol(es.ua.dlsi.im3.omr.model.entities.Symbol xmlSymbol, Region region) {
        System.out.println("\t\t\t\tImporting symbol");
        es.ua.dlsi.grfia.im3ws.muret.entity.Symbol symbol = new Symbol();
        symbol.setAgnosticSymbol(xmlSymbol.getAgnosticSymbol());
        symbol.setBoundingBox(convert(xmlSymbol.getBoundingBox()));
        symbol.setRegion(region);
        symbol.setComments(xmlSymbol.getComments());
        if (xmlSymbol.getStrokes() != null) {
            symbol.setStrokes(convert(xmlSymbol.getStrokes()));
        }
        return symbolService.create(symbol);
    }

    private Strokes convert(es.ua.dlsi.im3.omr.model.entities.Strokes xmlStrokes) {
        Strokes strokes = new CalcoStrokes();
        if (xmlStrokes.getStrokeList() != null) {
            for (es.ua.dlsi.im3.omr.model.entities.Stroke xmlStroke : xmlStrokes.getStrokeList()) {
                CalcoStroke stroke = new CalcoStroke();
                // remove strokes with a resolution lower than the pixel
                int prevX = 0;
                int prevY = 0;
                for (es.ua.dlsi.im3.omr.model.entities.Point xmlPoint : xmlStroke.pointsProperty()) {
                    int x = (int) Math.round(xmlPoint.getX());
                    int y = (int) Math.round(xmlPoint.getY());

                    if (prevX != x || prevY != y) {
                        stroke.addPoint(new Point(xmlPoint.getRelativeTime(), x, y));
                    }

                    prevX = x;
                    prevY = y;
                }
                ((CalcoStrokes) strokes).addStroke(stroke);
            }
        }

        return strokes;
    }

    private Region importRegion(es.ua.dlsi.im3.omr.model.entities.Region xmlRegion, Page page) {
        System.out.println("\t\t\tImporting region");
        es.ua.dlsi.grfia.im3ws.muret.entity.Region region = new Region();
        region.setBoundingBox(convert(xmlRegion.getBoundingBox()));
        region.setPage(page);
        region.setComments(xmlRegion.getComments());

        return regionService.create(region);
    }

    private Page importPage(es.ua.dlsi.im3.omr.model.entities.Page xmlPage, es.ua.dlsi.grfia.im3ws.muret.entity.Image image) {
        System.out.println("\t\tImporting page");

        es.ua.dlsi.grfia.im3ws.muret.entity.Page page = new Page();
        page.setComments(xmlPage.getComments());
        page.setBoundingBox(convert(xmlPage.getBoundingBox()));
        page.setImage(image);
        return pageService.create(page);
    }

    BoundingBox convert(es.ua.dlsi.im3.core.adt.graphics.BoundingBox bb) {
        return new BoundingBox((int)bb.getFromX(), (int)bb.getFromY(), (int)bb.getToX(), (int)bb.getToY());
    }

    private es.ua.dlsi.grfia.im3ws.muret.entity.Image importImage(Image xmlImage, File xmlImagesPath, Project project, File projectPath) throws IOException, IM3Exception {
        System.out.println("\tImporting image "  + xmlImage.getImageRelativeFileName());
        es.ua.dlsi.grfia.im3ws.muret.entity.Image image = new es.ua.dlsi.grfia.im3ws.muret.entity.Image();
        image.setProject(project);
        image.setFilename(xmlImage.getImageRelativeFileName());
        image.setComments(xmlImage.getComments());


        // copy original file
        File inputImage = new File(xmlImagesPath, xmlImage.getImageRelativeFileName());
        FileUtils.copy(inputImage, new File(new File(projectPath, MURETConfiguration.MASTER_IMAGES), xmlImage.getImageRelativeFileName()));

        BufferedImage fullImage = ImageIO.read(inputImage);
        image.setHeight(fullImage.getHeight());
        image.setWidth(fullImage.getWidth());

        // copy thumbnail file
        File thumbnail = new File(new File(projectPath, MURETConfiguration.THUMBNAIL_IMAGES), xmlImage.getImageRelativeFileName());
        ImageUtils.getInstance().scaleToFitHeight(inputImage, thumbnail, muretConfiguration.getThumbnailHeight());

        // copy preview file
        File preview = new File(new File(projectPath, MURETConfiguration.PREVIEW_IMAGES), xmlImage.getImageRelativeFileName());
        ImageUtils.getInstance().scaleToFitHeight(inputImage, preview, muretConfiguration.getPreviewHeight());

        return imageService.create(image);
    }


}