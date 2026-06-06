/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.RegisterDAO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author KayT
 */
public class RegisterService {

    private final RegisterDAO registerDao = new RegisterDAO();
    private final EmailService emailService = new EmailService();

    public Map<String, Object> processRegister(String email, String password, String fullName, String phone) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (registerDao.isEmailExitsed(email)) {
                result.put("success", false);
                result.put("error", "Email này đã được sử dụng. Vui lòng email khác hoặc đăng nhập.");
                return result;
            }
            String passwordHash = registerDao.hashSHA256(password);
            String otpCode = generateOtp();

            LocalDateTime OtpExpiredAt = LocalDateTime.now();

            int pendingId = registerDao.savePendingRegistration(email, passwordHash, fullName, phone, otpCode, OtpExpiredAt);
            if (pendingId == -1) {
                result.put("success", false);
                result.put("error", "Có lỗi xảy ra khi lưu thông tin đăng kí. Vui lòng thử lại.");
                return result;
            }
            emailService.sendOtp(email, otpCode);
            result.put("success", true);
            result.put("pendingId", pendingId);
            result.put("email", email);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Không thể gửi email xác thực.Vui lòng kiểm tra địa chỉ email và thử lại.");
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> verifyOtp(String email, String inputOtp) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> pending = registerDao.getPendingByEmail(email);
            if (pending == null) {
                result.put("success", false);
                result.put("errorType", "NOT_FOUND");
                result.put("error", "Không tìm thấy yêu cầu đăng kí. Vui lòng đăng kí lại");
            }
            String storedOtp = (String) pending.get("otpCode");
            LocalDateTime expiredAt = (LocalDateTime) pending.get("otpExpiredAt");
            int pendingId = (int) pending.get("pendingId");

            if (!storedOtp.equals(inputOtp.trim())) {
                registerDao.increaseAttemptCount(pendingId);
                result.put("success", false);
                result.put("errorType", "WRONG_OTP");
                result.put("error", "Mã OTP không chính xác. Vui lòng nhập lại.");
                return result;
            }
            if (LocalDateTime.now().isAfter(expiredAt)) {
                result.put("success", false);
                result.put("errorType", "EXPIRED_OTP");
                result.put("error", "Mã OTP đã hết hạn vui lòng bấm \"Gửi lại mã\" để nhận mã mới.");
                return result;
            }
            registerDao.completeRegistration(pending);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> resendOtp(String email) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> pending = registerDao.getPendingByEmail(email);
            if (pending == null) {
                result.put("success", false);
                result.put("error", "Không tìm thấy thông tin đăng kí. Vui lòng đăng kí lại.");
                return result;
            }
            String passwordHash = (String) pending.get("passwordHash");
            String fullName = (String) pending.get("fullName");
            String phone = (String) pending.get("phone");
            String newOtp = generateOtp();
            LocalDateTime newExpiredAt = LocalDateTime.now().plusMinutes(5);
            int newPendingId = registerDao.savePendingRegistration(email, passwordHash, fullName, phone, newOtp, newExpiredAt);
            if (newPendingId == -1) {
                result.put("success", false);
                result.put("error", "Có lỗi xảy ra. Vui lòng thử lại.");
                return result;
            }
            emailService.sendOtp(email, newOtp);
            result.put("success", true);
            result.put("pendingId", newPendingId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private String generateOtp() {
        Random random = new Random();
        int otpNumber = random.nextInt(900000) + 100000;
        return String.valueOf(otpNumber);
    }
}
