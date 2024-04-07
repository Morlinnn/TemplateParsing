package org.morlinnn.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.morlinnn.reader.TemplateFileReader.REPLACEMENT;

public class TemplateStreamReader {
    public static List<String> readTemplateStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder text = new StringBuilder();
        int len;
        char[] bytes = new char[1024];
        while ((len = reader.read(bytes)) != -1) {
            text.append(bytes, 0, len);
        }
        String str = text.toString();
        str = str.replaceAll("(\r?\n){2}", REPLACEMENT);
        str = str.replaceAll("[\r\n]", "");
        return new ArrayList<>(Arrays.asList(str.split(REPLACEMENT)));
    }
}
