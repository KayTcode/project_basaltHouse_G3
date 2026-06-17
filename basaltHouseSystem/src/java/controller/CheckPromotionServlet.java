package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import services.PromotionService;

// Gộp CheckDiscountServlet và CheckMemberServlet làm 1.
@WebServlet(name = "CheckPromotionServlet", urlPatterns = {"/CheckDiscount", "/CheckMember"})
public class CheckPromotionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getServletPath();
        PromotionService promotionService = new PromotionService();
        String json = "";

        if ("/CheckDiscount".equals(path)) {
            String code = request.getParameter("code");
            json = promotionService.checkDiscount(code);
        } else if ("/CheckMember".equals(path)) {
            String phone = request.getParameter("phone");
            json = promotionService.checkMember(phone);
        }

        response.getWriter().write(json);
    }
}
