import dao.IngredientDAO;
import dao.OrderDAO;
import dao.RecipeDAO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Ingredient;
import model.Order;
import model.OrderDetail;
import model.Recipe;

/**
 * BartenderService — xử lý toàn bộ luồng pha chế của bartender.
 * Issues 29, 30, 31.
 *
 * Owned Table (R/W): Orders, Ingredients
 * External Table (Read Only): OrderDetails, Recipes
 */
public class BartenderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private final RecipeDAO recipeDAO = new RecipeDAO();

    // ===== ISSUE 29 — Xem hàng đợi pha chế =====

    /**
     * Lấy danh sách đơn đã Paid và đang chờ bartender nhận (Preparing).
     * Chỉ đơn đã thanh toán mới xuất hiện — đảm bảo AC issue 29.
     */
    public List<Order> getPrepareQueue() {
        return orderDAO.getPaidPreparingOrders();
    }

    /**
     * Lấy danh sách món trong một đơn để hiển thị chi tiết queue.
     */
    public List<OrderDetail> getOrderItems(int orderId) {
        return orderDAO.getOrderDetailsByOrderId(orderId);
    }

    /**
     * Bartender nhận đơn — chuyển Preparing → In_Progress.
     * Validate: đơn phải ở trạng thái Preparing mới được nhận.
     */
    public boolean startPreparation(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) return false;
        if (!"Preparing".equalsIgnoreCase(order.getOrderStatus())) return false;

        orderDAO.updateOrderStatus(orderId, "In_Progress");
        return true;
    }
}