package controller;

import dto.UserLoginDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static services.AuthService.USER_SESSION_KEY;

public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/staff/" + resolveStaffPage(request));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/staff/import");
    }

    static void prepareStaffPage(HttpServletRequest request, String page) {
        HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;

        request.setAttribute("staffName", user != null && user.getFullName() != null
                ? user.getFullName()
                : "Staff");
        setStaffPageAttributes(request, page);
    }

    private static void setStaffPageAttributes(HttpServletRequest request, String page) {
        request.setAttribute("staffPage", page);

        if ("import".equals(page)) {
            request.setAttribute("staffPageEyebrow", "Staff / Import");
            request.setAttribute("staffPageTitle", "Nhập nguyên liệu");
            request.setAttribute("staffPageSubtitle", "Tạo phiếu nhập mới, chọn nhà cung cấp và cập nhật số lượng nhập kho.");
            return;
        }

        if ("history".equals(page)) {
            request.setAttribute("staffPageEyebrow", "Staff / Import history");
            request.setAttribute("staffPageTitle", "Lịch sử nhập nguyên liệu");
            request.setAttribute("staffPageSubtitle", "Theo dõi các phiếu nhập, trạng thái nhận hàng và người tạo phiếu.");
            return;
        }

        if ("sales-history".equals(page)) {
            request.setAttribute("staffPageEyebrow", "Staff / Sales audit");
            request.setAttribute("staffPageTitle", "Lịch sử bán hàng");
            request.setAttribute("staffPageSubtitle", "Đối chiếu số lượng sản phẩm bán ra với nguyên liệu đã dùng và lượng nguyên liệu nhập trong ngày kiểm kê.");
            return;
        }

        request.setAttribute("staffPage", "ingredient");
        request.setAttribute("staffPageEyebrow", "Staff / Inventory");
        request.setAttribute("staffPageTitle", "Quản lý nguyên liệu");
        request.setAttribute("staffPageSubtitle", "Theo dõi tồn kho, cảnh báo sắp hết và nhập thêm nguyên liệu.");
    }

    private static String resolveStaffPage(HttpServletRequest request) {
        String page = trimToNull(request.getParameter("page"));
        if (page == null) {
            String servletPath = request.getServletPath();
            if (servletPath != null && servletPath.startsWith("/staff/")) {
                page = servletPath.substring("/staff/".length());
            }
        }

        if (page == null) {
            return "ingredient";
        }

        String normalized = page.toLowerCase();
        if ("import".equals(normalized) || "importinvoice".equals(normalized) || "importinvoicevoice".equals(normalized)) {
            return "import";
        }
        if ("history".equals(normalized) || "historyimportinvoice".equals(normalized) || "importhistory".equals(normalized)) {
            return "history";
        }
        if ("sales-history".equals(normalized) || "saleshistory".equals(normalized) || "sales".equals(normalized)) {
            return "sales-history";
        }
        return "ingredient";
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    @Override
    public String getServletInfo() {
        return "Staff router";
    }
}
