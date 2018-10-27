package es.ua.dlsi.grfia.im3ws.muret.controller;

import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.controller.payload.UploadFileResponse;
import es.ua.dlsi.grfia.im3ws.muret.entity.Image;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.service.ImageService;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import es.ua.dlsi.grfia.im3ws.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// See complete file in https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/
/**
 * Used to upload project images to the server
 */

@CrossOrigin("${angular.url}")
@RequestMapping("/muret/upload")
@RestController
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MURETConfiguration muretConfiguration;
    @Autowired
    ProjectService projectService;
    @Autowired
    ImageService imageService;

    // angular ng2-file-upload uploads files one by one
    @PostMapping("projectImage")
    public UploadFileResponse uploadFile(@RequestParam("projectid") Integer projectid, @RequestParam("file") MultipartFile file) {
        Optional<Project> project = projectService.findById(projectid);
        if (!project.isPresent()) {
            throw new RuntimeException("Project with id " + projectid + " does not exist");
        }

        Path path = Paths.get(muretConfiguration.getFolder(), project.get().getPath());

        String fileName = fileStorageService.storeFile(path, file);

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Uploading file {0} to project with id {1}", new Object[]{fileName, projectid});

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(project.get().getPath())
                .path(fileName)
                .toUriString();

        //TODO Atómico
        //TODO Ordenación
        Image image = new Image(fileName, 0, project.get());
        imageService.create(image);

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    // angular ng2-file-upload uses file as parameter name
    /*@PostMapping("projectImages")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("projectid") Integer projectid, @RequestParam("files") MultipartFile[] files) {


        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Uploading {0} files to project with id {1}", new Object[]{files==null?0:files.length, projectid});
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }*/
}
