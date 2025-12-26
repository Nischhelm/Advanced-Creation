import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ApplyJsonChanges {

    static class DiffEntry {
        String file;
        List<String> diff;
    }

    public static void main(String[] args) throws IOException {
        // Input paths
        String jsonPath = "transformation_script.json";
        String srcZipPath = "src_mc_1-12-2.zip";
        String outputZipPath = "output_mc_1-16-5.zip";

        // Load JSON file
        Gson gson = new Gson();
        List<DiffEntry> changes;
        try (Reader reader = new FileReader(jsonPath)) {
            changes = gson.fromJson(reader, new TypeToken<List<DiffEntry>>() {}.getType());
        }

        // Create a temporary directory to extract and modify files
        Path tempDir = Files.createTempDirectory("mc_conversion");
        Path extractDir = tempDir.resolve("extracted");
        Files.createDirectories(extractDir);

        // Extract the source zip file
        unzip(srcZipPath, extractDir);

        // Apply changes
        for (DiffEntry entry : changes) {
            Path filePath = extractDir.resolve(entry.file);
            if (Files.exists(filePath)) {
                applyDiff(filePath, entry.diff);
            } else {
                System.out.println("File not found in source zip: " + entry.file);
            }
        }

        // Re-zip the modified files
        zipDirectory(extractDir, outputZipPath);

        // Clean up temporary directory
        deleteDirectory(tempDir);

        System.out.println("Output zip created at: " + outputZipPath);
    }

    private static void unzip(String zipFilePath, Path outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = outputDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (OutputStream os = Files.newOutputStream(filePath)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private static void applyDiff(Path filePath, List<String> diff) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        for (String diffLine : diff) {
            if (diffLine.startsWith("-")) {
                lines.remove(diffLine.substring(1));
            } else if (diffLine.startsWith("+")) {
                lines.add(diffLine.substring(1));
            }
        }
        Files.write(filePath, lines);
    }

    private static void zipDirectory(Path sourceDir, String zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        ZipEntry entry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + p + ": " + e.getMessage());
                    }
                });
    }
}
