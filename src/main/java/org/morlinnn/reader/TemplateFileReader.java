package org.morlinnn.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateFileReader {
    protected static final String REPLACEMENT = "⣹⌚";

    public static List<String> readTemplateFile(File templateFile) throws FileNotFoundException {
        if (!templateFile.exists()) throw new FileNotFoundException(templateFile.getName() + " 文件不存在");
        try {
            String str = new String(Files.readAllBytes(templateFile.toPath()));
            str = str.replaceAll("(\r?\n{2})", REPLACEMENT);
            str = str.replaceAll("[\r\n]", "");
            return new ArrayList<>(Arrays.asList(str.split(REPLACEMENT)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
