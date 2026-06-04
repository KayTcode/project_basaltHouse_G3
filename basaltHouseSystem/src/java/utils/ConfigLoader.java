/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author KayT
 */
public class ConfigLoader {

   private static final Properties properties = new Properties();

    static {
        try {
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
