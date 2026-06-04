/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.AuthDAO;
import dto.UserLoginDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import model.Account;
import utils.ConfigLoader;

/**
 *
 * @author KayT
 */
public class AuthService {

    private static final String JWT_SECRET_KEY = ConfigLoader.get("jwt.secret");
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private static final long JWT_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; //7 ngày
    public static final String JWT_COOKIE_NAME = "jwt_token";
    public static final String JWT_SESSION_KEY = "jwtToken";
    public static final String USER_SESSION_KEY = "currentUser";

//    public static void main(String[] args) {
//        System.out.println(JWT_SECRET_KEY);
//    }

    private final AuthDAO authDAO;

    public AuthService() {
        this.authDAO = new AuthDAO();
    }

    public UserLoginDTO login(String email, String password) {
        Account account = authDAO.findByEmail(email);
        if (account == null) {
            return UserLoginDTO.failure("Tài khoản không tồn tại");
        }
        if (!account.isIsActive()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lockoutEnd = account.getLockoutEnd();

            if (lockoutEnd != null && now.isAfter(lockoutEnd)) {
                authDAO.unlockAccount(account.getAccountId());
                account.setIsActive(true);
                account.setFailedAttempts(0);
                account.setLockoutEnd(null);
            } else {
                long minuteRemaining = 60; //fallback
                if (lockoutEnd != null) {
                    minuteRemaining = ChronoUnit.MINUTES.between(now, lockoutEnd);
                    if (minuteRemaining < 1) {
                        minuteRemaining = 1;
                    }
                }
                return UserLoginDTO.failure("Tài khoản đã bị khoá tạm thời. Vui lòng thử lại sau " + minuteRemaining + "phút");
            }
        }
        String hashInputPassword = hashSHA256(password);
        if (hashInputPassword.equalsIgnoreCase(account.getPasswordHash())) {
            authDAO.resetFailedAttempts(account.getAccountId());
            String roleName = authDAO.getRoleNameById(account.getRoleId());
            Map<String, String> profileInfo = authDAO.getFullNameAndAvatarByAccount(account);
            String fullName = profileInfo.get("fullName");
            String avatarUrl = profileInfo.get("avatarUrl");

            String jwtToken = generateJwtToken(account.getAccountId(), account.getEmail(), roleName, fullName);
            return UserLoginDTO.success(account.getAccountId(), account.getEmail(), account.getRoleId(), roleName, fullName, avatarUrl, jwtToken);
        } else {
            authDAO.incrementFailedAttempts(account.getAccountId());
            int newFailedAttemps = account.getFailedAttempts() + 1;

            if (newFailedAttemps >= 5) {
                LocalDateTime lockoutEnd = LocalDateTime.now().plusHours(1);
                authDAO.lockAccount(account.getAccountId(), lockoutEnd);
                return UserLoginDTO.failure("Bạn đã nhập sai quá 5 lần. Tài khoản bị khoá trong 1 giờ.");
            } else {
                int attemptsLeft = 5 - newFailedAttemps;
                return UserLoginDTO.failure("Sai mật khẩu. Bạn còn " + attemptsLeft + " lần thử lại.");
            }
        }
    }

    private String hashSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b & 0xff));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm không tìm thấy - lỗi JVM nghiêm trọng.", e);
        }
    }

    private String generateJwtToken(int accountId, String email, String roleName, String fullName) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject((email))
                .claim("accountId", accountId)
                .claim("roleName", roleName)
                .claim("fullName", fullName)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(SECRET_KEY)
                .compact();
    }

    private Claims parseJwtToken(String token) {
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
