package me.koutachan.queue.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static void copyFromResources(String source, Path destination) {
        if (!destination.toFile().exists()) {

            destination.toFile().mkdir();
        }

        Path path = destination.resolve(source);

        if (!path.toFile().exists()) {
            InputStream sources = FileUtil.class.getResourceAsStream("/" + source);
            try {
                Files.copy(sources, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
