/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.ActivityLog;

/**
 *
 * @author admin
 */
public class ActiveLogDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public boolean ctreatActiveLog(ActivityLog s) {
        try {
             String sql = """
                          INSERT INTO [dbo].[ActivityLogs]
                                     ([AccountId]
                                     ,[Action]
                                     ,[Module]
                                     ,[TargetId]
                                     ,[OldValue]
                                     ,[NewValue]
                                     ,[Status]
                                     ,[IsDeleted]
                                     ,[CreatedAt])
                               VALUES
                                     (?,?,?,?,?,?,?,?,?)
                          """;
             st = connection.prepareStatement(sql);
             st.setObject(1, s.getAccountId());
             st.setObject(2, s.getAction());
             st.setObject(3, s.getModule());
             st.setObject(4, s.getTargetId());
             st.setObject(5, s.getOldValue());
             st.setObject(6, s.getNewValue());
             st.setObject(7, s.getStatus());
             st.setObject(8, s.getIsDeleted());
             st.setObject(9, s.getCreatedAt());
             st.executeUpdate();
             return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
               
    }
}
