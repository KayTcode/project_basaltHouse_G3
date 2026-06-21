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
            json = promotionService.checkDiscount(code);
        } else if ("member".equals(action)) {
            String phone = request.getParameter("phone");
            json = promotionService.checkMember(phone);
        }

        response.getWriter().write(json);
    }
}
