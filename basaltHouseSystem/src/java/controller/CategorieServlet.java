/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Product;

/**
 *
 * @author admin
 */
public class CategorieServlet extends HttpServlet {

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
            out.println("<title>Servlet CategorieServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategorieServlet at " + request.getContextPath() + "</h1>");
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
        CategoryDAO cateDAO = new CategoryDAO();
        ProductDAO pdao = new ProductDAO();
        List<Product> product = new ArrayList<>();
        int categoryId = 1;
        String categoryParam = request.getParameter("category");
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                categoryId = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                categoryId = 1;
            }
        }
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }
        boolean isSearchResult = keyword != null && !keyword.isBlank();
        if (!isSearchResult) {
            product = pdao.getProductByCategory(categoryId);

        } else {
            product = pdao.getProductByName(keyword);
        }

        List<Category> list = cateDAO.getCategory();
        String categoryTitle = "Danh mục";
        for (Category c : list) {
            if (c.getCategoryId() == categoryId) {
                categoryTitle = c.getCategoryName();
                break;
            }
        }
        if (isSearchResult) {
            categoryTitle = "K\u1ebft qu\u1ea3 t\u00ecm ki\u1ebfm";
        }

        request.setAttribute("listP", product);
        request.setAttribute("ListC", list);
        request.setAttribute("currentCategory", isSearchResult ? 0 : categoryId);
        request.setAttribute("currentKeyword", keyword);
        request.setAttribute("isSearchResult", isSearchResult);
        request.setAttribute("categoryTitle", categoryTitle);
        request.setAttribute("productCount", product.size());
        request.getRequestDispatcher("views/Categories/Categorie.jsp").forward(request, response);
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
        processRequest(request, response);
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
