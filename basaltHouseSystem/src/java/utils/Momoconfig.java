/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Formatter;

/**
 *
 * @author KayT
 */
public class Momoconfig {

    public static final String MOMO_API_URL = "https://test-payment.momo.vn/v2/gateway/api/create";

    public static final String PARTNER_CODE = "MOMO";
    public static final String ACCESS_KEY = "F8BBA842ECF85";
    public static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    public static final String REDIRECT_URL
            = "http://localhost:8080/basaltHouseSystem/momo/return";
    public static final String IPN_URL
            = "http://localhost:8080/basaltHouseSystem/momo/ipn";
    public static final String REQUEST_TYPE = "payWithMethod";
    public static final String LANG = "vi";
    public static final int ORDER_EXPIRE_MINUTES = 15;

    public static String hmacSHA256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(rawHmac);
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("[MomoConfig] Không thể tạo chữ ký HMAC-SHA256: " + e.getMessage(), e);
        }
    }
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
