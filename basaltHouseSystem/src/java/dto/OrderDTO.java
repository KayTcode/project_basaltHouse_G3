package dto;

import model.Order;
import model.OrderDetail;
import model.DeliveryLog;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho module Quản lý Đơn hàng (Admin).
 * Gom Order + thông tin JOIN (khách hàng, shipper, địa chỉ, discount)
 * + danh sách OrderDetail và DeliveryLog vào một đối tượng duy nhất.
 */
public class OrderDTO {

    // ─── Đối tượng Order gốc ───────────────────────────────────────────
    private Order order;

    // ─── Thông tin JOIN từ các bảng liên quan ─────────────────────────
    private String customerName;     // Customers.FullName
    private String customerPhone;    // Customers.Phone
    private String customerAvatar;   // Customers.AvatarUrl

    private String shipperName;      // Shippers.FullName
    private String shipperPhone;     // Shippers.Phone

    private String discountCode;     // DiscountCodes.Code
    private String discountDisplay;  // "WELCOME10 (-8.000đ)"

    // Địa chỉ giao hàng (cho đơn Online)
    private String recipientName;    // OrderAddresses.RecipientName
    private String recipientPhone;   // OrderAddresses.RecipientPhone
    private String addressDetail;    // OrderAddresses.AddressDetail
    private String wardDistrict;     // DeliveryZones.WardName + District + Province

    // TableCode (cho đơn POS)
    private String tableCode;        // Tables.TableCode

    // ─── Thời gian đã format sẵn cho JSP ──────────────────────────────
    private String createdAtFormatted;

    // ─── Danh sách sản phẩm trong đơn ────────────────────────────────
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // ─── Lịch sử giao hàng ────────────────────────────────────────────
    private List<DeliveryLog> deliveryLogs = new ArrayList<>();

    // ─── Constructor ──────────────────────────────────────────────────
    public OrderDTO(Order order) {
        this.order = order;
        if (order.getCreatedAt() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            this.createdAtFormatted = order.getCreatedAt().format(fmt);
        } else {
            this.createdAtFormatted = "N/A";
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────
    public void addOrderDetail(OrderDetail detail) {
        boolean exists = orderDetails.stream()
                .anyMatch(d -> d.getOrderDetailId() == detail.getOrderDetailId());
        if (!exists) orderDetails.add(detail);
    }

    public void addDeliveryLog(DeliveryLog log) {
        boolean exists = deliveryLogs.stream()
                .anyMatch(l -> l.getDeliveryLogId() == log.getDeliveryLogId());
        if (!exists) deliveryLogs.add(log);
    }

    /** Tính nhãn hiển thị discount: "WELCOME10 (-8.000đ)" */
    public void buildDiscountDisplay() {
        if (discountCode == null || discountCode.isBlank()) {
            discountDisplay = null;
            return;
        }
        if (order.getDiscountAmount() != null
                && order.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            discountDisplay = discountCode
                    + " (-"
                    + String.format("%,.0f", order.getDiscountAmount())
                    + "đ)";
        } else {
            discountDisplay = discountCode;
        }
    }

    // ─── Getters & Setters ────────────────────────────────────────────
    public Order getOrder()                          { return order; }
    public String getCustomerName()                  { return customerName; }
    public void   setCustomerName(String v)          { this.customerName = v; }
    public String getCustomerPhone()                 { return customerPhone; }
    public void   setCustomerPhone(String v)         { this.customerPhone = v; }
    public String getCustomerAvatar()                { return customerAvatar; }
    public void   setCustomerAvatar(String v)        { this.customerAvatar = v; }
    public String getShipperName()                   { return shipperName; }
    public void   setShipperName(String v)           { this.shipperName = v; }
    public String getShipperPhone()                  { return shipperPhone; }
    public void   setShipperPhone(String v)          { this.shipperPhone = v; }
    public String getDiscountCode()                  { return discountCode; }
    public void   setDiscountCode(String v)          { this.discountCode = v; buildDiscountDisplay(); }
    public String getDiscountDisplay()               { return discountDisplay; }
    public String getRecipientName()                 { return recipientName; }
    public void   setRecipientName(String v)         { this.recipientName = v; }
    public String getRecipientPhone()                { return recipientPhone; }
    public void   setRecipientPhone(String v)        { this.recipientPhone = v; }
    public String getAddressDetail()                 { return addressDetail; }
    public void   setAddressDetail(String v)         { this.addressDetail = v; }
    public String getWardDistrict()                  { return wardDistrict; }
    public void   setWardDistrict(String v)          { this.wardDistrict = v; }
    public String getTableCode()                     { return tableCode; }
    public void   setTableCode(String v)             { this.tableCode = v; }
    public String getCreatedAtFormatted()            { return createdAtFormatted; }
    public List<OrderDetail> getOrderDetails()       { return orderDetails; }
    public List<DeliveryLog> getDeliveryLogs()       { return deliveryLogs; }
}
