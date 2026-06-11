package services;

import dao.AuthDAO;
import dto.UserLoginDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import model.Account;
import utils.ConfigLoader;

public class AuthService {

    private static final String JWT_SECRET_KEY = ConfigLoader.get("jwt.secret");
    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private static final long JWT_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    public static final String JWT_COOKIE_NAME = "jwt_token";
    public static final String JWT_SESSION_KEY = "jwtToken";
    public static final String USER_SESSION_KEY = "currentUser";

    private final AuthDAO authDAO;

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

        String hashInputPassword = hashSHA256(password);

        if (password.equalsIgnoreCase(account.getPasswordHash())) {

            authDAO.resetFailedAttempts(account.getAccountId());

            String roleName = authDAO.getRoleNameById(account.getRoleId());

            Map<String, String> profileInfo =
                    authDAO.getFullNameAndAvatarByAccount(account);

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

    public String hashSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes =
                    digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b & 0xff));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "SHA-256 algorithm không tìm thấy - lỗi JVM nghiêm trọng.",
                    e
            );
        }
    }

    private String generateJwtToken(int accountId,
                                    String email,
                                    String roleName,
                                    String fullName) {

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