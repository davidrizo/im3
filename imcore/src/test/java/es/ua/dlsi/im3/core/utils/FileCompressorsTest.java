package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileCompressorsTest {

    @Test
    public void tgz() throws IOException, IM3Exception {
        File tmpFolder = TestFileUtils.createTempFolder("tgztestinputfolder");
        ArrayList<Path> subfolders = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Path subfolder = Files.createTempDirectory(tmpFolder.toPath(), "tgztestsubfolder_" + i);
            subfolders.add(subfolder);
            for (int j=0; j<5; j++) {
                Path file = Files.createTempFile(subfolder, "tgztestfile_" + j, ".txt");
                String contentToWrite = "File #" + j + " in folder #" + j;
                Files.write(file, contentToWrite.getBytes());
            }
        }

        FileCompressors fileCompressors = new FileCompressors();
        File tmpTgzOneFolder = TestFileUtils.createTempFile("tgztest_one_folder.tgz");
        fileCompressors.tgzFolder(tmpTgzOneFolder.toPath(), tmpFolder.toPath());

        File tmpTgzFolders = TestFileUtils.createTempFile("tgztest_several_folders.tgz");
        fileCompressors.tgzFolders(tmpTgzFolders.toPath(), subfolders);

        //FileUtils.readFiles(tmpFolder, inputTarFiles, "txt");

        //TODO Hacer el test del descompresor
    }

}