/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import model.ActivityLog;

/**
 *
 * @author admin
 */
public class ActiveLogDAO extends DBContext {

    public boolean ctreatActiveLog(ActivityLog activityLog) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }

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

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, activityLog.getAccountId());
            statement.setString(2, activityLog.getAction());
            statement.setString(3, activityLog.getModule());
            statement.setInt(4, activityLog.getTargetId());
            statement.setString(5, activityLog.getOldValue());
            statement.setString(6, activityLog.getNewValue());
            statement.setString(7, activityLog.getStatus());
            statement.setInt(8, activityLog.getIsDeleted());
            statement.setTimestamp(9, Timestamp.valueOf(activityLog.getCreatedAt()));
            return statement.executeUpdate() == 1;
        }
    }
}
