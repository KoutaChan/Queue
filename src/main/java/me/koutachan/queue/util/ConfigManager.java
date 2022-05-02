package me.koutachan.queue.util;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Path;

@Getter @Setter
public class ConfigManager {

    public static YamlMapping of(String source, Path destination) {

        FileUtil.copyFromResources(source, destination);

        try {
            return Yaml.createYamlInput(destination.resolve(source).toFile()).readYamlMapping();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }
}
