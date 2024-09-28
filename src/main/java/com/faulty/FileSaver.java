package com.faulty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class FileSaver {
    public void saveMarkdown(String repoPath, String dir, String fileName, String content) {
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = repoPath + "/" + dir + "/" + fileName + ".md";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content);
            System.out.println("Successfully saved: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file: " + filePath);
            e.printStackTrace();
        }
    }
}
