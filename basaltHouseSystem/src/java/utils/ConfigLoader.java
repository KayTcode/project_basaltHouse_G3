package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream is = ConfigLoader.class
                    .getClassLoader()
                    .getResourceAsStream("config/app.properties");

            if (is == null) {
                // thử path khác
                is = ConfigLoader.class
                        .getClassLoader()
                        .getResourceAsStream("./config/app.properties");
            }

            if (is == null) {
                throw new RuntimeException("Cannot find app.properties");
            }

            properties.load(is);
            System.out.println("SUCCESS!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}