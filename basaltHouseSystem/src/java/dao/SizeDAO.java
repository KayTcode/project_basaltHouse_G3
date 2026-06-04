/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import model.Size;

/**
 *
 * @author admin
 */
public class SizeDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public HashMap<Integer, String> getSize() {
        HashMap<Integer, String> size = new HashMap<>();
        try {
            String sql = """
                           select SizeId,SizeName from Sizes
                           """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Size s = new Size(rs.getInt("SizeId"), rs.getString("SizeName"));
                size.put(s.getSizeId(), s.getSizeName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return size;

    }
}
