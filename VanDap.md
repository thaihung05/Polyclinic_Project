# Chiến lược vấn đáp – Đề tài 6: Phòng Khám Đa Khoa Trực Tuyến

## Context
Dự án đã hoàn thiện toàn bộ code. Mục tiêu là chuẩn bị cho buổi vấn đáp trình bày được: **cách làm, kỹ thuật sử dụng, và lý do chọn** từng giải pháp. Tài liệu này đi theo từng chức năng trong đề, giải thích kỹ thuật đằng sau, và dự đoán câu hỏi thường gặp.

> **Lưu ý:** Một số chức năng đang được bổ sung/sửa (xem mục Checklist). Chi tiết kỹ thuật sửa đổi xem tại `DonThuoc.md` trong thư mục gốc project.

---

## TỔNG QUAN KIẾN TRÚC HỆ THỐNG

### Stack kỹ thuật
| Tầng | Công nghệ | Lý do chọn |
|------|-----------|------------|
| Backend | Java 17 + Spring Boot (Spring MVC/Security/ORM) | Framework enterprise chuẩn, hỗ trợ đầy đủ REST API, bảo mật, ORM |
| Frontend | ReactJS 19 + React Router DOM 6 | SPA giúp UI mượt mà không reload trang, component hóa dễ maintain |
| Database | MySQL 8 | RDBMS phổ biến, hỗ trợ quan hệ phức tạp giữa bệnh nhân-bác sĩ-đơn thuốc |
| ORM | Hibernate 6 | Tự động mapping Java object ↔ DB table, tránh viết SQL thủ công |
| Auth | JWT (nimbus-jose-jwt) | Stateless, phù hợp REST API, không cần lưu session server-side |
| Image | Cloudinary CDN | Không tốn dung lượng server, URL ảnh trả về ngay, global CDN |
| Email | JavaMail SMTP | Standard Java email API, dễ cấu hình với Gmail/SMTP bất kỳ |
| Video | Google Calendar API | Tự động tạo Google Meet link khi xác nhận lịch hẹn |
| Payment | VietQR + MOMO | Cổng thanh toán phổ biến tại Việt Nam, hỗ trợ QR code |

### Kiến trúc phân tầng (Layered Architecture)
```
Client (ReactJS)
     ↕ HTTP/REST API (JSON)
Controller Layer  →  nhận request, validate, trả response
     ↕
Service Layer     →  business logic, gọi nhiều repo, xử lý nghiệp vụ
     ↕
Repository Layer  →  truy vấn database (Hibernate HQL/Criteria)
     ↕
Database (MySQL)
```
**Câu hỏi thường gặp:** *"Tại sao cần tách Service riêng không gộp vào Controller?"*
→ Controller chỉ nên xử lý HTTP (parse request, trả response). Business logic phức tạp (kiểm tra lịch trống, tạo meeting, gửi email) phải ở Service để dễ test, dễ tái sử dụng, dễ thay đổi.

---

## 1. ĐĂNG KÝ TÀI KHOẢN & QUẢN LÝ HỒ SƠ CÁ NHÂN

### Cách làm
- **Backend:** `ApiUserController.POST /api/register` nhận `multipart/form-data` (JSON fields + file avatar)
- Avatar upload qua **Cloudinary SDK** → lưu URL vào `Users.avatar`
- Password mã hóa bằng **BCryptPasswordEncoder** trước khi lưu DB
- Role mặc định là `ROLE_PATIENT` khi tự đăng ký
- **Frontend:** `Register.js` dùng `FormData` để gửi file + thông tin, validation phía client (regex phone VN, email, username)

### Kỹ thuật nổi bật
- **BCrypt:** thuật toán one-way hash với salt ngẫu nhiên → cùng password có hash khác nhau mỗi lần → không thể reverse
- **Cloudinary:** thay vì lưu file vào server (tốn ổ cứng, phức tạp deploy), dùng CDN trả về URL ổn định
- **FormData API:** gửi đồng thời text fields và binary file trong 1 request HTTP

