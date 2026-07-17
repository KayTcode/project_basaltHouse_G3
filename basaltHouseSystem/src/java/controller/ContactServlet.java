/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.regex.Pattern;

/**
 *
 * @author admin
 */
public class ContactServlet extends HttpServlet {

    private static final String CONTACT_SUCCESS_FLASH = "contactSuccess";
    private static final int MAX_FULL_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_PHONE_LENGTH = 20;
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN
            = Pattern.compile("^[0-9+().\\-\\s]*$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
   
        request.getRequestDispatcher("views/Contact/Contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String fullName = normalize(request.getParameter("fullName"));
        String email = normalize(request.getParameter("email"));
        String phone = normalize(request.getParameter("phone"));
        String message = normalize(request.getParameter("message"));

        String validationError = validateContact(fullName, email, phone, message);
        if (validationError != null) {
            request.setAttribute("contactError", validationError);
            request.setAttribute("contactFullName", fullName);
            request.setAttribute("contactEmail", email);
            request.setAttribute("contactPhone", phone);
            request.setAttribute("contactMessage", message);
            request.getRequestDispatcher("views/Contact/Contact.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute(CONTACT_SUCCESS_FLASH,
                "Cảm ơn bạn đã gửi thông tin liên hệ. BasaltHouse sẽ phản hồi sớm nhất có thể.");
        response.sendRedirect(request.getContextPath() + "/contact");
    }

    private String validateContact(String fullName, String email, String phone, String message) {
        if (fullName.isEmpty()) {
            return "Vui lòng nhập họ và tên.";
        }
        if (fullName.length() > MAX_FULL_NAME_LENGTH) {
            return "Họ và tên không được vượt quá 100 ký tự.";
        }
        if (email.isEmpty()) {
            return "Vui lòng nhập email.";
        }
        if (email.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Email không đúng định dạng.";
        }
        if (phone.length() > MAX_PHONE_LENGTH || !PHONE_PATTERN.matcher(phone).matches()) {
            return "Số điện thoại không đúng định dạng.";
        }
        if (message.isEmpty()) {
            return "Vui lòng nhập nội dung liên hệ.";
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return "Nội dung liên hệ không được vượt quá 2000 ký tự.";
        }
        return null;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

}
