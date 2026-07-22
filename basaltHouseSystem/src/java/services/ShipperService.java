/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ShipperDAO;
import java.util.List;
import model.Order;
import model.OrderAddress;
import model.ProcessOrderResult;
import model.Shipper;

/**
 *
 * @author KayT
 */
public class ShipperService {

    private final ShipperDAO shipperDAO = new ShipperDAO();

    public Shipper getShipperByAccountId(int accountId) {
        try {
            return shipperDAO.getShipperByAccountId(accountId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> getPendingShipperOrders(int shipperId) {
        try {
            return shipperDAO.getPendingShipperOrders(shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Order getCurrentShippingOrder(int shipperId) {
        try {
            return shipperDAO.getCurrentShippingOrder(shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public OrderAddress getOrderAddress(Integer orderAddressId) {
        if (orderAddressId == null) {
            return null;
        }
        try {
            return shipperDAO.getOrderAddressById(orderAddressId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProcessOrderResult accecptOrder(int orderId, int shipperId) {
        ProcessOrderResult result = new ProcessOrderResult();
        if (orderId <= 0) {
            result.addError("Mã đơn hàng không hợp lệ");
            return result;
        }
        try {
            return shipperDAO.acceptOrder(orderId, shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            result.addError("Lỗi hệ thống!");
            return result;
        }
    }

    public ProcessOrderResult updateDeliveryStatus(int orderId, int shipperId, boolean isSuccess, String note, String proofImageUrl, String failReason) {
        ProcessOrderResult result = new ProcessOrderResult();

        if (!isSuccess && (failReason == null || failReason.trim().isEmpty())) {
            result.addError("Vui lòng nhập lí do giao hàng thất bại!");
            return result;
        }
        try {
            return shipperDAO.updateDeliveryStatus(orderId, shipperId, isSuccess, note, proofImageUrl, failReason);
        } catch (Exception e) {
            e.printStackTrace();
            result.addError("Lỗi hệ thống khi cập nhật trạng thái.");
            return result;
        }

    }
}