### Câu hỏi thường gặp
- *"Tại sao không lưu mật khẩu plaintext?"* → BCrypt hash: nếu DB bị leak, hacker không đọc được mật khẩu
- *"Validation ở đâu?"* → Cả hai tầng: frontend (UX tốt, phản hồi ngay) + backend (bảo mật, không trust client)

---

## 2. XÁC THỰC & PHÂN QUYỀN (JWT)

### Cách làm
- `POST /login` nhận username/password → `UserService` xác thực → `JwtUtils.generateToken()` tạo JWT
- JWT chứa: username, role, expiry (24h), ký bằng **HMAC SHA-256**
- **JwtFilter** intercept mọi request đến `/api/secure/**` → validate token → set `SecurityContextHolder`
- Frontend lưu token vào **cookie** (react-cookies), mọi request authenticated đính kèm `Authorization: Bearer <token>`

### 3 Roles phân quyền
| Role | Quyền |
|------|-------|
| ROLE_PATIENT | Đặt lịch, xem hồ sơ, thanh toán, thông báo |
| ROLE_DOCTOR | Quản lý lịch hẹn, hồ sơ bệnh án, kê đơn, thuốc |
| ROLE_ADMIN | Quản lý users, chuyên khoa, thống kê |

### Câu hỏi thường gặp
- *"JWT khác Session như thế nào?"* → Session lưu trên server (stateful, khó scale); JWT lưu trên client (stateless, server không cần nhớ gì, scale horizontal dễ)
- *"JWT có thể bị đánh cắp không?"* → Có. Giảm thiểu: HTTPS, expire ngắn (24h), HttpOnly cookie
- *"ROLE_ADMIN tạo như thế nào?"* → Admin tạo qua Admin panel (`/admin`), không có đường đăng ký public

---

## 3. ĐẶT LỊCH HẸN VỚI BÁC SĨ THEO CHUYÊN KHOA

### Cách làm (luồng 6 bước trên frontend `Appointment.js`)
1. Chọn **chuyên khoa** → `GET /api/specialties`
2. Chọn **bác sĩ** → `GET /api/doctors?specialtyId=X`
3. Chọn **ngày** → React Calendar date picker
4. Chọn **khung giờ** → `GET /api/doctors/{id}/schedules` (lọc theo ngày)
5. Nhập **triệu chứng**
6. Xác nhận → `POST /api/secure/appointments`

### Backend xử lý
- `AppointmentService` kiểm tra schedule còn trống không (isActive)
- Tạo `Appointments` với status = `PENDING`
- **Google Meet link tự động tạo** qua `GoogleMeetingService` (Google Calendar API)
- Gửi **notification** cho cả bác sĩ lẫn bệnh nhân
- Gửi **email** xác nhận

### Luồng trạng thái Appointment
```
PENDING → (bác sĩ confirm) → CONFIRMED → (khám xong) → COMPLETED
        → (ai hủy)         → CANCELLED
                           → NO_SHOW (bệnh nhân không đến)
        → IN_PROGRESS (đang khám)
```

### Câu hỏi thường gặp
- *"Làm sao tránh 2 người đặt cùng khung giờ?"* → DoctorSchedule có `isActive` flag; khi đặt thành công set `isActive=false` → lock slot
- *"Google Meet link tạo như thế nào?"* → Google Calendar API tạo event với `conferenceData` → Google tự generate Meet URL → lưu vào `Appointments.meetingUrl`
- *"Tại sao cần status PENDING trước khi CONFIRMED?"* → Cho phép bác sĩ xem xét và confirm; có thể tích hợp thanh toán trước confirm

---

## 4. THANH TOÁN TRỰC TUYẾN

### Hệ thống có 2 loại thanh toán
| Loại | Khi nào | Endpoint |
|------|---------|----------|
| **Phí khám** | Khi đặt lịch hẹn | `POST /api/secure/payment/create` |
| **Tiền thuốc** | Khi bệnh nhân xem đơn thuốc | `POST /api/secure/prescriptions/{id}/payment/create` |

