<%-- 
    Document   : Footer
    Created on : Jun 8, 2026, 10:10:59 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
        <!-- Footer -->
        <footer class="footer-coffeely">
            <div class="container">
                <div class="row g-4">
                    <div class="col-md-6 col-lg-4">
                        <a href="#" class="footer-brand">Coffeely</a>
                        <p class="footer-desc">
                            Mang hương vị cà phê nguyên bản đến tận tay bạn mỗi ngày. Trải nghiệm sự tinh tế trong từng giọt cà phê.
                        </p>
                        <div class="footer-social-wrapper">
                            <a href="#" class="footer-social-btn" title="Website">
                                <span class="material-symbols-outlined">public</span>
                            </a>
                            <a href="#" class="footer-social-btn" title="Email">
                                <span class="material-symbols-outlined">alternate_email</span>
                            </a>
                        </div>
                    </div>

                    <div class="col-6 col-md-3 col-lg-2 offset-lg-1">
                        <h4 class="footer-title">Về chúng tôi</h4>
                        <ul class="footer-link-list">
                            <li><a href="#" class="footer-link">Câu chuyện thương hiệu</a></li>
                            <li><a href="#" class="footer-link">Hợp tác kinh doanh</a></li>
                            <li><a href="#" class="footer-link">Tin tức & Sự kiện</a></li>
                            <li><a href="#" class="footer-link">Tuyển dụng</a></li>
                        </ul>
                    </div>

                    <div class="col-6 col-md-3 col-lg-2">
                        <h4 class="footer-title">Chính sách</h4>
                        <ul class="footer-link-list">
                            <li><a href="#" class="footer-link">Chính sách bảo mật</a></li>
                            <li><a href="#" class="footer-link">Điều khoản dịch vụ</a></li>
                            <li><a href="#" class="footer-link">Thông tin vận chuyển</a></li>
                            <li><a href="#" class="footer-link">Chính sách đổi trả</a></li>
                        </ul>
                    </div>

                    <div class="col-md-6 col-lg-3">
                        <h4 class="footer-title">Liên hệ</h4>
                        <ul class="footer-link-list">
                            <li class="footer-contact-item">
                                <span class="material-symbols-outlined">location_on</span>
                                123 Đường Cà Phê, Quận 1, TP. HCM
                            </li>
                            <li class="footer-contact-item">
                                <span class="material-symbols-outlined">call</span>
                                1900 1234
                            </li>
                            <li class="footer-contact-item">
                                <span class="material-symbols-outlined">mail</span>
                                hello@coffeely.vn
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="footer-bottom">
                    <p>© 2024 Coffeely. All rights reserved. Crafted for coffee lovers.</p>
                </div>
            </div>
        </footer>

        <!-- Bootstrap 5 JS Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <script>
                                            // Simple JS demo handling for the design
                                            function requireLogin() {
                                                alert('Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng!');
                                                window.location.href = "${pageContext.request.contextPath}/login";
                                            }

                                            function addToCart(itemName, price) {
                                                alert('Đã thêm "' + itemName + '" vào giỏ hàng! Giá: ' + price.toLocaleString('vi-VN') + 'đ');
                                            }

        </script>
    </body>

</html>
