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
import model.Category;
import model.CustomerDiscountCode;
import model.DiscountCode;
import model.Product;
import services.CategoryService;
import services.DiscountCodeService;
import services.ProductService;

/**
 *
 * @author admin
 */
public class HomePageServlet extends HttpServlet {

    private static final ProductService Pservice = new ProductService();
    private static final CategoryService Cservice = new CategoryService();
    private static final DiscountCodeService Dservice = new DiscountCodeService();

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
        HashMap<String, Object> s3 = Dservice.getDiscountCode();
        List<DiscountCode> listD = null;
        if (s3.containsKey("error")) {
            request.setAttribute("error", s3.get("error").toString());
        } else {
            listD = (List<DiscountCode>) s3.get("success");
        }
        List<Category> list = null;
        HashMap<String, Object> s2 = Cservice.getCategory();
        if (s2.containsKey("error")) {
            request.setAttribute("error", s2.get("error").toString());

        } else {
            list = (List<Category>) s2.get("success");
        }

        HashMap<String, Object> s = Pservice.getBestSellingProducts(5);
        List<model.Product> featuredProducts = null;
        if (s.containsKey("error")) {
            request.setAttribute("error", s.get("error").toString());

        } else {
            featuredProducts = (List<model.Product>) s.get("success");
        }

        // ── Load voucher cá nhân cho khách hàng đã đăng nhập ──────────────
        Object userObj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser") : null;
        if (userObj instanceof dto.UserLoginDTO) {
            dto.UserLoginDTO currentUser = (dto.UserLoginDTO) userObj;
            // Chỉ load voucher cho role Customer (roleId = 2)
            if (currentUser.getRoleId() == 2) {
                try {
                    dao.DiscountCodeDAO discountCodeDAO = new dao.DiscountCodeDAO();
                    List<CustomerDiscountCode> customerVouchers =
                            discountCodeDAO.getVoucherById(currentUser.getAccountId());
                    request.setAttribute("customerVouchers", customerVouchers);
                    request.setAttribute("voucherCount", customerVouchers != null ? customerVouchers.size() : 0);
                } catch (Exception e) {
                    System.err.println("[HomePageServlet] Load voucher error: " + e.getMessage());
                }
            }
        }

        request.setAttribute("Listd", listD);
        request.setAttribute("ListP", list);
        request.setAttribute("featuredProducts", featuredProducts);
        request.getRequestDispatcher("views/HomePage/HomePage.jsp").forward(request, response);
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
