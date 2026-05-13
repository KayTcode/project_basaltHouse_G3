/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author KayT
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("config/app.properties");
            properties.load(fis);
            System.out.println("SUCCESS!");
        } catch (IOException ex) {
            System.out.println("Cannot load app.properties");
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
