package danfe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    public static String readFromFileName(String fileName) throws IOException {
        var inputStream = FileUtils.class.getResourceAsStream(fileName);
        return readFromInputStream(inputStream);
    }

    public static String readFromFile(File file) throws IOException {
        return readFromInputStream(new FileInputStream(file));
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        var result = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    public static void deleteFile(String fileName) {
        var file = new File(fileName);
        if (file.exists())
            file.delete();
    }

}
