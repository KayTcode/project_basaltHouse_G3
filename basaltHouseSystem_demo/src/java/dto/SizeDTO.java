package dto;
import java.math.BigDecimal;

public class SizeDTO {
    private int sizeId;
    private String sizeName;
    private BigDecimal price; // Giá tiền thay đổi theo Size (từ bảng ProductSizes)

    public SizeDTO(int sizeId, String sizeName, BigDecimal price) {
        this.sizeId = sizeId;
        this.sizeName = sizeName;
        this.price = price;
    }
    // Getter & Setter...
    public int getSizeId() { return sizeId; }
    public String getSizeName() { return sizeName; }
    public BigDecimal getPrice() { return price; }
}