<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="utils.ConfigLoader"%>
<!DOCTYPE html>
<html>
<head>
    <title>Kiểm tra kết nối Database</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .success { color: green; font-weight: bold; }
        .error { color: red; font-weight: bold; }
        pre { background: #eee; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h2>Kết quả kiểm tra kết nối Database</h2>
    <%
        String url = ConfigLoader.get("url");
        String user = ConfigLoader.get("userID");
        String pass = ConfigLoader.get("password");
    %>
    <p><strong>URL cấu hình:</strong> <%= url %></p>
    <p><strong>User cấu hình:</strong> <%= user %></p>
    
    <%
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, pass);
            out.println("<p class='success'> Kết nối thành công đến SQL Server!</p>");
            
            // Query tables list
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM [Tables]");
            if(rs.next()) {
                out.println("<p class='success'> Tìm thấy bảng Tables. Số lượng bản ghi hiện tại: " + rs.getInt(1) + "</p>");
            }
        } catch (ClassNotFoundException e) {
            out.println("<p class='error'> Lỗi: Không tìm thấy Driver SQL Server.</p>");
            out.println("<pre>");
            e.printStackTrace(new java.io.PrintWriter(out));
            out.println("</pre>");
        } catch (SQLException e) {
            out.println("<p class='error'> Lỗi SQL Server: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(new java.io.PrintWriter(out));
            out.println("</pre>");
        } finally {
            if (conn != null) {
                try { conn.close(); } catch(Exception e) {}
            }
        }
    %>
</body>
</html>
