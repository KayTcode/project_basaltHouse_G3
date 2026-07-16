package controller;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.SizeDAO;
import dao.CategoryDAO;
import dao.ShipperDAO;
import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import model.Product;
import model.Category;
import model.Order;
import model.Shipper;
import services.OrderService;
import services.StockService;


public class CashierServlet extends HttpServlet {
    
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/cashier/pos":
                handlePosPage(request, response);
                break;
                
            case "/cashier/oderview":
                handlePage(request, response);
                break;
                
            case "/cashier/dashboard":
                dao.OrderDAO orderDAO = new dao.OrderDAO();
                Map<String, Object> stats = orderDAO.getCashierDashboard();
                request.setAttribute("dashboard", stats);
                request.getRequestDispatcher("/views/Cashier/CashierDashboard.jsp").forward(request, response);
                break;

            case "/cashier/shippers":
                List<Shipper> activeShippers = new ShipperDAO().getActiveShippers();
                request.setAttribute("activeShippers", activeShippers);
                // truyền orderId nếu có (khi từ trang tạo đơn sang)
                String orderId = request.getParameter("orderId");
                if (orderId != null) request.setAttribute("orderId", orderId);
                request.getRequestDispatcher("/views/Cashier/AddShipper.jsp").forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Gán shipper cho đơn hàng ──
        String action = request.getServletPath();
        if ("/cashier/shippers".equals(action)) {
            String oId  = request.getParameter("orderId");
            String sId  = request.getParameter("shipperId");
            if (oId != null && sId != null) {
                try {
                    new ShipperDAO().assignShipper(Integer.parseInt(oId), Integer.parseInt(sId));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/cashier/oderview");
            return;
        }
        
        String cartData = request.getParameter("cartData");
        String totalAmountStr = request.getParameter("totalAmount");
        String paymentMethod = request.getParameter("paymentMethod");
        String tableName = request.getParameter("tableName");
        String note = request.getParameter("note");
        
        String customerIdStr = request.getParameter("customerId");
        String discountCode = request.getParameter("discountCode");
        String discountAmountStr = request.getParameter("discountAmount");
        String finalAmountStr = request.getParameter("finalAmount");
        String tableIdStr = request.getParameter("tableId");

        if (cartData == null || cartData.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Cart is empty");
            return;
        }
       Integer cashierId = null;
        var httpSession = request.getSession(false);
        if (httpSession != null) {
            Object attr = httpSession.getAttribute("cashierId");
            if (attr instanceof Integer) cashierId = (Integer) attr;
        }
        try {
            OrderService orderService = new OrderService();
             int orderId = orderService.createOfflineOrder(cartData, totalAmountStr, discountAmountStr, finalAmountStr,
              paymentMethod, tableName, note, customerIdStr, discountCode, tableIdStr, cashierId);
                                                          
            if (orderId != -1) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Order created successfully: " + orderId);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to create order");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to create order: " + e.getMessage());
        }
    }
    private void handlePage(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException{
        int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageStr);
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }
                int limit = 10;
                
                String typeParam = request.getParameter("type");
                if (typeParam == null || typeParam.isEmpty()) {
                    typeParam = "all";
                }

                OrderDAO oDao = new OrderDAO();
                List<Order> fullList = oDao.getAllOrdersWithCustomerName();
                
                if (!"all".equalsIgnoreCase(typeParam)) {
                    List<model.Order> filteredList = new ArrayList<>();
                    for (model.Order o : fullList) {
                        String oType = o.getOrderType() != null ? o.getOrderType().toLowerCase() : "pos";
                        if (typeParam.equalsIgnoreCase(oType)) {
                            filteredList.add(o);
                        }
                    }
                    fullList = filteredList;
                }
                
                int totalOrders = fullList.size();
                int totalPages = (int) Math.ceil((double) totalOrders / limit);
                
                if (page > totalPages && totalPages > 0) page = totalPages;
                if (page < 1) page = 1;
                
                int start = (page - 1) * limit;
                int end = Math.min(start + limit, totalOrders);
                
                List<model.Order> orderList = fullList.subList(start, end);
                
                request.setAttribute("orderList", orderList);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("currentType", typeParam);

                ProductDAO pDao = new ProductDAO();
                java.util.HashMap<Integer, model.Product> products = pDao.getProduct();
                request.setAttribute("products", products);
                
                SizeDAO sDao = new SizeDAO();
                java.util.HashMap<Integer, String> sizes = sDao.getSize();
                request.setAttribute("sizes", sizes);

                request.getRequestDispatcher("/views/Cashier/OrderViews.jsp").forward(request, response);
    }
    
    private void handlePosPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO pDao = new ProductDAO();
        List<Product> allProducts = pDao.getAllProductsForPOS();
 
        CategoryDAO cDao = new CategoryDAO();
        List<Category> categoryList = cDao.getAllCategories();

        StockService ssInit = new StockService();
        HashMap<Product, HashMap<String, Integer>> stockMapInit = ssInit.calculateProduct();
        HashMap<Integer, Integer> maxStockMap = new HashMap<>();
        for (Map.Entry<Product, HashMap<String, Integer>> entry : stockMapInit.entrySet()) {
            if (entry.getKey() == null) continue;
            int max = 0;
            for (Integer val : entry.getValue().values()) {
                if (val > max) max = val;
            }
            maxStockMap.put(entry.getKey().getProductId(), max);
        }

        
        int PAGE_SIZE = 6; 

        String catParam = request.getParameter("cat");
        if (catParam == null || catParam.isEmpty()) catParam = "all";

        String pageStr = request.getParameter("productPage");
        int productPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try { productPage = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
        }

        
        List<Product> filtered;
        if ("all".equalsIgnoreCase(catParam)) {
            filtered = allProducts;
        } else {
            filtered = new ArrayList<>();
            try {
                int catId = Integer.parseInt(catParam);
                for (Product p : allProducts) {
                    if (p.getCategoryId() == catId) filtered.add(p);
                }
            } catch (NumberFormatException e) {
                filtered = allProducts;
            }
        }

        
        int totalProducts = filtered.size();
        int totalProductPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        if (totalProductPages < 1) totalProductPages = 1;
        if (productPage < 1) productPage = 1;
        if (productPage > totalProductPages) productPage = totalProductPages;

        int start = (productPage - 1) * PAGE_SIZE;
        int end   = Math.min(start + PAGE_SIZE, totalProducts);
        List<Product> pagedProducts = filtered.subList(start, end);

       
        request.setAttribute("pagedProducts",   pagedProducts);
        request.setAttribute("categoryList",    categoryList);
        request.setAttribute("maxStockMap",     maxStockMap);
        request.setAttribute("productPage",     productPage);
        request.setAttribute("totalProductPages", totalProductPages);
        request.setAttribute("currentCat",      catParam);

        request.getRequestDispatcher("/views/Cashier/POSOrders.jsp").forward(request, response);
    }
}
