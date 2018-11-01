package es.ua.dlsi.grfia.im3ws.scripts;

import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.model.ProjectModel;
import es.ua.dlsi.grfia.im3ws.muret.service.ImageService;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import es.ua.dlsi.grfia.im3ws.muret.service.impl.ImageServiceImpl;
import es.ua.dlsi.grfia.im3ws.muret.service.impl.ProjectServiceImpl;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.core.utils.ImageUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Image;
import es.ua.dlsi.im3.omr.model.io.XMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * It migrates MuRET XML files to the online version
 */
//Uncomment to run command line @SpringBootApplication
//@SpringBootApplication
@ComponentScan("es.ua.dlsi.grfia.im3ws")
@EnableJpaRepositories("es.ua.dlsi.grfia.im3ws.muret.repository")
@EntityScan("es.ua.dlsi.grfia.im3ws.muret.entity")
public class MigrateMuretXML implements CommandLineRunner {
    @Autowired
    ProjectService projectService;
    @Autowired
    ImageService imageService;

    MURETConfiguration muretConfiguration;

    public static void main(String[] args)  {
        SpringApplication.run(MigrateMuretXML.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        muretConfiguration = new MURETConfiguration("/Applications/MAMP/htdocs/muret", "http://localhost:8888/muret", 200, 720);

        importMuRETXML("/Users/drizo/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/B-3.28/B-3.28.mrt");

        System.out.println("Finished");
        ConfigurableApplicationContext ctx = SpringApplication.run(MigrateMuretXML.class, args);
        SpringApplication.exit(ctx);
    }

    private void importMuRETXML(String xmlFileName) throws Exception {
        try {
            ProjectModel projectModel = new ProjectModel(projectService, muretConfiguration);
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

            // use new obtained project object
            project = projectModel.newProject(project);

            System.out.println("\tProject created and inserted, working with images");
            File xmlImagesPath = new File(xmlProjectPath, "images");

            File projectPath = new File(muretConfiguration.getFolder(), project.getPath());

            for (es.ua.dlsi.im3.omr.model.entities.Image xmlImage : xmlProject.getImages()) {
                es.ua.dlsi.grfia.im3ws.muret.entity.Image image = importImage(xmlImage, xmlImagesPath, project, projectPath);

                // not work with pages...

            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(t);
        }
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

        // copy thumbnail file
        File thumbnail = new File(new File(projectPath, MURETConfiguration.THUMBNAIL_IMAGES), xmlImage.getImageRelativeFileName());
        ImageUtils.getInstance().scaleToFitHeight(inputImage, thumbnail, muretConfiguration.getThumbnailHeight());

        // copy preview file
        File preview = new File(new File(projectPath, MURETConfiguration.PREVIEW_IMAGES), xmlImage.getImageRelativeFileName());
        ImageUtils.getInstance().scaleToFitHeight(inputImage, preview, muretConfiguration.getPreviewHeight());

        return imageService.create(image);
    }


}