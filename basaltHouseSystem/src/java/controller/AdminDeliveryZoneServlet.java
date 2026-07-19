package controllers;

import services.AdminDeliveryZoneService;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminDeliveryZoneServlet", urlPatterns = {"/admin/shipping-zones"})
public class AdminDeliveryZoneServlet extends HttpServlet {

    private final AdminDeliveryZoneService service = new AdminDeliveryZoneService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String isActive = request.getParameter("isActive");

        Map<String, Object> data = service.getDashboardData(province, district, isActive);

        request.setAttribute("zones", data.get("zones"));
        request.setAttribute("groupedZones", data.get("groupedZones"));
        request.setAttribute("stats", data.get("stats"));
        request.setAttribute("provinces", data.get("provinces"));
        request.setAttribute("districts", data.get("districts"));

        request.setAttribute("oldProvince", data.get("oldProvince"));
        request.setAttribute("oldDistrict", data.get("oldDistrict"));
        request.setAttribute("oldIsActive", data.get("oldIsActive"));

        request.getRequestDispatcher("/views/admin/admin_delivery_zones.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("add".equals(action)) {
            String wardName = request.getParameter("wardName");
            String district = request.getParameter("district");
            String province = request.getParameter("province");
            boolean isActive = "1".equals(request.getParameter("isActive"));

            String result = service.addZone(wardName, district, province, isActive);
            if ("success".equals(result)) {
                session.setAttribute("successMsg", "Thêm vùng giao hàng thành công.");
            } else {
                session.setAttribute("errorMsg", result);
            }

        } else if ("update".equals(action)) {
            try {
                int zoneId = Integer.parseInt(request.getParameter("zoneId"));
                String wardName = request.getParameter("wardName");
                String district = request.getParameter("district");
                String province = request.getParameter("province");
                boolean isActive = "1".equals(request.getParameter("isActive"));

                String result = service.updateZone(zoneId, wardName, district, province, isActive);
                if ("success".equals(result)) {
                    session.setAttribute("successMsg", "Cập nhật vùng giao hàng thành công.");
                } else {
                    session.setAttribute("errorMsg", result);
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "ID vùng giao hàng không hợp lệ.");
            }

        } else if ("toggleActive".equals(action)) {
            try {
                int zoneId = Integer.parseInt(request.getParameter("zoneId"));
                if (service.toggleActive(zoneId)) {
                    session.setAttribute("successMsg", "Đã cập nhật trạng thái.");
                } else {
                    session.setAttribute("errorMsg", "Lỗi cập nhật trạng thái.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "ID vùng giao hàng không hợp lệ.");
            }

        } else if ("delete".equals(action)) {
            try {
                int zoneId = Integer.parseInt(request.getParameter("zoneId"));
                if (service.deleteZone(zoneId)) {
                    session.setAttribute("successMsg", "Đã xóa vùng giao hàng.");
                } else {
                    session.setAttribute("errorMsg", "Lỗi xóa vùng giao hàng.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "ID vùng giao hàng không hợp lệ.");
            }
        }

        String redirectUrl = request.getContextPath() + "/admin/shipping-zones";
        StringBuilder queryStr = new StringBuilder();
        
        String p = request.getParameter("filterProvince");
        if (p != null && !p.isEmpty()) queryStr.append("province=").append(p).append("&");
        String d = request.getParameter("filterDistrict");
        if (d != null && !d.isEmpty()) queryStr.append("district=").append(d).append("&");
        String a = request.getParameter("filterIsActive");
        if (a != null && !a.isEmpty()) queryStr.append("isActive=").append(a).append("&");
        
        if (queryStr.length() > 0) {
            redirectUrl += "?" + queryStr.substring(0, queryStr.length() - 1);
        }

        response.sendRedirect(redirectUrl);
    }
}
