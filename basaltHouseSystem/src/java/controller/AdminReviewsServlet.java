package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminReviewsServlet", urlPatterns = {"/admin/reviews"})
public class AdminReviewsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward trực tiếp sang trang JSP (trang JSP tự động fallback dữ liệu mẫu nếu chưa kết nối DAO)
        request.getRequestDispatcher("/views/admin/admin_reviews.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        
        if (action == null || idStr == null) {
            out.print("{\"success\":false,\"message\":\"Yêu cầu không hợp lệ.\"}");
            return;
        }
        
        // Hiện tại trả về JSON thành công giả lập (Mock) cho Front-end tương tác
        if ("toggle".equals(action)) {
            String visibleStr = request.getParameter("visible");
            boolean visible = Boolean.parseBoolean(visibleStr);
            System.out.println("[AdminReviewsServlet Mock] Toggled review ID " + idStr + " visibility to: " + visible);
            out.print("{\"success\":true,\"message\":\"Cập nhật trạng thái hiển thị thành công.\"}");
        } else if ("delete".equals(action)) {
            System.out.println("[AdminReviewsServlet Mock] Soft deleted review ID " + idStr);
            out.print("{\"success\":true,\"message\":\"Xóa đánh giá thành công.\"}");
        } else {
            out.print("{\"success\":false,\"message\":\"Hành động không được hỗ trợ.\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Reviews Controller";
    }
}
