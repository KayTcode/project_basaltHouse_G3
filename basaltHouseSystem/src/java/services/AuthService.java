package services;

import dao.AuthDAO;
import dto.UserLoginDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.SecretKey;
import model.Account;
import utils.ConfigLoader;
import utils.PasswordUtils;

public class AuthService {

    private static final String JWT_SECRET_KEY = ConfigLoader.get("jwt.secret");
    private static final SecretKey SECRET_KEY
            = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private static final long JWT_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    public static final String JWT_COOKIE_NAME = "jwt_token";
    public static final String JWT_SESSION_KEY = "jwtToken";
    public static final String USER_SESSION_KEY = "currentUser";

    private final AuthDAO authDAO;
    private final EmailService emailService = new EmailService();
    private final RegisterService registerService = new RegisterService();
    private static final String PURPOSE = "FORGOT_PASSWORD";

    public AuthService() {
        this.authDAO = new AuthDAO();
    }

    public UserLoginDTO login(String email, String password) {

        Account account = authDAO.findByEmail(email);

        if (account == null) {
            return UserLoginDTO.failure("Tài khoản không tồn tại");
        }

        if (!account.isIsActive() || account.isIsLocked()) {
            return UserLoginDTO.failure("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }
        String hashInputPassword = PasswordUtils.hashSHA256(password);
        if (hashInputPassword.equalsIgnoreCase(account.getPasswordHash())) {
            authDAO.resetFailedAttempts(account.getAccountId());

            String roleName = authDAO.getRoleNameById(account.getRoleId());

            Map<String, String> profileInfo
                    = authDAO.getFullNameAndAvatarByAccount(account);

            String fullName = profileInfo.get("fullName");
            String avatarUrl = profileInfo.get("avatarUrl");

            String jwtToken = generateJwtToken(
                    account.getAccountId(),
                    account.getEmail(),
                    roleName,
                    fullName
            );

            return UserLoginDTO.success(
                    account.getAccountId(),
                    account.getEmail(),
                    account.getRoleId(),
                    roleName,
                    fullName,
                    avatarUrl,
                    jwtToken
            );

        } else {

            authDAO.incrementFailedAttempts(account.getAccountId());

            int newFailedAttempts = account.getFailedAttempts() + 1;

            if (newFailedAttempts >= 5) {
                authDAO.lockAccount(account.getAccountId());
                return UserLoginDTO.failure(
                        "Bạn đã nhập sai quá 5 lần. Tài khoản đã bị khóa."
                );
            }

            int attemptsLeft = 5 - newFailedAttempts;

            return UserLoginDTO.failure(
                    "Sai mật khẩu. Bạn còn " + attemptsLeft + " lần thử lại."
            );
        }
    }

    private String generateJwtToken(int accountId, String email, String roleName, String fullName) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject(email)
                .claim("accountId", accountId)
                .claim("roleName", roleName)
                .claim("fullName", fullName)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims parseJwtToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            System.err.println("JWT không hợp lệ: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> sendForgotPasswordOtp(String email) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        try {
            Map<String, Object> account = authDAO.findActiveAccountByEmail(email);
            if (account == null) {
                result.put("error", "Email này chưa được đăng kí trong hệ thống");
                return result;
            }
            int accountId = (int) account.get("accountId");
            String otpCode = generateOtp();
            LocalDateTime exp = LocalDateTime.now().plusMinutes(5);
            authDAO.saveEmailOtp(accountId, otpCode, PURPOSE, exp);
            try {
                emailService.sendOtp(email, otpCode);
            } catch (Exception mailEx) {
                System.err.println("[AuthService] Mail error: " + mailEx.getMessage());
                mailEx.printStackTrace();
                result.put("error", "Không thể gửi email. Vui lòng thử lại.");
                return result;
            }
            result.put("success", true);
            result.put("accountId", accountId);
        } catch (Exception e) {
            result.put("error", "Lỗi hệ thống vui lòng thử lại sau");
        }
        return result;
    }

    public Map<String, Object> verifyOtp(int accountId, String inputOtp) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        try {
            Map<String, Object> otp = authDAO.getLastOtp(accountId, PURPOSE);
            if (otp == null) {
                result.put("errorType", "NOT_FOUND");
                result.put("error", "Không tìm thấy mã OTP. Vui lòng thử lại.");
                return result;
            }
            String storedCode = (String) otp.get("otpCode");
            LocalDateTime expiredAt = (LocalDateTime) otp.get("expiredAt");
            int otpId = (int) otp.get("otpId");
            if (!storedCode.equals((inputOtp.trim()))) {
                result.put("errorType", "WRONG_OTP");
                result.put("error", "Mã OTP không chính xác. Vui lòng thử lại");
                return result;
            }
            if (LocalDateTime.now().isAfter(expiredAt)) {
                result.put("errorType", "EXPIRED_OTP");
                result.put("error", "Mã OTP đã hết hạn. Vui lòng bấn \"Gửi lại mã\".");
                return result;
            }
            authDAO.markOtpUsed(otpId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("errorType", "DB_ERROR");
            result.put("error", "Lỗi hệ thống. Vui lòng thử lại.");
        }
        return result;
    }

    public Map<String, Object> resetPassword(int accountId, String newPassword) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        try {
            String passwordHash = PasswordUtils.hashSHA256(newPassword);
            authDAO.updatePassword(accountId, passwordHash);
            result.put("success", true);
        } catch (Exception e) {
            result.put("error", "Lỗi hệ thống. Vui lòng thử lại");
        }
        return result;
    }

//    public static void main(String[] args) {
//        AuthService ser = new AuthService();
//        int accountId = 4;
//        String input = "thanhedo123";
//        Map<String, Object> result = ser.resetPassword(accountId, input);
//        System.out.println(result);
//    }
    public Map<String, Object> resendOtp(String email) {
        return sendForgotPasswordOtp(email);
    }

