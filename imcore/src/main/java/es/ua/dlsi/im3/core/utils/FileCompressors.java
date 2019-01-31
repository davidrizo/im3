package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * It uses the Apache Commons tools using https://memorynotfound.com/java-tar-example-compress-decompress-tar-tar-gz-files/
 */
public class FileCompressors {
    /**
     * Tar and compress with tgz
     * @param tarFile
     * @param inputFolder
     * @throws IOException
     */
    public void tgzFolder(Path tarFile, File inputFolder) throws IOException, IM3Exception {
        if (!inputFolder.isDirectory()) {
            throw new IM3Exception("Input folder '" + inputFolder.getAbsolutePath() + "' is not a folder");
        }
        try (TarArchiveOutputStream out = getTarArchiveOutputStream(tarFile)){
            addToArchiveCompression(out, inputFolder, ".");
        }
    }

    private TarArchiveOutputStream getTarArchiveOutputStream(Path file) throws IOException {
        TarArchiveOutputStream taos = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(file.toFile())));
        // TAR has an 8 gig file limit by default, this gets around that
        taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        // TAR originally didn't support long file names, so enable the support for it
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        taos.setAddPaxHeadersForNonAsciiNames(true);
        return taos;
    }

    private void addToArchiveCompression(TarArchiveOutputStream out, File file, String dir) throws IOException {
        String entry = dir + File.separator + file.getName();
        if (file.isFile()){
            out.putArchiveEntry(new TarArchiveEntry(file, entry));
            try (FileInputStream in = new FileInputStream(file)){
                IOUtils.copy(in, out);
            }
            out.closeArchiveEntry();
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null){
                for (File child : children){
                    addToArchiveCompression(out, child, entry);
                }
            }
        } else {
            System.out.println(file.getName() + " is not supported");
        }
    }


    /**
     * Untar and decompress with tgz
     * @param in
     * @param out
     * @throws IOException
     */
    public void untgz(String in, File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)))){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
    }
}
