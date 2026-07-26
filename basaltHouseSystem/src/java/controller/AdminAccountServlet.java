/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.AdminAccountDAO;
import model.Account;
import dto.AccountViewDTO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import model.ActivityLog;
import services.ActivityLogService;
import services.AdminAccountService;
import utils.PasswordUtils;

/**
 *
 * @author MSI
 */
@WebServlet(name = "AdminAccountServlet", urlPatterns = {"/admin/accounts"})
public class AdminAccountServlet extends HttpServlet {

    private final AdminAccountService accountService = new AdminAccountService();
    private final ActivityLogService logService = new ActivityLogService();
    private static final int PAGE_SIZE = 10; // Định số dòng trên mỗi trang hiển thị

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdminAccountServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminAccountServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        if (action.equals("list")) {
            renderAccountDashboard(request, response);
        } else {
            renderAccountDashboard(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "add" -> handleAddAccount(request, response);
            case "update" -> handleUpdateAccount(request, response);
            case "delete" -> handleDeleteAccount(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/admin/accounts");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

// ─────────────────────────────────────────────────────────────────────
    // LUỒNG XỬ LÝ GET: Đóng gói và đẩy dữ liệu sang file JSP hiển thị bảng
    // ─────────────────────────────────────────────────────────────────────
    private void renderAccountDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Nhận các tham số lọc thô ở dạng String từ Client
            String search = request.getParameter("search");
            String roleStr = request.getParameter("roleId");
            String status = request.getParameter("status");
            String pageStr = request.getParameter("page");

            // 2. Chuyển giao toàn bộ tham số thô cho Service xử lý nghiệp vụ tập trung
            Map<String, Object> accountData = accountService.getAccountDashboardPage(search, roleStr, status, pageStr, PAGE_SIZE);

            // 3. Đính kèm Map kết quả tổng thể vào Request Scope với tên định danh là "accountData"
            request.setAttribute("accountData", accountData);

        } catch (Exception e) {
            System.err.println("Lỗi nạp danh sách tài khoản: " + e.getMessage());
            request.setAttribute("errorMessage", "Không thể tải danh sách tài khoản. Vui lòng thử lại sau!");
        }

        // 4. Chuyển hướng xử lý sang giao diện View đích
        request.getRequestDispatcher("/views/admin/admin_account.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────────────────
    // LUỒNG XỬ LÝ POST: Nhận dữ liệu Form, gửi sang Service xử lý rồi Redirect trang sạch
    // ─────────────────────────────────────────────────────────────────────
    
    // 1. Xử lý thêm tài khoản mới
    private void handleAddAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password"); 
        String hashedPass = PasswordUtils.hashSHA256(password);
        String roleIdStr = request.getParameter("roleId");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String isActiveStr = request.getParameter("isActive");

        // Đẩy toàn bộ tham số thô dạng String sang Service tự ép kiểu và xử lý DB
        boolean isSuccess = accountService.processAddAccount(email, hashedPass, roleIdStr, fullName, phone, isActiveStr);

        if (!isSuccess) {
            request.getSession().setAttribute("toastMessage", "Thêm tài khoản thất bại!");
            writeLog(getAdminId(request), "ADD", "Account", 0,
                    null, "Thêm tài khoản thất bại: " + email, "FAIL");
        } else {
            writeLog(getAdminId(request), "ADD", "Account", 0,
                    null, "Thêm tài khoản: " + email, "SUCCESS");
        }

        response.sendRedirect(request.getContextPath() + "/admin/accounts");
    }

    // 2. Xử lý cập nhật tài khoản
    private void handleUpdateAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("accountId");
        String email = request.getParameter("email");
        String roleIdStr = request.getParameter("roleId");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String oldRoleIdStr = request.getParameter("oldRoleId");
        String isActiveStr = request.getParameter("isActive");
        String isLockedStr = request.getParameter("isLocked");

        // Giao việc cho Service xử lý logic cập nhật tài khoản và cập nhật profile đi kèm
        boolean isSuccess = accountService.processUpdateAccount(idStr, email, roleIdStr, fullName, phone, oldRoleIdStr, isActiveStr, isLockedStr);

        int accountId = parseIntSafe(idStr);
        if (!isSuccess) {
            request.getSession().setAttribute("toastMessage", "Cập nhật tài khoản thất bại!");
            writeLog(getAdminId(request), "UPDATE", "Account", accountId,
                    "AccountId=" + accountId, "Cập nhật tài khoản thất bại: " + email, "FAIL");
        } else {
            writeLog(getAdminId(request), "UPDATE", "Account", accountId,
                    "AccountId=" + accountId, "Cập nhật tài khoản: " + email, "SUCCESS");
        }

        response.sendRedirect(request.getContextPath() + "/admin/accounts");
    }

    // 3. Xử lý xóa mềm tài khoản
    private void handleDeleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("accountId");
        String roleIdStr = request.getParameter("roleId");

        // Gọi service xử lý cập nhật trạng thái IsDeleted = 1 cho cả Accounts và bảng Profile
        boolean isSuccess = accountService.processDeleteAccount(idStr, roleIdStr);

        int accountId = parseIntSafe(idStr);
        if (!isSuccess) {
            request.getSession().setAttribute("toastMessage", "Xóa tài khoản thất bại!");
            writeLog(getAdminId(request), "DELETE", "Account", accountId,
                    "AccountId=" + accountId, "Xóa tài khoản thất bại ID=" + accountId, "FAIL");
        } else {
            writeLog(getAdminId(request), "DELETE", "Account", accountId,
                    "AccountId=" + accountId, "Xóa tài khoản ID=" + accountId, "SUCCESS");
        }

        response.sendRedirect(request.getContextPath() + "/admin/accounts");
    }

    private int getAdminId(HttpServletRequest request) {
        Object obj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser") : null;
        if (obj instanceof dto.UserLoginDTO) return ((dto.UserLoginDTO) obj).getAccountId();
        return 0;
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private void writeLog(int accountId, String action, String module,
                          int targetId, String oldValue, String newValue, String status) {
        try {
            ActivityLog log = new ActivityLog(accountId, action, module,
                    targetId, oldValue, newValue, status, 0, LocalDateTime.now());
            logService.ctreatActiveLog(log);
        } catch (Exception e) {
            System.err.println("[AdminAccountServlet] writeLog error: " + e.getMessage());
        }
    }
}