### Cách làm
- **2 phương thức:** BANKING (chuyển khoản ngân hàng) và MOMO
- Tạo `Payments` record với status=`PENDING`
- `QrService` generate **VietQR** URL → bệnh nhân quét QR bằng app ngân hàng
- Sau khi thanh toán: gọi confirm → update status=`COMPLETED`

### Kỹ thuật VietQR
- VietQR là chuẩn QR code VN tích hợp Napas → scan bằng app ngân hàng bất kỳ
- URL format: `img.vietqr.io/image/{bankCode}-{accountNumber}-{template}.png?amount=X&addInfo=Y`
- Thông tin embed trong QR: số tiền, mã giao dịch, nội dung chuyển khoản

### Câu hỏi thường gặp
- *"Thanh toán có tự động không?"* → Semi-manual: bệnh nhân quét QR rồi bấm xác nhận. Production cần webhook từ ngân hàng
- *"Tại sao chọn VietQR?"* → Không cần API key ngân hàng, phù hợp prototype/demo; production dùng VNPay/MoMo SDK
- *"Tiền thuốc tính từ đâu?"* → Tổng của `PrescriptionItem.quantity × PrescriptionItem.unitPrice` trong đơn thuốc

---

## 5. HỒ SƠ BỆNH ÁN ĐIỆN TỬ (EHR)

### Cách làm
- Sau khi khám, bác sĩ tạo `MedicalRecord` cho appointment
- Nội dung: chief complaint (lý do khám), diagnosis (chẩn đoán), treatment plan (phác đồ), follow-up date, notes
- `POST /api/secure/appointments/{id}/medical-record`
- Bệnh nhân xem qua: `GET /api/secure/patients/medical-history` (aggregate tất cả records)

### Liên kết dữ liệu
```
Appointment (1) → MedicalRecord (1) → Prescriptions (M) → PrescriptionItems (M) → Medicines
                                    → LabResults (M)
```

### Câu hỏi thường gặp
- *"Tại sao MedicalRecord gắn với Appointment thay vì Patient?"* → Mỗi lần khám là 1 record riêng, liên kết với cuộc hẹn cụ thể giúp trace được doctor nào khám, khi nào
- *"Bệnh nhân có được sửa hồ sơ không?"* → Không, chỉ bác sĩ mới có quyền tạo/sửa; bệnh nhân chỉ xem

---

## 6. KÊ ĐƠN THUỐC & THANH TOÁN TIỀN THUỐC

### Cách làm (2 vai trò)

**Phía bác sĩ** (`AppointmentDetail.js`):
- Bác sĩ tạo đơn thuốc với danh sách `PrescriptionItems` (tên thuốc, số lượng, liều dùng, số ngày)
- `POST /api/secure/medical-records/{id}/prescriptions`
- Hệ thống lưu đơn thuốc, **chưa trừ kho** (chờ bệnh nhân thanh toán)

**Phía bệnh nhân** (Tab "Đơn thuốc" trong `MedicalHistory.js`):
- Bệnh nhân thấy bảng: tên thuốc, số lượng, đơn giá, **tổng tiền**
- Hệ thống hiện **QR thanh toán** tiền thuốc
- Sau khi bấm xác nhận thanh toán → hệ thống:
  1. Set `Prescriptions.isPaid = true`
  2. Trừ kho thuốc (`deductStock`)
  3. Mở khóa thông tin chi tiết: liều dùng, hướng dẫn sử dụng
- Bệnh nhân đến **quầy dược của phòng khám** xuất trình đơn và lấy thuốc

### Lý do thiết kế như vậy
- Bệnh nhân biết tên thuốc + giá **trước khi thanh toán** → không "mua mèo trong bao"
- Trừ kho **sau thanh toán** → tồn kho phản ánh đúng thực tế xuất dược
- Phòng khám có **quầy dược nội bộ** → bán thuốc kèm dịch vụ khám là hợp lý

