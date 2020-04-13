package awoo.cult.vote.storage;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Properties;

public class FileConfig {

    private String name;
    private File config;
    private InputStream stream;
    private Properties properties;

    public FileConfig(String fileName) throws IOException {
        this.name = fileName;
        this.config = new File(fileName);
        if (!config.exists()) {
            System.out.println("Configuration doesn't exist. Exiting....");
            System.exit(0);
        }

        stream = new FileInputStream(fileName);
        properties = new Properties();
        properties.load(stream);
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(@Nonnull String path) {
        return getProperties().getProperty(path);
    }

}
