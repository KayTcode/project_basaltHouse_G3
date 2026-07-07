/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminProductService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author MSI
 */
public class AdminProductServlet extends HttpServlet {
    // Khai báo Service điều hướng dữ liệu

    private final AdminProductService productService = new AdminProductService();

    // Thiết lập số lượng sản phẩm trên mỗi trang (Ví dụ: 10 sản phẩm/trang)
    private static final int PAGE_SIZE = 10;

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
            out.println("<title>Servlet AdminProductServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminProductServlet at " + request.getContextPath() + "</h1>");
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
        // 1. Thu thập các tham số lọc từ Request (URL hoặc Form gửi lên)
        String search = request.getParameter("search");
        String categoryId = request.getParameter("categoryId");
        String pageStr = request.getParameter("page");

        // 2. Gọi Service để lấy toàn bộ dữ liệu (Danh sách DTO đã JOIN 5 bảng, phân trang, KPI)
        Map<String, Object> dashboardData = productService.getProductDashboardData(search, categoryId, pageStr, PAGE_SIZE);

        // 3. Đẩy nguyên cục Map dữ liệu vào Request Attribute để ngoài JSP dùng JSTL (`${...}`) đọc ra
        request.setAttribute("data", dashboardData); //màn hình yêu cầu set dữ liêu trên UI
        System.out.println("database: " + dashboardData.toString());
        // LƯU Ý TÙY CHỌN: Nếu bạn cần hiển thị danh sách danh mục ở bộ lọc `<select>` ngoài giao diện, 
        // bạn có thể gọi thêm CategoryDAO tại đây rồi đẩy ra:
        // request.setAttribute("categories", new CategoryDAO().getAllCategories());

        // 4. Chuyển tiếp (Forward) yêu cầu và dữ liệu sang file JSP hiển thị giao diện công việc
        request.getRequestDispatcher("/views/admin/admin_product.jsp").forward(request, response);
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
        // Đảm bảo đồng bộ hóa tiếng Việt khi nhận dữ liệu từ Form POST
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                case "add":
                    // 1. Dữ liệu cơ bản của Product
                    String name = request.getParameter("productName");
                    String description = request.getParameter("description");
                    String catId = request.getParameter("categoryId");
                    String price = request.getParameter("price");
                    String imgUrl = request.getParameter("imageUrl");

                    // 2. Dữ liệu mảng của ProductSizes
                    String[] sizeIds = request.getParameterValues("sizeIds");
                    String[] sizePrices = request.getParameterValues("sizePrices");

                    // 3. Dữ liệu mảng của Recipes & Ingredients
                    String[] ingredientIds = request.getParameterValues("ingredientIds");
                    String[] ingredientNames = request.getParameterValues("ingredientNames"); // Dùng khi người dùng nhập tên nguyên liệu mới
                    String[] quantities = request.getParameterValues("quantities");
                    String[] units = request.getParameterValues("units");

                    // 4. Chuyển cho Service xử lý
                    boolean isSuccess = productService.processAddProduct(
                            name, description, catId, price, imgUrl,
                            sizeIds, sizePrices,
                            ingredientIds, ingredientNames, quantities, units
                    );

                    if (isSuccess) {
                        request.getSession().setAttribute("toastMessage", "Thêm sản phẩm mới thành công!");
                    } else {
                        request.getSession().setAttribute("toastError", "Lỗi: Không thể thêm sản phẩm!");
                    }
                    break;

                case "edit":
                    // Lấy dữ liệu gửi lên từ form Sửa
                    String editProductId = request.getParameter("productId"); 
                    String editName = request.getParameter("productName");
                    String editDescription = request.getParameter("description");
                    String editCatId = request.getParameter("categoryId");
                    String editPrice = request.getParameter("price");
                    String editImgUrl = request.getParameter("imageUrl");
                    String editIsActive = request.getParameter("isActive");

                    String[] editSizeIds = request.getParameterValues("sizeIds");
                    String[] editSizePrices = request.getParameterValues("sizePrices");
                    String[] editIngredientIds = request.getParameterValues("ingredientIds");
                    String[] editIngredientNames = request.getParameterValues("ingredientNames");
                    String[] editQuantities = request.getParameterValues("quantities");
                    String[] editUnits = request.getParameterValues("units");

                    // In ra Console để debug nếu lỗi
                    System.out.println("=== UPDATE SẢN PHẨM ID: " + editProductId + " ===");

                    boolean isEditSuccess = productService.processEditProduct(
                            editProductId, editName, editDescription, editCatId, editPrice, editImgUrl, editIsActive,
                            editSizeIds, editSizePrices,
                            editIngredientIds, editIngredientNames, editQuantities, editUnits
                    );

                    if (isEditSuccess) {
                        request.getSession().setAttribute("toastMessage", "Cập nhật sản phẩm thành công!");
                    } else {
                        request.getSession().setAttribute("toastError", "Lỗi: Không thể cập nhật sản phẩm!");
                    }
                    break;

                case "delete":
                    String deleteProductId = request.getParameter("productId");
                    boolean isDeleteSuccess = productService.processDeleteProduct(deleteProductId);

                    if (isDeleteSuccess) {
                        request.getSession().setAttribute("toastMessage", "Xóa sản phẩm thành công!");
                    } else {
                        request.getSession().setAttribute("toastError", "Lỗi: Không thể xóa sản phẩm!");
                    }
                    break;
            }
        }

        // Sau khi thực hiện POST (Thêm/Sửa/Xóa) xong, chuyển hướng (Redirect) về lại trang GET 
        // để tránh lỗi trùng lặp dữ liệu khi người dùng F5 (Tải lại trang)
        response.sendRedirect(request.getContextPath() + "/admin/products");
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