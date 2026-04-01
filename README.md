# 🏥 Polyclinic Management System (Hệ Thống Phòng Khám Đa Khoa)

## 📌 Giới thiệu

Dự án xây dựng một hệ thống web giúp quản lý toàn bộ hoạt động của phòng khám đa khoa.
Hệ thống hỗ trợ bệnh nhân đặt lịch khám trực tuyến, quản lý hồ sơ bệnh án, kê đơn thuốc, thanh toán và thống kê báo cáo.

---

## 🎯 Mục tiêu

* Xây dựng hệ thống web hoàn chỉnh sử dụng **Java Spring MVC + Hibernate**
* Áp dụng mô hình **MVC (Model - View - Controller)**
* Tương tác cơ sở dữ liệu MySQL thông qua **ORM (Hibernate)**
* Hỗ trợ quản lý phòng khám theo quy trình thực tế

---

## 👥 Đối tượng sử dụng

### 👤 Bệnh nhân

* Đăng ký, đăng nhập tài khoản
* Cập nhật thông tin cá nhân
* Đặt lịch khám với bác sĩ
* Xem lịch sử khám bệnh
* Thanh toán chi phí khám chữa bệnh
* Nhận thông báo nhắc lịch

---

### 👨‍⚕️ Bác sĩ / Nhân viên y tế

* Quản lý lịch làm việc và lịch khám
* Xem danh sách bệnh nhân
* Tạo và cập nhật hồ sơ bệnh án
* Kê đơn thuốc

---

### 📊 Quản trị hệ thống

* Quản lý người dùng
* Xem báo cáo thống kê:

  * Số lượng bệnh nhân
  * Dịch vụ y tế
  * Doanh thu
  * Tình hình bệnh lý

---

## ⚙️ Công nghệ sử dụng

* **Backend:** Java Spring MVC
* **ORM:** Hibernate
* **Frontend:** JSP / Thymeleaf
* **Database:** MySQL
* **Server:** Apache Tomcat
* **Build tool:** Maven

---

## 🧱 Kiến trúc hệ thống

```
Controller → Service → Repository (DAO) → Database
```

* **Controller:** Xử lý request từ người dùng
* **Service:** Xử lý logic nghiệp vụ
* **Repository:** Tương tác database (Hibernate)
* **View:** Giao diện người dùng

---

## 👨‍💻 Nhóm thực hiện

* Thái Hùng
* Văn Long
