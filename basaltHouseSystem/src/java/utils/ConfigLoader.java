package utils;



import java.io.FileInputStream;
import java.io.IOException;
>>>>>>> main
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
                throw new RuntimeException("Cannot find app.properties");
            }

            properties.load(is);

            System.out.println("SUCCESS!");

        } catch (Exception ex) {

            System.out.println("Cannot load app.properties");

            InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("./config/app.properties");
            if (input == null) {
                System.out.println("CANNOT FIND app.properties");
            } else {
                properties.load(input);
                System.out.println("LOADED app.properties");
            }
        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}