<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<header class="top-header">
    <div class="logo">Basalt <span>House</span></div>
    <div class="header-buttons">
        <div class="server-time">
            Thời gian máy chủ: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy - HH:mm" />
        </div>
    </div>
</header>