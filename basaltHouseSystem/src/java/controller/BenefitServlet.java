/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import model.DiscountCode;
import services.DiscountCodeService;

/**
 *
 * @author admin
 */
public class BenefitServlet extends HttpServlet {

    private static final DiscountCodeService dService = new DiscountCodeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HashMap<String, Object> s = dService.getDiscountCode();
        List<DiscountCode> publicVouchers = null;
        if (s.containsKey("error")) {
            request.setAttribute("error", s.get("error").toString());

        } else {
            publicVouchers = (List<DiscountCode>) s.get("success");
        }

        request.setAttribute("publicVouchers", publicVouchers);
        request.getRequestDispatcher("views/Benefit/Benefit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Benefit page";
    }
}
