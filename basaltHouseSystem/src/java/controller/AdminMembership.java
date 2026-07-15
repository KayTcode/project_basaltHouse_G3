/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.ActivityLog;
import model.CustomerMembership;
import model.MembershipRank;
import services.ActivityLogService;
import services.AuthService;
import services.CustomerMemberShipService;

/**
 *
 * @author admin
 */
public class AdminMembership extends HttpServlet {

    private static final int MEMBER_PAGE_SIZE = 5;
    private static final int RANK_PAGE_SIZE = 4;
    private final CustomerMemberShipService membershipService = new CustomerMemberShipService();
    private final ActivityLogService activityService = new ActivityLogService();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdminMembership</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminMembership at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        String search = request.getParameter("search");
        String key = search == null ? "" : search.trim();
        String rankIdParam = request.getParameter("rankId");
        int rankId = 0;
        if (rankIdParam != null && !rankIdParam.trim().isEmpty()) {
            try {
                rankId = Integer.parseInt(rankIdParam.trim());
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Hang thanh vien khong hop le");
            }
        }
        String statusParam = request.getParameter("status");
        String status = statusParam == null ? "" : statusParam.trim();
        if (!"active".equals(status) && !"locked".equals(status)) {
            status = "";
        }
        int requestedPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                requestedPage = Math.max(1, Integer.parseInt(pageParam.trim()));
            } catch (NumberFormatException e) {
                requestedPage = 1;
            }
        }
        int requestedRankPage = 1;
        String rankPageParam = request.getParameter("rankPage");
        if (rankPageParam != null && !rankPageParam.trim().isEmpty()) {
            try {
                requestedRankPage = Math.max(1, Integer.parseInt(rankPageParam.trim()));
            } catch (NumberFormatException e) {
                requestedRankPage = 1;
            }
        }
        HashMap<String, Object> rankResult = membershipService.getRankName();
        HashMap<String, Object> memberResult = membershipService.searchCustomer(key, rankId, status);
        List<CustomerMembership> memberList = new ArrayList<>();
        List<MembershipRank> rankList = new ArrayList<>();
        try {
            if (rankResult.containsKey("success")) {
                rankList = (List<MembershipRank>) rankResult.get("success");
            } else if (rankResult.containsKey("error")) {
                request.setAttribute("error", rankResult.get("error").toString());
            }
        } catch (Exception e) {
            request.setAttribute("error", "loi");
        }

        try {
            if (memberResult.containsKey("success")) {
                memberList = (List<CustomerMembership>) memberResult.get("success");
            } else if (memberResult.containsKey("error")) {
                request.setAttribute("error", memberResult.get("error").toString());
            }
        } catch (Exception e) {
            request.setAttribute("error", "loi");
        }

        List<MembershipRank> activeRankList = new ArrayList<>();
        for (MembershipRank rank : rankList) {
            if (!rank.isIsDeleted()) {
                activeRankList.add(rank);
            }
        }

        BigDecimal totalSpent = BigDecimal.ZERO;
        int activeMembers = 0;
        for (CustomerMembership member : memberList) {
            if (member.getTotalSpent() != null) {
                totalSpent = totalSpent.add(member.getTotalSpent());
            }
            if (!"locked".equals(member.getStatus())) {
                activeMembers++;
            }
        }

        String topRank = "";
        int topDiscount = 0;
        BigDecimal topMinSpent = null;
        for (MembershipRank rank : activeRankList) {
            if (rank.getDiscountValue() > topDiscount) {
                topDiscount = rank.getDiscountValue();
            }
            if (rank.getMinTotalSpent() != null && (topMinSpent == null || rank.getMinTotalSpent().compareTo(topMinSpent) > 0)) {
                topMinSpent = rank.getMinTotalSpent();
                topRank = rank.getRankName();
            }
        }

        int totalMemberCount = memberList.size();
        int totalPages = (totalMemberCount + MEMBER_PAGE_SIZE - 1) / MEMBER_PAGE_SIZE;
        int currentPage = totalPages == 0 ? 1 : Math.min(requestedPage, totalPages);
        int fromIndex = totalMemberCount == 0 ? 0 : (currentPage - 1) * MEMBER_PAGE_SIZE;
        int toIndex = Math.min(fromIndex + MEMBER_PAGE_SIZE, totalMemberCount);
        List<CustomerMembership> pagedMemberList = new ArrayList<>(memberList.subList(fromIndex, toIndex));

        int totalRankCount = activeRankList.size();
        int totalRankPages = (totalRankCount + RANK_PAGE_SIZE - 1) / RANK_PAGE_SIZE;
        int currentRankPage = totalRankPages == 0 ? 1 : Math.min(requestedRankPage, totalRankPages);
        int rankFromIndex = totalRankCount == 0 ? 0 : (currentRankPage - 1) * RANK_PAGE_SIZE;
        int rankToIndex = Math.min(rankFromIndex + RANK_PAGE_SIZE, totalRankCount);
        List<MembershipRank> pagedRankList
                = new ArrayList<>(activeRankList.subList(rankFromIndex, rankToIndex));

        request.setAttribute("rankList", activeRankList);
        request.setAttribute("pagedRankList", pagedRankList);
        request.setAttribute("memberList", pagedMemberList);
        request.setAttribute("membershipRanks", activeRankList);
        request.setAttribute("membershipMembers", pagedMemberList);
        request.setAttribute("totalMembers", totalMemberCount);
        request.setAttribute("activeMembers", activeMembers);
        request.setAttribute("totalSpent", totalSpent);
        request.setAttribute("topRank", topRank);
        request.setAttribute("topDiscount", topDiscount);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageStart", totalMemberCount == 0 ? 0 : fromIndex + 1);
        request.setAttribute("pageEnd", toIndex);
        request.setAttribute("totalRanks", totalRankCount);
        request.setAttribute("currentRankPage", currentRankPage);
        request.setAttribute("totalRankPages", totalRankPages);
        request.setAttribute("rankPageStart", totalRankCount == 0 ? 0 : rankFromIndex + 1);
        request.setAttribute("rankPageEnd", rankToIndex);
        request.setAttribute("searchValue", key);
        request.setAttribute("selectedRankId", rankId);
        request.setAttribute("selectedStatus", status);
        request.getRequestDispatcher("/views/admin/admin_membership.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
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

        HashMap<String, Object> result = new HashMap<>();
        
         
        try {
            if ("createRank".equals(action)) {
                String rankName = request.getParameter("rankName");
                BigDecimal minTotalSpent = new BigDecimal(request.getParameter("minTotalSpent"));
                int discountValue = Integer.parseInt(request.getParameter("discountValue"));
                boolean isDeleted = Boolean.parseBoolean(request.getParameter("isDeleted"));

                MembershipRank rank = new MembershipRank(rankName, minTotalSpent, discountValue, isDeleted);

                result = membershipService.checkInseartRanking(rank);
                if (result.containsKey("success")) {
                    int rankId = (Integer) result.get("rankId");
                    HashMap<String, Object> logResult = activityService.ctreatActiveLog(
                            new ActivityLog(
                                    user.getAccountId(),
                                    "Tạo mới hạng thành viên",
                                    "MembershipRanks",
                                    rankId,
                                    null,
                                    rankName,
                                    "Success",
                                    0,
                                    LocalDateTime.now()
                            )
                    );

                    if (logResult.containsKey("error")) {
                        result.put("activityLogError", logResult.get("error"));
                    }
                }
            } else if ("updateRank".equals(action)) {
                int rankId = Integer.parseInt(request.getParameter("rankId"));
                String rankName = request.getParameter("rankName");
                BigDecimal minTotalSpent = new BigDecimal(request.getParameter("minTotalSpent"));
                int discountValue = Integer.parseInt(request.getParameter("discountValue"));
                boolean isDeleted = Boolean.parseBoolean(request.getParameter("isDeleted"));

                MembershipRank rank = new MembershipRank(rankId, rankName, minTotalSpent, discountValue, isDeleted);
                result = membershipService.chekUpdateRanking(rank);
            } else if ("toggleMemberStatus".equals(action)) {
                int id = Integer.parseInt(request.getParameter("customerId"));
                result = membershipService.updateLockId(id);
            }
            if (result.containsKey("error")) {
                session.setAttribute("toastError", result.get("error").toString());
            } else if (result.containsKey("success")) {
                session.setAttribute("toastMessage", result.get("success").toString());
                if (result.containsKey("activityLogError")) {
                    session.setAttribute("toastError",
                            "Đã tạo hạng thành viên nhưng không thể ghi nhật ký hoạt động");
                }
            }
        } catch (Exception e) {
            session.setAttribute("toastError", "Du lieu khong hop le");

        }

        String redirectUrl = request.getContextPath() + "/admin/memberships";
        if ("toggleMemberStatus".equals(action)) {
            String returnPage = request.getParameter("returnPage");
            String returnSearch = request.getParameter("returnSearch");
            String returnRankId = request.getParameter("returnRankId");
            String returnStatus = request.getParameter("returnStatus");
            redirectUrl += "?page=" + encode(returnPage)
                    + "&search=" + encode(returnSearch)
                    + "&rankId=" + encode(returnRankId)
                    + "&status=" + encode(returnStatus);
        }
        response.sendRedirect(redirectUrl);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
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
