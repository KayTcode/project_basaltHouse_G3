package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Bill;


public class BillDAO extends DBContext {

  
    public boolean insertBill(Bill bill) throws Exception {
      
        if (bill.getBillCode() == null || bill.getBillCode().isEmpty()) {
            bill.setBillCode("BILL-" + bill.getOrderId() + "-" + System.currentTimeMillis());
        }
        String sql = """
                     INSERT INTO Bills (BillCode, OrderId, TableId, CashierId, SubTotal, DiscountAmount, FinalAmount, PaymentMethod, Note, PrintedAt, IsDeleted)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                     """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, bill.getBillCode());
            st.setInt(2, bill.getOrderId());
            if (bill.getTableId() != null) st.setInt(3, bill.getTableId()); else st.setNull(3, java.sql.Types.INTEGER);
            if (bill.getCashierId() != null) st.setInt(4, bill.getCashierId()); else st.setNull(4, java.sql.Types.INTEGER);
            st.setBigDecimal(5, bill.getSubTotal());
            st.setBigDecimal(6, bill.getDiscountAmount() != null ? bill.getDiscountAmount() : java.math.BigDecimal.ZERO);
            st.setBigDecimal(7, bill.getFinalAmount());
            st.setString(8, bill.getPaymentMethod());
            st.setString(9, bill.getNote());
            st.setTimestamp(10, Timestamp.valueOf(bill.getPrintedAt() != null ? bill.getPrintedAt() : LocalDateTime.now()));
            return st.executeUpdate() > 0;
        }
        
    }

    public List<Bill> getAllBillsForAdmin(String filterDate, String filterPayment) {
        List<Bill> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT b.BillId, b.BillCode, b.OrderId, b.TableId, b.CashierId, "
          + "       b.SubTotal, b.DiscountAmount, b.FinalAmount, b.PaymentMethod, b.Note, b.PrintedAt, "
          + "       o.OrderType, o.OrderStatus, "
          + "       COALESCE(c.FullName, 'Khach le') AS CustomerName, "
          + "       COALESCE(ca.FullName, N'---') AS CashierName "
          + "FROM Bills b "
          + "JOIN Orders o ON b.OrderId = o.OrderId "
          + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
          + "LEFT JOIN Cashiers ca ON b.CashierId = ca.CashierId "
          + "WHERE b.IsDeleted = 0 "
        );

        if (filterDate != null && !filterDate.isEmpty()) {
            sql.append("AND CAST(b.PrintedAt AS DATE) = ? ");
        }
        boolean isGroupFilter = false;
        if (filterPayment != null && !filterPayment.isEmpty()) {
            String fp = filterPayment.trim().toUpperCase();
            if ("QR CODE".equals(fp) || "TRANSFER".equals(fp) || "MOMO".equals(fp)) {
                sql.append("AND (UPPER(b.PaymentMethod) LIKE '%QR%' OR UPPER(b.PaymentMethod) LIKE '%MOMO%' OR UPPER(b.PaymentMethod) LIKE '%TRANSFER%' OR UPPER(b.PaymentMethod) LIKE '%BANK%') ");
                isGroupFilter = true;
            } else if ("CASH".equals(fp)) {
                sql.append("AND (UPPER(b.PaymentMethod) LIKE '%CASH%' OR UPPER(b.PaymentMethod) LIKE N'%TIỀN MẶT%') ");
                isGroupFilter = true;
            } else if ("COD".equals(fp)) {
                sql.append("AND UPPER(b.PaymentMethod) LIKE '%COD%' ");
                isGroupFilter = true;
            } else {
                sql.append("AND b.PaymentMethod = ? ");
            }
        }
        sql.append("ORDER BY b.PrintedAt DESC");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (filterDate != null && !filterDate.isEmpty()) st.setString(idx++, filterDate);
            if (filterPayment != null && !filterPayment.isEmpty() && !isGroupFilter) st.setString(idx++, filterPayment);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Bill b = new Bill();
                    b.setBillId(rs.getInt("BillId"));
                    b.setBillCode(rs.getString("BillCode"));
                    b.setOrderId(rs.getInt("OrderId"));
                    b.setTableId(rs.getObject("TableId") != null ? rs.getInt("TableId") : null);
                    b.setCashierId(rs.getObject("CashierId") != null ? rs.getInt("CashierId") : null);
                    b.setSubTotal(rs.getBigDecimal("SubTotal"));
                    b.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                    b.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                    b.setPaymentMethod(rs.getString("PaymentMethod"));
                    b.setNote(rs.getString("Note"));
                    Timestamp ts = rs.getTimestamp("PrintedAt");
                    if (ts != null) b.setPrintedAt(ts.toLocalDateTime());
                    b.setOrderType(rs.getString("OrderType"));
                    b.setOrderStatus(rs.getString("OrderStatus"));
                    b.setCustomerName(rs.getString("CustomerName"));
                    b.setCashierName(rs.getString("CashierName"));
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("[BillDAO] getAllBillsForAdmin error: " + e.getMessage());
        }
        return list;
    }

    public Bill getBillById(int billId) {
        String sql = "SELECT b.*, o.OrderType, o.OrderStatus, c.FullName AS CustomerName, ca.FullName AS CashierName "
          + "FROM Bills b "
          + "JOIN Orders o ON b.OrderId = o.OrderId "
          + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
          + "LEFT JOIN Cashiers ca ON b.CashierId = ca.CashierId "
          + "WHERE b.BillId = ? AND b.IsDeleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, billId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Bill b = new Bill();
                    b.setBillId(rs.getInt("BillId"));
                    b.setBillCode(rs.getString("BillCode"));
                    b.setOrderId(rs.getInt("OrderId"));
                    b.setCashierId(rs.getInt("CashierId") == 0 ? null : rs.getInt("CashierId"));
                    b.setTableId(rs.getInt("TableId") == 0 ? null : rs.getInt("TableId"));
                    b.setSubTotal(rs.getBigDecimal("SubTotal"));
                    b.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                    b.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                    b.setPaymentMethod(rs.getString("PaymentMethod"));
                    b.setNote(rs.getString("Note"));
                    java.sql.Timestamp ts = rs.getTimestamp("PrintedAt");
                    if (ts != null) b.setPrintedAt(ts.toLocalDateTime());
                    b.setOrderType(rs.getString("OrderType"));
                    b.setOrderStatus(rs.getString("OrderStatus"));
                    b.setCustomerName(rs.getString("CustomerName"));
                    b.setCashierName(rs.getString("CashierName"));
                    return b;
                }
            }
        } catch (Exception e) {
            System.err.println("[BillDAO] getBillById error: " + e.getMessage());
        }
        return null;
    }
}