### Câu hỏi thường gặp
- *"Tại sao không trừ kho khi bác sĩ kê đơn?"* → Nếu bệnh nhân không thanh toán, kho bị trừ oan → số liệu sai thực tế
- *"Nếu bệnh nhân thanh toán rồi không đến lấy?"* → Thuốc đã được trừ kho, bệnh nhân cần liên hệ phòng khám xử lý (offline flow)
- *"unitPrice lấy từ đâu?"* → Snapshot từ `Medicines.price` tại thời điểm kê đơn → giá không thay đổi dù thuốc update giá sau

---

## 7. KẾT QUẢ XÉT NGHIỆM (LAB RESULTS)

### Cách làm
- Bác sĩ thêm kết quả: `POST /api/secure/appointments/{id}/lab-results`
- Fields: testName, testCode, result, unit, referenceRange, isAbnormal (boolean flag)
- `isAbnormal=true` → UI highlight màu đỏ để cảnh báo

### Câu hỏi thường gặp
- *"isAbnormal được set tự động không?"* → Hiện tại bác sĩ set thủ công; có thể tự động nếu biết referenceRange (tương lai)

---

## 8. QUẢN LÝ LỊCH LÀM VIỆC BÁC SĨ

### Cách làm
- Bác sĩ thêm khung giờ qua `ScheduleManager.js`
- `DoctorSchedules` lưu: startTime, endTime, isActive
- `GET /api/doctors/{id}/schedules` → frontend hiển thị các slot còn trống (isActive=true)
- Khi slot bị đặt → isActive=false (tự động trong AppointmentService)

### Câu hỏi thường gặp
- *"Bác sĩ có thể block ngày nghỉ không?"* → Xóa schedule slot tương ứng hoặc set isActive=false

---

## 9. QUẢN LÝ DƯỢC PHẨM & KHO THUỐC

### Cách làm
- CRUD thuốc qua `MedicineManager.js` (chỉ Doctor/Admin)
- `Medicines` entity: code, name, genericName, category, unit, concentration, manufacturer, **stockQuantity**, **expiryDate**, price
- **Cảnh báo tự động:**
  - Sắp hết hàng: `stockQuantity <= threshold` (configurable)
  - Sắp hết hạn: `expiryDate` trong vòng N ngày tới
- Frontend hiển thị badges màu đỏ/vàng

### Logic trừ kho (đúng nghiệp vụ)
- Khi **bác sĩ kê đơn** → **không trừ kho**
- Khi **bệnh nhân xác nhận thanh toán tiền thuốc** → mới trừ kho
- `deductStock` kiểm tra tồn kho trước: nếu `stockQuantity < quantity` → throw exception, bác sĩ được thông báo

### Câu hỏi thường gặp
- *"Cảnh báo hết hạn tính như thế nào?"* → `expiryDate BETWEEN now AND now+N days` query trong Hibernate
- *"Tại sao trừ kho sau thanh toán không phải khi kê đơn?"* → Kê đơn chỉ là "dự kiến dùng thuốc", kho thực tế chỉ giảm khi thuốc thực sự được xuất — tức khi bệnh nhân thanh toán và đến lấy
- *"Nhập kho như thế nào?"* → Update `stockQuantity` qua PUT medicine endpoint

---

## 10. THỐNG KÊ VÀ BÁO CÁO (ADMIN)

### Cách làm
- Backend: `StatsService/StatsRepository` + `AdminStatsController`
- Admin dashboard dùng Thymeleaf MVC (server-side rendering)
- Các báo cáo:
  - Bệnh nhân theo độ tuổi, giới tính, chuyên khoa
  - Dịch vụ y tế được sử dụng (số lượt khám)
  - Bệnh phổ biến (từ MedicalRecord.diagnosis)
  - Doanh thu: tổng hợp từ `Payments` với status=COMPLETED

