/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.util.Properties;
import utils.ConfigLoader;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author KayT
 */
public class EmailService {

private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USERNAME = ConfigLoader.get("email.username");
    private static final String SMTP_PASSWORD = ConfigLoader.get("email.password");
    private static final String FROM_NAME = "BasaltHouse Cafe";

    public void sendOtp(String toEmail, String otpCode) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", 587 );
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
            }
        });
        session.setDebug(false);
        String htmlContent = buildOtpEmailHtml(otpCode);

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SMTP_USERNAME, FROM_NAME, StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("=?UTF-8?B?" + Base64.getEncoder().encodeToString("Mã xác thực OTP - BasaltHouse".getBytes(StandardCharsets.UTF_8)) + "?=");
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildOtpEmailHtml(String otpCode) {
        return "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
                + "<body style='margin:0;padding:0;font-family:Georgia,serif;background:#f5f0eb;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f5f0eb;padding:40px 0;'>"
                + "  <tr><td align='center'>"
                + "    <table width='520' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:12px;"
                + "           box-shadow:0 4px 24px rgba(80,50,20,0.10);overflow:hidden;'>"
                // Header
                + "      <tr><td style='background:#3b1f0a;padding:36px 40px 28px;text-align:center;'>"
                + "        <div style='font-size:28px;font-weight:bold;color:#e8c99a;letter-spacing:3px;'>☕ COFFEELY</div>"
                + "        <div style='font-size:12px;color:#a07850;letter-spacing:6px;margin-top:4px;'>BASALTHOUSE</div>"
                + "      </td></tr>"
                // Body
                + "      <tr><td style='padding:40px 44px 32px;'>"
                + "        <p style='font-size:17px;color:#3b1f0a;margin:0 0 12px;'>Xin chào,</p>"
                + "        <p style='font-size:15px;color:#5c4033;line-height:1.7;margin:0 0 28px;'>"
                + "          Cảm ơn bạn đã đăng ký tài khoản tại <strong>Coffeely</strong>. "
                + "          Đây là mã xác thực OTP của bạn:</p>"
                // OTP Box
                + "        <div style='background:#fdf6ee;border:2px dashed #c8966a;border-radius:10px;"
                + "                    text-align:center;padding:28px 20px;margin:0 0 28px;'>"
                + "          <div style='font-size:42px;font-weight:bold;color:#3b1f0a;letter-spacing:14px;"
                + "                      font-family:\"Courier New\",monospace;'>" + otpCode + "</div>"
                + "          <div style='font-size:13px;color:#a07850;margin-top:10px;'>Mã có hiệu lực trong <strong>5 phút</strong></div>"
                + "        </div>"
                + "        <p style='font-size:13px;color:#8d6e63;line-height:1.6;margin:0;'>"
                + "          ⚠️ Vui lòng không chia sẻ mã này với bất kỳ ai. "
                + "          Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.</p>"
                + "      </td></tr>"
                // Footer
                + "      <tr><td style='background:#f9f3ec;padding:20px 44px;border-top:1px solid #e8d5c0;'>"
                + "        <p style='font-size:12px;color:#a07850;margin:0;text-align:center;'>"
                + "          © 2024 Coffeely - BasaltHouse. Trân trọng.</p>"
                + "      </td></tr>"
                + "    </table>"
                + "  </td></tr>"
                + "</table>"
                + "</body></html>";
    }

}
