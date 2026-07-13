package dao;

import dto.OrderDTO;
import model.DeliveryLog;
import model.Order;
import model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminOrderDAO — truy vấn dữ liệu Đơn hàng cho trang Admin.
 * Pattern giống AdminProductDAO: dùng CTE phân trang + JOIN nhiều bảng,
 * map kết quả vào LinkedHashMap để tránh trùng.
 */
public class AdminOrderDAO extends DBContext {

    // ══════════════════════════════════════════════════════════════════
    // 1. LẤY DANH SÁCH ĐƠN HÀNG (CÓ PHÂN TRANG + BỘ LỌC)
    // Bảng JOIN: Orders, Customers, Shippers, DiscountCodes,
    //            OrderAddresses, DeliveryZones, Tables, TableSessions
    // ══════════════════════════════════════════════════════════════════
    public List<OrderDTO> getOrdersWithFullDetails(
            String search, String orderType, String orderStatus,
            String paymentStatus, int offset, int limit) {

        Map<Integer, OrderDTO> orderMap = new LinkedHashMap<>();

        // ── Phần CTE phân trang ────────────────────────────────────────
        String sql = "WITH PagedOrders AS ( "
                + "  SELECT o.OrderId FROM Orders o "
                + "  LEFT JOIN Customers cu ON o.CustomerId = cu.CustomerId "
                + "  WHERE o.IsDeleted = 0 ";

        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (cu.FullName LIKE ? OR CAST(o.OrderId AS NVARCHAR) LIKE ?) ";
        }
        if (orderType != null && !orderType.trim().isEmpty()) {
            sql += " AND o.OrderType = ? ";
        }
        if (orderStatus != null && !orderStatus.trim().isEmpty()) {
            sql += " AND o.OrderStatus = ? ";
        }
        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            sql += " AND o.PaymentStatus = ? ";
        }

        sql += "  ORDER BY o.CreatedAt DESC "
                + "  OFFSET ? ROWS FETCH NEXT ? ROWS ONLY "
                + ") "
                // ── Phần SELECT chính JOIN tất cả bảng ──────────────────
                + "SELECT "
                + "  o.OrderId, o.CustomerId, o.CashierId, o.ShipperId, "
                + "  o.TableSessionId, o.OrderAddressId, o.DiscountId, "
                + "  o.OrderType, o.OrderStatus, o.PaymentMethod, o.PaymentStatus, "
                + "  o.TotalAmount, o.DiscountAmount, o.FinalAmount, "
                + "  o.CreatedAt, o.IsDeleted, "
                // Customers
                + "  cu.FullName AS CustomerName, cu.Phone AS CustomerPhone, cu.AvatarUrl AS CustomerAvatar, "
                // Shippers
                + "  sh.FullName AS ShipperName, sh.Phone AS ShipperPhone, "
                // DiscountCodes
                + "  dc.Code AS DiscountCode, "
                // OrderAddresses + DeliveryZones
                + "  oa.RecipientName, oa.RecipientPhone, oa.AddressDetail, "
                + "  dz.WardName, dz.District, dz.Province, "
                // Tables (qua TableSessions)
                + "  t.TableCode, "
                // OrderDetails
                + "  od.OrderDetailId, od.ProductId, od.SizeId, od.Quantity, od.UnitPrice, od.Note AS DetailNote, "
                + "  p.ProductName, sz.SizeName, p.ImageUrl AS ProductImageUrl, "
                // DeliveryLogs
                + "  dl.DeliveryLogId, dl.Status AS DeliveryStatus, "
                + "  dl.EstimatedDeliveryAt, dl.PickedUpAt, dl.ShipperConfirmedAt, "
                + "  dl.CustomerConfirmedAt, dl.DeliveredAt, dl.IsOverdue, dl.Note AS DeliveryNote "
                + "FROM Orders o "
                + "INNER JOIN PagedOrders po ON o.OrderId = po.OrderId "
                + "LEFT JOIN Customers cu       ON o.CustomerId = cu.CustomerId "
                + "LEFT JOIN Shippers sh        ON o.ShipperId  = sh.ShipperId "
                + "LEFT JOIN DiscountCodes dc   ON o.DiscountId = dc.DiscountId "
                + "LEFT JOIN OrderAddresses oa  ON o.OrderAddressId = oa.OrderAddressId "
                + "LEFT JOIN DeliveryZones dz   ON oa.ZoneId = dz.ZoneId "
                + "LEFT JOIN TableSessions ts   ON o.TableSessionId = ts.SessionId "
                + "LEFT JOIN Tables t           ON ts.TableId = t.TableId "
                + "LEFT JOIN OrderDetails od    ON o.OrderId = od.OrderId AND od.IsDeleted = 0 "
                + "LEFT JOIN Products p         ON od.ProductId = p.ProductId "
                + "LEFT JOIN Sizes sz           ON od.SizeId = sz.SizeId "
                + "LEFT JOIN DeliveryLogs dl    ON o.OrderId = dl.OrderId AND dl.IsDeleted = 0 "
                + "ORDER BY o.CreatedAt DESC, o.OrderId, od.OrderDetailId, dl.DeliveryLogId";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            int idx = 1;

            if (search != null && !search.trim().isEmpty()) {
                st.setString(idx++, "%" + search + "%");
                st.setString(idx++, "%" + search + "%");
            }
            if (orderType != null && !orderType.trim().isEmpty()) {
                st.setString(idx++, orderType);
            }
            if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                st.setString(idx++, orderStatus);
            }
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                st.setString(idx++, paymentStatus);
            }
            st.setInt(idx++, offset);
            st.setInt(idx,   limit);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("OrderId");

                    // ── Tạo OrderDTO nếu chưa có ────────────────────────
                    if (!orderMap.containsKey(orderId)) {
                        Order o = new Order();
                        o.setOrderId(orderId);
                        o.setCustomerId(getNullableInt(rs, "CustomerId"));
                        o.setCashierId(getNullableInt(rs, "CashierId"));
                        o.setShipperId(getNullableInt(rs, "ShipperId"));
                        o.setTableSessionId(getNullableInt(rs, "TableSessionId"));
                        o.setOrderAddressId(getNullableInt(rs, "OrderAddressId"));
                        o.setDiscountId(getNullableInt(rs, "DiscountId"));
                        o.setOrderType(rs.getString("OrderType"));
                        o.setOrderStatus(rs.getString("OrderStatus"));
                        o.setPaymentMethod(rs.getString("PaymentMethod"));
                        o.setPaymentStatus(rs.getString("PaymentStatus"));
                        o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                        o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                        o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                        o.setIsDeleted(rs.getBoolean("IsDeleted"));

                        Timestamp ts = rs.getTimestamp("CreatedAt");
                        if (ts != null) o.setCreatedAt(ts.toLocalDateTime());

                        OrderDTO dto = new OrderDTO(o);
                        dto.setCustomerName(rs.getString("CustomerName"));
                        dto.setCustomerPhone(rs.getString("CustomerPhone"));
                        dto.setCustomerAvatar(rs.getString("CustomerAvatar"));
                        dto.setShipperName(rs.getString("ShipperName"));
                        dto.setShipperPhone(rs.getString("ShipperPhone"));
                        dto.setDiscountCode(rs.getString("DiscountCode")); // tự gọi buildDiscountDisplay()

                        dto.setRecipientName(rs.getString("RecipientName"));
                        dto.setRecipientPhone(rs.getString("RecipientPhone"));
                        dto.setAddressDetail(rs.getString("AddressDetail"));

                        // Ghép chuỗi địa chỉ: "12 Hàng Bạc, Hoàn Kiếm, Hà Nội"
                        String ward     = rs.getString("WardName");
                        String district = rs.getString("District");
                        String province = rs.getString("Province");
                        if (ward != null) {
                            dto.setWardDistrict(ward + ", " + district + ", " + province);
                        }

                        dto.setTableCode(rs.getString("TableCode"));
                        orderMap.put(orderId, dto);
                    }

                    OrderDTO current = orderMap.get(orderId);

                    // ── Thêm OrderDetail (nếu có) ────────────────────────
                    int odId = rs.getInt("OrderDetailId");
                    if (!rs.wasNull()) {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderDetailId(odId);
                        detail.setOrderId(orderId);
                        detail.setProductId(rs.getInt("ProductId"));
                        detail.setSizeId(rs.getInt("SizeId"));
                        detail.setQuantity(rs.getInt("Quantity"));
                        detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                        detail.setNote(rs.getString("DetailNote"));
                        detail.setProductName(rs.getString("ProductName"));
                        detail.setSizeName(rs.getString("SizeName"));
                        current.addOrderDetail(detail);
                    }

                    // ── Thêm DeliveryLog (nếu có) ────────────────────────
                    int dlId = rs.getInt("DeliveryLogId");
                    if (!rs.wasNull()) {
                        DeliveryLog dl = new DeliveryLog();
                        dl.setDeliveryLogId(dlId);
                        dl.setOrderId(orderId);
                        dl.setStatus(rs.getString("DeliveryStatus"));
                        dl.setNote(rs.getString("DeliveryNote"));
                        dl.setIsOverdue(rs.getBoolean("IsOverdue"));

                        Timestamp eta = rs.getTimestamp("EstimatedDeliveryAt");
                        if (eta != null) dl.setEstimatedDeliveryAt(eta.toLocalDateTime());

                        Timestamp picked = rs.getTimestamp("PickedUpAt");
                        if (picked != null) dl.setPickedUpAt(picked.toLocalDateTime());

                        Timestamp shipConf = rs.getTimestamp("ShipperConfirmedAt");
                        if (shipConf != null) dl.setShipperConfirmedAt(shipConf.toLocalDateTime());

                        Timestamp custConf = rs.getTimestamp("CustomerConfirmedAt");
                        if (custConf != null) dl.setCustomerConfirmedAt(custConf.toLocalDateTime());

                        Timestamp delivered = rs.getTimestamp("DeliveredAt");
                        if (delivered != null) dl.setDeliveredAt(delivered.toLocalDateTime());

                        current.addDeliveryLog(dl);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách đơn hàng (Admin): " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>(orderMap.values());
    }

    // ══════════════════════════════════════════════════════════════════
    // 2. LẤY CHI TIẾT 1 ĐƠN HÀNG theo OrderId
    // ══════════════════════════════════════════════════════════════════
    public OrderDTO getOrderDetail(int orderId) {
        String sql = "SELECT "
                + "  o.OrderId, o.CustomerId, o.CashierId, o.ShipperId, "
                + "  o.TableSessionId, o.OrderAddressId, o.DiscountId, "
                + "  o.OrderType, o.OrderStatus, o.PaymentMethod, o.PaymentStatus, "
                + "  o.TotalAmount, o.DiscountAmount, o.FinalAmount, "
                + "  o.CreatedAt, o.IsDeleted, "
                + "  cu.FullName AS CustomerName, cu.Phone AS CustomerPhone, cu.AvatarUrl AS CustomerAvatar, "
                + "  sh.FullName AS ShipperName, sh.Phone AS ShipperPhone, "
                + "  dc.Code AS DiscountCode, "
                + "  oa.RecipientName, oa.RecipientPhone, oa.AddressDetail, "
                + "  dz.WardName, dz.District, dz.Province, "
                + "  t.TableCode, "
                + "  od.OrderDetailId, od.ProductId, od.SizeId, od.Quantity, od.UnitPrice, od.Note AS DetailNote, "
                + "  p.ProductName, sz.SizeName, p.ImageUrl AS ProductImageUrl, "
                + "  dl.DeliveryLogId, dl.Status AS DeliveryStatus, "
                + "  dl.EstimatedDeliveryAt, dl.PickedUpAt, dl.ShipperConfirmedAt, "
                + "  dl.CustomerConfirmedAt, dl.DeliveredAt, dl.IsOverdue, dl.Note AS DeliveryNote "
                + "FROM Orders o "
                + "LEFT JOIN Customers cu      ON o.CustomerId      = cu.CustomerId "
                + "LEFT JOIN Shippers sh       ON o.ShipperId       = sh.ShipperId "
                + "LEFT JOIN DiscountCodes dc  ON o.DiscountId      = dc.DiscountId "
                + "LEFT JOIN OrderAddresses oa ON o.OrderAddressId  = oa.OrderAddressId "
                + "LEFT JOIN DeliveryZones dz  ON oa.ZoneId         = dz.ZoneId "
                + "LEFT JOIN TableSessions ts  ON o.TableSessionId  = ts.SessionId "
                + "LEFT JOIN Tables t          ON ts.TableId        = t.TableId "
                + "LEFT JOIN OrderDetails od   ON o.OrderId = od.OrderId AND od.IsDeleted = 0 "
                + "LEFT JOIN Products p        ON od.ProductId      = p.ProductId "
                + "LEFT JOIN Sizes sz          ON od.SizeId         = sz.SizeId "
                + "LEFT JOIN DeliveryLogs dl   ON o.OrderId = dl.OrderId AND dl.IsDeleted = 0 "
                + "WHERE o.OrderId = ? AND o.IsDeleted = 0 "
                + "ORDER BY od.OrderDetailId, dl.DeliveryLogId";

        OrderDTO dto = null;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, orderId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    if (dto == null) {
                        Order o = new Order();
                        o.setOrderId(rs.getInt("OrderId"));
                        o.setCustomerId(getNullableInt(rs, "CustomerId"));
                        o.setShipperId(getNullableInt(rs, "ShipperId"));
                        o.setOrderAddressId(getNullableInt(rs, "OrderAddressId"));
                        o.setDiscountId(getNullableInt(rs, "DiscountId"));
                        o.setOrderType(rs.getString("OrderType"));
                        o.setOrderStatus(rs.getString("OrderStatus"));
                        o.setPaymentMethod(rs.getString("PaymentMethod"));
                        o.setPaymentStatus(rs.getString("PaymentStatus"));
                        o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                        o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                        o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                        o.setIsDeleted(rs.getBoolean("IsDeleted"));
                        Timestamp ts = rs.getTimestamp("CreatedAt");
                        if (ts != null) o.setCreatedAt(ts.toLocalDateTime());

                        dto = new OrderDTO(o);
                        dto.setCustomerName(rs.getString("CustomerName"));
                        dto.setCustomerPhone(rs.getString("CustomerPhone"));
                        dto.setCustomerAvatar(rs.getString("CustomerAvatar"));
                        dto.setShipperName(rs.getString("ShipperName"));
                        dto.setShipperPhone(rs.getString("ShipperPhone"));
                        dto.setDiscountCode(rs.getString("DiscountCode"));
                        dto.setRecipientName(rs.getString("RecipientName"));
                        dto.setRecipientPhone(rs.getString("RecipientPhone"));
                        dto.setAddressDetail(rs.getString("AddressDetail"));

                        String ward     = rs.getString("WardName");
                        String district = rs.getString("District");
                        String province = rs.getString("Province");
                        if (ward != null) {
                            dto.setWardDistrict(ward + ", " + district + ", " + province);
                        }
                        dto.setTableCode(rs.getString("TableCode"));
                    }

                    int odId = rs.getInt("OrderDetailId");
                    if (!rs.wasNull()) {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderDetailId(odId);
                        detail.setOrderId(orderId);
                        detail.setProductId(rs.getInt("ProductId"));
                        detail.setSizeId(rs.getInt("SizeId"));
                        detail.setQuantity(rs.getInt("Quantity"));
                        detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                        detail.setNote(rs.getString("DetailNote"));
                        detail.setProductName(rs.getString("ProductName"));
                        detail.setSizeName(rs.getString("SizeName"));
                        dto.addOrderDetail(detail);
                    }

                    int dlId = rs.getInt("DeliveryLogId");
                    if (!rs.wasNull()) {
                        DeliveryLog dl = new DeliveryLog();
                        dl.setDeliveryLogId(dlId);
                        dl.setOrderId(orderId);
                        dl.setStatus(rs.getString("DeliveryStatus"));
                        dl.setNote(rs.getString("DeliveryNote"));
                        dl.setIsOverdue(rs.getBoolean("IsOverdue"));
                        Timestamp eta = rs.getTimestamp("EstimatedDeliveryAt");
                        if (eta != null) dl.setEstimatedDeliveryAt(eta.toLocalDateTime());
                        Timestamp shipConf = rs.getTimestamp("ShipperConfirmedAt");
                        if (shipConf != null) dl.setShipperConfirmedAt(shipConf.toLocalDateTime());
                        Timestamp custConf = rs.getTimestamp("CustomerConfirmedAt");
                        if (custConf != null) dl.setCustomerConfirmedAt(custConf.toLocalDateTime());
                        Timestamp delivered = rs.getTimestamp("DeliveredAt");
                        if (delivered != null) dl.setDeliveredAt(delivered.toLocalDateTime());
                        dto.addDeliveryLog(dl);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy chi tiết đơn hàng #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return dto;
    }

    // ══════════════════════════════════════════════════════════════════
    // 3. ĐẾM SỐ ĐƠN HÀNG (Phục vụ phân trang + thẻ KPI)
    // ══════════════════════════════════════════════════════════════════
    public int countOrders(String search, String orderType, String orderStatus, String paymentStatus) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT o.OrderId) FROM Orders o "
                + "LEFT JOIN Customers cu ON o.CustomerId = cu.CustomerId "
                + "WHERE o.IsDeleted = 0 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (cu.FullName LIKE ? OR CAST(o.OrderId AS NVARCHAR) LIKE ?) ");
        }
        if (orderType != null && !orderType.trim().isEmpty()) {
            sql.append(" AND o.OrderType = ? ");
        }
        if (orderStatus != null && !orderStatus.trim().isEmpty()) {
            sql.append(" AND o.OrderStatus = ? ");
        }
        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            sql.append(" AND o.PaymentStatus = ? ");
        }

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (search != null && !search.trim().isEmpty()) {
                st.setString(idx++, "%" + search + "%");
                st.setString(idx++, "%" + search + "%");
            }
            if (orderType != null && !orderType.trim().isEmpty())     st.setString(idx++, orderType);
            if (orderStatus != null && !orderStatus.trim().isEmpty()) st.setString(idx++, orderStatus);
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) st.setString(idx++, paymentStatus);

            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ══════════════════════════════════════════════════════════════════
    // 4. ĐẾM KPI — đếm theo trạng thái đơn cụ thể (không filter search)
    // Dùng cho các thẻ: Tất cả / Pending / Delivering / Done / Cancelled
    // ══════════════════════════════════════════════════════════════════
    public int countOrdersByStatus(String orderStatus) {
        String sql = orderStatus == null
                ? "SELECT COUNT(*) FROM Orders WHERE IsDeleted = 0"
                : "SELECT COUNT(*) FROM Orders WHERE IsDeleted = 0 AND OrderStatus = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (orderStatus != null) st.setString(1, orderStatus);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ══════════════════════════════════════════════════════════════════
    // 5. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    // ══════════════════════════════════════════════════════════════════
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET OrderStatus = ? WHERE OrderId = ? AND IsDeleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, newStatus);
            st.setInt(2, orderId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái đơn #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // 6. XÓA MỀM ĐƠN HÀNG
    // ══════════════════════════════════════════════════════════════════
    public boolean softDeleteOrder(int orderId) {
        String sql = "UPDATE Orders SET IsDeleted = 1 WHERE OrderId = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, orderId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // HELPER: đọc Integer nullable từ ResultSet
    // ══════════════════════════════════════════════════════════════════
    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int val = rs.getInt(col);
        return rs.wasNull() ? null : val;
    }
}