### Câu hỏi thường gặp
- *"Tại sao admin dùng Thymeleaf còn API dùng REST?"* → Admin panel là internal tool, Thymeleaf render nhanh không cần SPA; REST API phục vụ React frontend/mobile
- *"Báo cáo doanh thu lấy từ đâu?"* → `SELECT SUM(amount) FROM payments WHERE status='COMPLETED'` — gộp cả phí khám lẫn tiền thuốc

---

## 11. THÔNG BÁO (NOTIFICATIONS)

### Cách làm
- `NotificationService.createNotification(userId, title, message)` được gọi tại:
  - Đặt lịch → thông báo cho bác sĩ
  - Bác sĩ confirm → thông báo cho bệnh nhân
  - Hủy lịch → thông báo cho bên kia
  - Kê đơn xong → thông báo bệnh nhân "Có đơn thuốc mới, vui lòng thanh toán để xem chi tiết"
- **Email:** `EmailService` gửi song song với notification in-app

### Câu hỏi thường gặp
- *"Thông báo có real-time không?"* → Không, polling khi load trang. Real-time cần WebSocket/SSE (ngoài scope)
- *"Tại sao cần cả notification lẫn email?"* → Notification: trong app ngay; Email: dự phòng khi user offline

---

## 12. TƯ VẤN TRỰC TUYẾN (VIDEO CALL)

### Cách làm
- Khi bác sĩ confirm appointment → `GoogleMeetingService.createMeeting()` được gọi
- Google Calendar API tạo event với `conferenceData: {createRequest: {requestId: uuid}}`
- Google tự generate Meet URL → lưu vào `Appointments.meetingUrl`
- Bệnh nhân và bác sĩ nhấn link trong appointment detail để join

### Câu hỏi thường gặp
- *"Video call có embed trong app không?"* → Không, mở Google Meet trên browser mới. Embed cần Google Meet SDK (ngoài scope)
- *"Tại sao dùng Google Meet thay vì WebRTC tự build?"* → Tiết kiệm thời gian, Google Meet có sẵn recording, chat, ổn định; WebRTC tự build tốn rất nhiều tài nguyên

---

## CÂU HỎI KỸ THUẬT TỔNG QUÁT

### Về kiến trúc
| Câu hỏi | Trả lời ngắn |
|---------|-------------|
| Tại sao dùng REST API thay vì GraphQL? | REST đơn giản hơn, phù hợp scope, tooling phong phú |
| Tại sao dùng MySQL thay vì NoSQL? | Dữ liệu có quan hệ phức tạp, ACID transactions quan trọng cho thanh toán |
| CORS là gì? Tại sao cần? | Cho phép frontend ở domain khác (localhost:3000) gọi API backend (localhost:8080) |
| Hibernate lazy vs eager loading? | Lazy: fetch khi cần (tránh N+1); Eager: fetch ngay khi luôn cần data |

### Về bảo mật
| Câu hỏi | Trả lời ngắn |
|---------|-------------|
| SQL Injection phòng tránh? | Hibernate parameterized HQL, không ghép string SQL thủ công |
| XSS phòng tránh? | React tự escape HTML trong JSX; Thymeleaf th:text tự escape |
| CSRF? | JWT Bearer token không dùng cookie cho auth → CSRF không áp dụng |

### Về database
| Câu hỏi | Trả lời ngắn |
|---------|-------------|
| Transaction dùng ở đâu? | Đặt lịch (appointment + lock slot), Thanh toán thuốc (confirm payment + deductStock phải atomic) |
| Tại sao lưu unitPrice trong PrescriptionItem? | Price snapshot: giá thuốc có thể thay đổi, đơn thuốc cũ phải giữ nguyên giá cũ |

---

## CHECKLIST CHỨC NĂNG ĐỀ YÊU CẦU vs. THỰC TẾ

