package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import services.PromotionService;

@WebServlet(name = "CheckPromotionServlet", urlPatterns = {"/CheckPromotion"})
public class CheckPromotionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");
        PromotionService promotionService = new PromotionService();
        String json = "";

        if ("discount".equals(action)) {
            String code = request.getParameter("code");
            String custIdStr = request.getParameter("customerId");
            String tsIdStr = request.getParameter("tableSessionId");
            String tIdStr = request.getParameter("tableId");
            Integer custId = null;
            Integer tableSessionId = null;
            Integer tableId = null;
            if (custIdStr != null && !custIdStr.trim().isEmpty()) {
                try { custId = Integer.parseInt(custIdStr.trim()); } catch (Exception e) {}
            }
            if (tsIdStr != null && !tsIdStr.trim().isEmpty()) {
                try { tableSessionId = Integer.parseInt(tsIdStr.trim()); } catch (Exception e) {}
            }
            if (tIdStr != null && !tIdStr.trim().isEmpty()) {
                try { tableId = Integer.parseInt(tIdStr.trim()); } catch (Exception e) {}
            }
            json = promotionService.checkDiscount(code, custId, tableSessionId, tableId);
        } else if ("member".equals(action)) {
            String phone = request.getParameter("phone");
            json = promotionService.checkMember(phone);
        } else if ("member_name".equals(action)) {
            String name = request.getParameter("name");
            json = promotionService.searchMembersByName(name);
        } else if ("public_discounts".equals(action)) {
            json = promotionService.getPublicDiscounts();
        }

        response.getWriter().write(json);
    }
}