    public Map<String, Object> loginOrRegisterWithGoogle(String email, String fullName, String avatarUrl) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        try {
            Account account = authDAO.findByEmail(email);
            if (account == null) {
                Map<String, Object> createResult = authDAO.createGoogleAccount(email, fullName, avatarUrl);
                Boolean created = (Boolean) createResult.get("success");
                if (created == null || !created) {
                    result.put("error", createResult.getOrDefault("error", "Không thể tạo tài khoản. Vui lòng thử lại!"));
                    return result;
                }
                int newAccountId = (int) createResult.get("accountId");
                account = authDAO.findById(newAccountId);
                if (account == null) {
                    result.put("error", "Lỗi hệ thống sau khi tạo tài khoản.");
                    return result;
                }
            }
            if (!account.isIsActive()) {
                result.put("error", "Tài khoản đã bị khoá. Vui lòng liên hệ hỗ trợ.");
                return result;
            }
            String roleName = authDAO.getRoleNameById(account.getRoleId());
            Map<String, String> profileInfo = authDAO.getFullNameAndAvatarByAccount(account);
            String dbFullName = profileInfo.get("fullName");
            String dbAvatarUrl = profileInfo.get("avatarUrl");
            String displayName = (dbFullName != null && !dbFullName.equals(account.getEmail())) ? dbFullName : fullName;
            String displayAvatar = (dbAvatarUrl != null) ? dbAvatarUrl : avatarUrl;

            String jwtToken = generateJwtToken(account.getAccountId(), account.getEmail(), roleName, displayName);

            UserLoginDTO dto = UserLoginDTO.success(account.getAccountId(), account.getEmail(), account.getRoleId(), roleName, displayName, displayAvatar, jwtToken);
            result.put("success", true);
            result.put("token", jwtToken);
            result.put("currentUser", dto);
            result.put("roleName", roleName);
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

    public static String getJwtCookieName() {
        return JWT_COOKIE_NAME;
    }

    public static String getJwtSessionKey() {
        return JWT_SESSION_KEY;
    }

    public static String getUserSessionKey() {
        return USER_SESSION_KEY;
    }
}
