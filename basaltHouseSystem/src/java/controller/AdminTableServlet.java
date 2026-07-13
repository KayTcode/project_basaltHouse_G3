package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import services.AdminTableService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/tables")
public class AdminTableServlet extends HttpServlet {

    private final AdminTableService tableService = new AdminTableService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Data cho phần Sơ đồ bàn (Dashboard)
        Map<String, Object> dashboardData = tableService.getTableDashboardData();
        request.setAttribute("dashboard", dashboardData);
        
        // 2. Data cho phần Lịch sử phiên
        String pageStr = request.getParameter("page");
        Map<String, Object> historyData = tableService.getHistoryData(pageStr, 10);
        request.setAttribute("historyData", historyData);
        
        // 3. Render JSP
        request.getRequestDispatcher("/views/admin/admin_table.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        String action = request.getParameter("action");
        
        // Lấy Account hiện tại từ session (giả định session attribute là 'account')
        // Nếu project dùng tên khác, cần điều chỉnh lại 'account'
        Account acc = (Account) request.getSession().getAttribute("account");
        int cashierId = (acc != null) ? acc.getAccountId() : 1; // Fallback 1 for dev
        
        if (action != null) {
            switch (action) {
                case "openSession":
                    String openTableId = request.getParameter("tableId");
                    String guestCount = request.getParameter("guestCount");
                    boolean openOk = tableService.openSession(openTableId, cashierId, guestCount);
                    
                    if (openOk) {
                        request.getSession().setAttribute("toastMessage", "Mở phiên thành công cho bàn!");
                    } else {
                        request.getSession().setAttribute("toastError", "Lỗi: Không thể mở phiên. Bàn có thể không trống.");
                    }
                    break;
                    
                case "closeSession":
                    String closeSessionId = request.getParameter("sessionId");
                    String closeTableId = request.getParameter("tableId");
                    boolean closeOk = tableService.closeSession(closeSessionId, closeTableId);
                    
                    if (closeOk) {
                        request.getSession().setAttribute("toastMessage", "Đã đóng phiên làm việc!");
                    } else {
                        request.getSession().setAttribute("toastError", "Lỗi: Không thể đóng phiên.");
                    }
                    break;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/tables");
    }
}
