package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import model.ActivityLog;
import services.ActivityLogService;
import services.AuthService;
import services.DiscountCodeService;

public class VoucherServlet extends HttpServlet {

    private static final String USER_SESSION_KEY = "currentUser";
    private static final DiscountCodeService discountCodeService = new DiscountCodeService();
    private static final ActivityLogService activeService = new ActivityLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserLoginDTO user = getCurrentUser(request);
        Integer accountId = user != null ? user.getAccountId() : null;
        HashMap<String, Object> voucherData = discountCodeService.statusVoucherById(accountId);
        
        for (Map.Entry<String, Object> entry : voucherData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        request.getRequestDispatcher("views/Voucher/Voucher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserLoginDTO user1 = (UserLoginDTO) session.getAttribute(AuthService.USER_SESSION_KEY);
        if (user1 == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        HashMap<String, Object> result = discountCodeService.applyVoucherCode(
                request.getParameter("voucherCode"), user1.getAccountId());

        if (result.containsKey("voucher")) {
            request.getSession().setAttribute("voucher", result.get("voucher"));
        }

        if (result.containsKey("success")) {
            HashMap<String,Object>s = activeService.ctreatActiveLog(new ActivityLog(user1.getAccountId(),
                    "Apply Voucher Code",
                    "CustomerDiscountcode",
                    user1.getAccountId(),
                    null,
                    request.getParameter("voucherCode"),
                    "Success",
                    0,
                    LocalDateTime.now()));
            request.setAttribute("success", result.get("success"));
        } else if (result.containsKey("error")) {
            request.setAttribute("error", result.get("error"));
        }

        doGet(request, response);
    }

    private UserLoginDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY) : null;
    }

    @Override
    public String getServletInfo() {
        return "VoucherServlet";
    }
}
