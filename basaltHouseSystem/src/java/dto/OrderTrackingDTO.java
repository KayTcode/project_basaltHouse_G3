package dto;

import java.util.List;
import model.Order;
import model.OrderAddress;
import model.OrderDetail;


public class OrderTrackingDTO {

    private Order order;
    private List<OrderDetail> details;
    private OrderAddress address;

    public OrderTrackingDTO() {}

    public OrderTrackingDTO(Order order, List<OrderDetail> details, OrderAddress address) {
        this.order   = order;
        this.details = details;
        this.address = address;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public void setAddress(OrderAddress a) {
        this.address = a;
    }
}
