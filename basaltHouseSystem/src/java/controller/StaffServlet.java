package controller;

import dto.UserLoginDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static services.AuthService.USER_SESSION_KEY;

public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/staff/ingredient");
    }

    static void prepareStaffPage(HttpServletRequest request, String page) {
        UserLoginDTO user = (UserLoginDTO) request.getSession(false)
                .getAttribute(USER_SESSION_KEY);
        String staffName = user.getFullName();
        if (staffName == null || staffName.trim().isEmpty()) {
            staffName = "Staff";
        }
        request.setAttribute("staffName", staffName);
        setStaffPageAttributes(request, page);
    }

    private static void setStaffPageAttributes(HttpServletRequest request, String page) {
        request.setAttribute("staffPage", page);

        if ("import".equals(page)) {
            request.setAttribute("staffPageTitle", "Nhập nguyên liệu");
            request.setAttribute("staffPageSubtitle", "Tạo phiếu nhập mới, chọn nhà cung cấp và cập nhật số lượng nhập kho.");
            return;
        }

        if ("history".equals(page)) {
            request.setAttribute("staffPageTitle", "Lịch sử nhập nguyên liệu");
            request.setAttribute("staffPageSubtitle", "Theo dõi các phiếu nhập, trạng thái nhận hàng và người tạo phiếu.");
            return;
        }

        if ("sales-history".equals(page)) {
            request.setAttribute("staffPageTitle", "Lịch sử bán hàng");
            request.setAttribute("staffPageSubtitle", "Đối chiếu số lượng sản phẩm bán ra với nguyên liệu đã dùng và lượng nguyên liệu nhập trong ngày kiểm kê.");
            return;
        }

        request.setAttribute("staffPage", "ingredient");
        request.setAttribute("staffPageTitle", "Quản lý nguyên liệu");
        request.setAttribute("staffPageSubtitle", "Theo dõi tồn kho, cảnh báo sắp hết và nhập thêm nguyên liệu.");
    }

}
