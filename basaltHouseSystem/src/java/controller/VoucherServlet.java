package controller;

import dao.DiscountCodeDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CustomerDiscountCode;
import model.DiscountCode;

public class VoucherServlet extends HttpServlet {

    private static final String USER_SESSION_KEY = "currentUser";
    private static final int EXPIRING_DAYS = 7;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;

        DiscountCodeDAO dao = new DiscountCodeDAO();
        Map<Integer, String> voucherStatus = new HashMap<>();
        Map<Integer, String> voucherStatusText = new HashMap<>();
        Map<Integer, String> voucherStatusClass = new HashMap<>();
        Map<Integer, String> publicVoucherStatus = new HashMap<>();
        Map<Integer, String> publicVoucherStatusText = new HashMap<>();
        Map<Integer, String> publicVoucherStatusClass = new HashMap<>();
         LocalDateTime now = LocalDateTime.now();
        List<CustomerDiscountCode> list = new ArrayList<>();
        if (list.isEmpty()) {
            List<DiscountCode> publicVouchers = dao.getDiscountCode();
           

            for (DiscountCode item : publicVouchers) {
                String status = "available";
                String statusText = "Khả dụng";
                String statusClass = "";

                if (item.getEndDate() != null) {
                    long daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), item.getEndDate().toLocalDate());

                    if (daysLeft < 0) {
                        status = "expired";
                        statusText = "Hết hạn";
                        statusClass = "voucher-status--expired";
                    } else if (daysLeft <= EXPIRING_DAYS) {
                        status = "expiring";
                        statusText = "Sắp hết hạn";
                        statusClass = "voucher-status--warning";
                    }
                }

                publicVoucherStatus.put(item.getDiscountId(), status);
                publicVoucherStatusText.put(item.getDiscountId(), statusText);
                publicVoucherStatusClass.put(item.getDiscountId(), statusClass);
            }

            request.setAttribute("publicVouchers", publicVouchers);
        } else {
            list = dao.getVoucherById(user.getAccountId());
            for (CustomerDiscountCode item : list) {
                String status = "available";
                String statusText = "Khả dụng";
                String statusClass = "";

                if (item.getEndDate() != null) {
                    long daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), item.getEndDate().toLocalDate());

                    if (daysLeft < 0) {
                        status = "expired";
                        statusText = "Hết hạn";
                        statusClass = "voucher-status--expired";
                    } else if (daysLeft <= EXPIRING_DAYS) {
                        status = "expiring";
                        statusText = "Sắp hết hạn";
                        statusClass = "voucher-status--warning";
                    }
                }

            }

            request.setAttribute("publicVoucherStatus", publicVoucherStatus);
            request.setAttribute("publicVoucherStatusText", publicVoucherStatusText);
            request.setAttribute("publicVoucherStatusClass", publicVoucherStatusClass);

            request.setAttribute("voucherStatus", voucherStatus);
            request.setAttribute("voucherStatusText", voucherStatusText);
            request.setAttribute("voucherStatusClass", voucherStatusClass);
            request.setAttribute("listP", list);
        }

        request.getRequestDispatcher("views/Voucher/Voucher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;
        String code = request.getParameter("voucherCode");
        code = code != null ? code.trim() : "";

        if (code.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã voucher");
            doGet(request, response);
            return;
        }

        DiscountCodeDAO dao = new DiscountCodeDAO();
        for (DiscountCode item : dao.getDiscountCode()) {
            if (code.equalsIgnoreCase(item.getCode())) {
                request.getSession().setAttribute("voucher", item);
                request.setAttribute("success", "Thêm mã giảm giá thành công");
                doGet(request, response);
                return;
            }
        }

        if (user != null) {
            for (CustomerDiscountCode item : dao.getVoucherById(user.getAccountId())) {
                if (code.equalsIgnoreCase(item.getCode())) {
                    DiscountCode voucher = new DiscountCode();
                    voucher.setDiscountId(item.getDiscountId());
                    voucher.setCode(item.getCode());
                    voucher.setDiscountPercent(item.getDiscountPercent());
                    voucher.setDiscountAmount(item.getDiscountAmount());
                    voucher.setStartDate(item.getStartDate());
                    voucher.setEndDate(item.getEndDate());
                    voucher.setDescription(item.getDescription());

                    request.getSession().setAttribute("voucher", voucher);
                    request.setAttribute("success", "Thêm mã giảm giá thành công");
                    doGet(request, response);
                    return;
                }
            }
        }

        request.setAttribute("error", "Mã code không tồn tại hoặc đã hết hạn");
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "VoucherServlet";
    }
}
