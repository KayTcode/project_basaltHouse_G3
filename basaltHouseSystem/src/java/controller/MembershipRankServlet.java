/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import model.CustomerRanking;
import model.MembershipRank;
import services.AuthService;
import services.CustomerMemberShipService;

/**
 *
 * @author admin
 */
public class MembershipRankServlet extends HttpServlet {

    private static final CustomerMemberShipService membershipService
            = new CustomerMemberShipService();

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
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserLoginDTO user = (UserLoginDTO) session.getAttribute(AuthService.USER_SESSION_KEY);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerRanking cr = null;
        HashMap<String, Object> s = membershipService.getCustomeRankingById(user.getAccountId());
        if (s.containsKey("error")) {
            request.setAttribute("error", s.get("error").toString());
        } else {
            cr = (CustomerRanking) s.get("success");
        }
        List<MembershipRank> list = null;
        HashMap<String, Object> s2 = membershipService.getRankName();
        if (s2.containsKey("error")) {
            request.setAttribute("error", s2.get("error").toString());
        } else {
            list = (List<MembershipRank>) s2.get("success");
            if (list != null) {
                list = list.stream()
                        .filter(rank -> !rank.isIsDeleted())
                        .toList();
            }
        }
        request.setAttribute("cus", cr);
        request.setAttribute("name", user.getFullName());
        request.setAttribute("rankList", list);
        if (cr != null) {
            request.setAttribute("progressValue", calculateProgressValue(cr));
            request.setAttribute("rankName", cr.getName());
            request.setAttribute("nextRankName",
                    cr.getNextRank() != null ? cr.getNextRank() : cr.getName());
        }
        request.getRequestDispatcher("views/MembershipRanks/MembershipRank.jsp").forward(request, response);
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
        doGet(request, response);
    }

    private int calculateProgressValue(CustomerRanking customerRanking) {
        BigDecimal totalSpent = valueOrZero(customerRanking.getTotalSpent());
        BigDecimal currentRankMinSpent = valueOrZero(customerRanking.getMinTotalSpent());
        BigDecimal nextRankMinSpent = customerRanking.getNextRankMinSpent();

        if (nextRankMinSpent == null) {
            return 100;
        }

        BigDecimal rankRange = nextRankMinSpent.subtract(currentRankMinSpent);
        if (rankRange.signum() <= 0) {
            return totalSpent.compareTo(nextRankMinSpent) >= 0 ? 100 : 0;
        }

        BigDecimal progressInRank = totalSpent.subtract(currentRankMinSpent);
        if (progressInRank.signum() <= 0) {
            return 0;
        }
        if (progressInRank.compareTo(rankRange) >= 0) {
            return 100;
        }

        return progressInRank
                .multiply(BigDecimal.valueOf(100))
                .divide(rankRange, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
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

}
