/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ConfigLoader;

/**
 *
 * @author KayT
 */
public class DBContext {

    protected Connection connection;

    public DBContext() {
        //@Students: You are not allowed to edit this method  
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = ConfigLoader.get("url");
            String user = ConfigLoader.get("userID");
            String pass = ConfigLoader.get("password");

            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
