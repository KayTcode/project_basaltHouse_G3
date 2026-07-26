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

    public ProcessOrderResult assignShipperToOrder(int orderId, int shipperId) {
        ProcessOrderResult result = new ProcessOrderResult();
        try {
            boolean ok = shipperDAO.assignShipper(orderId, shipperId);
            if (ok) {
                result.setSuccess(true);
            } else {
                result.addError("Không thể gán shipper: đơn #" + orderId
                        + " không còn ở trạng thái chờ gán (có thể đã có shipper khác nhận, "
                        + "hoặc đơn chưa ở trạng thái 'Waiting_Shipper').");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.addError("Lỗi hệ thống khi gán shipper.");
        }
        return result;
    }

    public ProcessOrderResult acceptOrder(int orderId, int shipperId) {
        try {
            return shipperDAO.acceptShipperOrder(orderId, shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            ProcessOrderResult result = new ProcessOrderResult();
            result.addError("Lỗi hệ thống khi nhận đơn.");
            return result;
        }
    }

    public boolean rejectOrder(int orderId, int shipperId) {
        try {
            return shipperDAO.rejectShipper(orderId, shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
