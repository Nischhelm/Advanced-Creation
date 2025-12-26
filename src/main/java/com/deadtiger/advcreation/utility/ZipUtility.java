package com.deadtiger.advcreation.utility;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.nio.file.attribute.*;

public class ZipUtility
{
    private static ZipOutputStream zos;

    /**
     * This Java program demonstrates how to compress a directory in ZIP format.
     *
     * @author www.codejava.net
     */
    public static class ZipDir extends SimpleFileVisitor<Path>
    {
        private Path sourceDir;

        public ZipDir(Path sourceDir)
        {
            this.sourceDir = sourceDir;
        }

        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attributes)
        {

            try
            {
                Path targetFile = sourceDir.relativize(file);

                zos.putNextEntry(new ZipEntry(targetFile.toString()));

                byte[] bytes = Files.readAllBytes(file);
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();

            }
            catch (IOException ex)
            {
                System.err.println(ex);

            }


            return FileVisitResult.CONTINUE;
        }
    }

    public static boolean zipDirectory(String dirName, String sourcePath, String destPath)
    {
        Path sourceDir = Paths.get(sourcePath + "//" + dirName);
        System.out.println(sourcePath.toString());

        try
        {
            String zipFileName = destPath.concat("//" + dirName + ".zip");
            zos = new ZipOutputStream(new FileOutputStream(zipFileName));

            Files.walkFileTree(sourceDir, new ZipDir(sourceDir));

            zos.close();
        }
        catch (IOException ex)
        {
            System.err.println("I/O Error: " + ex);
            return false;
        }
        return true;

    }

    public static boolean zipDirectory(String srcDirName, String zipName, String sourcePath, String destPath)
    {
        Path sourceDir = Paths.get(sourcePath + "//" + srcDirName);
        System.out.println(sourcePath.toString());

        try
        {
            String zipFileName = destPath.concat("//" + zipName + ".zip");
            zos = new ZipOutputStream(new FileOutputStream(zipFileName));

            Files.walkFileTree(sourceDir, new ZipDir(sourceDir));

            zos.close();
        }
        catch (IOException ex)
        {
            System.err.println("I/O Error: " + ex);
            return false;
        }
        return true;

    }

}
