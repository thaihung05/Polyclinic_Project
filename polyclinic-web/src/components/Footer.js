const Footer = () => {
    return (
        <footer className="site-footer">
            <div className="container">
                <div className="row g-4">
                    <div className="col-xl-4 col-lg-4 col-md-6">
                        <h6><i className="bi bi-hospital me-1"></i>Phòng Khám TH & VL</h6>
                        <p className="mb-3">
                            Chuyên cung cấp dịch vụ khám chữa bệnh chất lượng cao với đội ngũ bác sĩ
                            giàu kinh nghiệm, trang thiết bị hiện đại, mang lại sức khỏe tốt nhất.
                        </p>
                    </div>

                    <div className="col-xl-3 col-lg-3 col-md-6">
                        <h6>Thông Tin Liên Hệ</h6>
                        <div className="contact-row">
                            <i className="bi bi-geo-alt-fill mt-1"></i>
                            <span>123 Đường ABC, Phường XYZ, Quận 10, TP.HCM</span>
                        </div>
                        <div className="contact-row">
                            <i className="bi bi-telephone-fill mt-1"></i>
                            <a href="tel:+84388980678">(+84) ...</a>
                        </div>
                        <div className="contact-row">
                            <i className="bi bi-envelope-fill mt-1"></i>
                            <a href="mailto:thaihung.me05@gmail.com">thaihung.me05@gmail.com</a>
                        </div>
                    </div>
                </div>

                <div className="footer-divider">
                    &copy; 2026 Phòng Khám TH & VL | Thiết kế bởi Thái Hùng & Văn Long
                </div>
            </div>
        </footer>
    );
};

export default Footer;