| Chức năng | Trạng thái | Ghi chú |
|-----------|------------|---------|
| Đăng ký tài khoản bệnh nhân | ✅ Hoàn chỉnh | Register.js + BCrypt |
| Quản lý hồ sơ cá nhân | ✅ Hoàn chỉnh | Profile.js |
| Đặt lịch hẹn theo chuyên khoa | ✅ Hoàn chỉnh | Appointment.js 6 bước |
| Lịch sử khám & kết quả xét nghiệm | ✅ Hoàn chỉnh | MedicalHistory.js |
| Thanh toán phí khám trực tuyến | ✅ Hoàn chỉnh | VietQR + MOMO |
| Thanh toán tiền thuốc | 🔧 Đang làm | Xem DonThuoc.md |
| Xem đơn thuốc (bệnh nhân) | 🔧 Đang làm | Tab mới trong MedicalHistory.js |
| Thông báo nhắc lịch & đơn thuốc | ✅ Hoàn chỉnh | In-app + Email |
| Quản lý lịch làm việc bác sĩ | ✅ Hoàn chỉnh | ScheduleManager.js |
| Hồ sơ bệnh án điện tử | ✅ Hoàn chỉnh | MedicalRecord + Lab Results |
| Kê đơn thuốc trực tuyến | ✅ Hoàn chỉnh | Prescription + PrescriptionItems |
| Tư vấn video call | ✅ Google Meet link | Mở ngoài app |
| Quản lý danh mục thuốc/kho | ✅ Hoàn chỉnh | MedicineManager.js |
| Cảnh báo thuốc hết/hết hạn | ✅ Hoàn chỉnh | Alert badges frontend |
| Liên kết kê đơn → trừ kho | 🔧 Đang sửa | Chuyển trừ kho sang sau thanh toán |
| Báo cáo thống kê admin | ✅ Backend + Admin MVC | Thymeleaf, không có chart trên React |
| Báo cáo doanh thu | ✅ Backend StatsService | Admin Thymeleaf |

---

## GỢI Ý KHI TRÌNH BÀY

1. **Bắt đầu bằng sơ đồ kiến trúc tổng thể** (Client ↔ REST API ↔ Service ↔ DB)
2. **Mỗi chức năng:** trình bày theo luồng dữ liệu từ user action → API → service → DB → response
3. **Nhấn mạnh điểm nổi bật:** JWT stateless auth, Google Meet tự động, VietQR, thanh toán 2 lớp (phí khám + tiền thuốc), logic trừ kho đúng nghiệp vụ
4. **Chủ động giải thích quyết định thiết kế:** "Chúng tôi chọn trừ kho sau thanh toán thay vì khi kê đơn vì..."
5. **Thừa nhận hạn chế thành thật:** payment manual confirm, notifications không real-time, video không embed
6. **Đề xuất cải thiện:** webhook ngân hàng, WebSocket real-time, embed video

---

## FILES QUAN TRỌNG CẦN NẮM RÕ

### Backend
- `SpringSecurityConfigs.java` – Toàn bộ security: JWT, CORS, roles, Cloudinary
- `JwtFilter.java` + `JwtUtils.java` – JWT implementation
- `ApiAppointmentController.java` – Core feature endpoints
- `AppointmentServiceImpl.java` – Business logic đặt lịch
- `PrescriptionServiceImpl.java` – Logic kê đơn + trừ kho (đang sửa)
- `MedicineRepositoryImpl.java` – deductStock (đang sửa)
- `GoogleMeetingServiceImpl.java` – Google Meet integration
- `PaymentServiceImpl.java` + `QrServiceImpl.java` – Payment flow

### Frontend
- `src/configs/Api.js` – Base URL, authApis() helper
- `src/reducers/MyUserReducer.js` – Auth state management
- `src/screens/Appointment/Appointment.js` – Multi-step booking flow
- `src/screens/Doctor/Appointments/AppointmentDetail.js` – Toàn bộ doctor workflow
- `src/screens/Doctor/Medicines/MedicineManager.js` – Inventory + alerts
- `src/screens/Patient/MedicalHistory.js` – Lịch sử khám + đơn thuốc (đang bổ sung)
- `src/App.js` – Route structure + ProtectedRoute
