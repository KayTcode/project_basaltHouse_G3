package controller;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.SizeDAO;
import dao.TableDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import services.OrderService;


public class BartenderServlet extends HttpServlet {

   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/bartender/view".equals(path)) {
            handleBartenderView(request, response);
        } else if ("/bartender/history".equals(path)) {
            handleBartenderHistory(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderIdStr = request.getParameter("orderId");
        String action     = request.getParameter("action");

        if (orderIdStr == null || action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr.replaceAll("\\D+", ""));
            
            Integer cashierId = null;
            if (request.getSession(false) != null) {
                Object attr = request.getSession(false).getAttribute("cashierId");
                if (attr instanceof Integer) cashierId = (Integer) attr;
            }

            OrderService orderService = new OrderService();
            orderService.updateOrderStatus(orderId, action, cashierId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Success");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
     private <T> void paginateList(HttpServletRequest request, List<T> list, int limit, String pageParam, 
             String listAttr, String pageAttr, String totalPagesAttr, String totalItemsAttr) {
        int pageNum = 1;
        String ps = request.getParameter(pageParam);
        if (ps != null && !ps.isEmpty()) {
            try { pageNum = Integer.parseInt(ps); } catch (Exception ignored) {}
        }
        
        if (pageNum < 1) pageNum = 1;
        int total = list.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / limit));
        if (pageNum > totalPages) pageNum = totalPages;
        int from = (pageNum - 1) * limit;
        int to   = Math.min(from + limit, total);

        request.setAttribute(listAttr, list.subList(from, to));
        request.setAttribute(totalPagesAttr, totalPages);
        request.setAttribute(pageAttr, pageNum);
        if (totalItemsAttr != null) {
            request.setAttribute(totalItemsAttr, total);
        }
    }

    private void handleBartenderView(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
            dao.OrderDAO oDao = new dao.OrderDAO();
            List<Order> allOrders = oDao.getBartenderOrders();

            // Calculate active order counts per type (POS vs Online) in Backend
            int countPos = 0;
            int countOnline = 0;
            for (Order o : allOrders) {
                String type = o.getOrderType() != null ? o.getOrderType().toLowerCase() : "pos";
                if ("online".equals(type)) {
                    countOnline++;
                } else {
                    countPos++;
                }
            }
            request.setAttribute("countPos", countPos);
            request.setAttribute("countOnline", countOnline);

            String orderType = request.getParameter("type");
            if (orderType == null || orderType.trim().isEmpty()) {
                orderType = "pos";
            }
            orderType = orderType.toLowerCase();
            request.setAttribute("currentType", orderType);

            List<Order> typeFiltered = new ArrayList<>();
            for (Order o : allOrders) {
                String type = o.getOrderType() != null ? o.getOrderType().toLowerCase() : "pos";
                if (orderType.equals(type)) {
                    typeFiltered.add(o);
                }
            }
            allOrders = typeFiltered;

            String filter = request.getParameter("filter");
            if (filter != null && !filter.isEmpty() && !"all".equals(filter)) {
                List<Order> filtered = new ArrayList<>();
                for (Order o : allOrders) {
                    String loc;
                    if (o.getTableName() != null && !o.getTableName().isEmpty()) {
                        loc = o.getTableName();
                    } else if (o.getOrderType() != null && o.getOrderType().equalsIgnoreCase("online")) {
                        loc = "Online";
                    } else {
                        loc = "Walk-in";
                    }
                    if (filter.equals(loc)) filtered.add(o);
                }
                allOrders = filtered;
            }
            request.setAttribute("currentFilter", filter != null ? filter : "all");
            
            int limit = 5; 
            String[] statuses   = {"Preparing", "In_Progress", "Ready"};
            String[] paramNames = {"page_pending", "page_preparing", "page_ready"};
            String[] attrNames  = {"pendingList", "preparingList", "readyList"};
            String[] pgAttr     = {"pendingPages", "preparingPages", "readyPages"};
            String[] curAttr    = {"pendingPage",  "preparingPage",  "readyPage"};

            for (int ci = 0; ci < statuses.length; ci++) {
                final String status = statuses[ci];
                List<Order> col = new ArrayList<>();
                for (model.Order o : allOrders) {
                    if (status.equals(o.getOrderStatus())) col.add(o);
                }
                paginateList(request, col, limit, paramNames[ci], attrNames[ci], curAttr[ci], pgAttr[ci], "totalCol_" + ci);
            }

            dao.ProductDAO pDao = new dao.ProductDAO();
            request.setAttribute("products", pDao.getProduct());
            dao.SizeDAO sDao = new dao.SizeDAO();
            request.setAttribute("sizes", sDao.getSize());
            request.setAttribute("completedCount", oDao.getCompletedOrders().size());
            dao.TableDAO tDao = new dao.TableDAO();
            request.setAttribute("tablesList", tDao.getAllTablesWithOccupancy());

            request.getRequestDispatcher("/views/Bartender/BartenderViews.jsp").forward(request, response);
    }

    private void handleBartenderHistory(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
            String todayDateStr = java.time.LocalDate.now().toString();
            String historyDate = request.getParameter("historyDate");
            if (historyDate == null || historyDate.trim().isEmpty()) {
                historyDate = todayDateStr;
            }
            request.setAttribute("selectedHistoryDate", historyDate);
            request.setAttribute("todayDateStr", todayDateStr);

            String orderType = request.getParameter("orderType");
            if (orderType == null || orderType.trim().isEmpty()) {
                orderType = "all";
            }
            request.setAttribute("selectedOrderType", orderType);

            dao.OrderDAO oDao = new dao.OrderDAO();
            List<Order> allOrders = oDao.getCompletedOrdersByDate(historyDate, orderType);
            
            paginateList(request, allOrders, 6, "page", "orderList", 
                    "historyPage", "historyPages", "totalHistory");

            dao.ProductDAO pDao = new dao.ProductDAO();
            request.setAttribute("products", pDao.getProduct());
            dao.SizeDAO sDao = new dao.SizeDAO();
            request.setAttribute("sizes", sDao.getSize());

            request.getRequestDispatcher("/views/Bartender/BartenderHistory.jsp").forward(request, response);
    }
}
