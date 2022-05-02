package me.koutachan.queue.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static void copyFromResources(String source, Path destination) {
        if (!destination.toFile().exists()) {

            destination.toFile().mkdir();

            InputStream sources = FileUtil.class.getResourceAsStream("/" + source);
            try {
                Files.copy(sources, destination.resolve(source));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
