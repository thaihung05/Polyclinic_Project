# Kế hoạch sửa logic Đơn Thuốc

## Vấn đề hiện tại

### 1. Trừ kho sai thời điểm
`PrescriptionServiceImpl.save()` đang gọi `deductStock` ngay khi bác sĩ tạo đơn, trước khi bệnh nhân thanh toán.

```
Bác sĩ tạo đơn → Kho bị trừ ngay
→ Bệnh nhân chưa/không thanh toán → Kho sai thực tế
```

**File:** `Polyclinic/src/main/java/com/pkdk/service/impl/PrescriptionServiceImpl.java` dòng 42-44

### 2. Bug deductStock không báo lỗi khi thiếu hàng
```java
m.setStockQuantity(Math.max(newQuantity, 0)); // im lặng khi kho < số lượng kê
```
Kho 5 viên, kê 10 → kho thành 0, không có lỗi, đơn vẫn lưu thành công.

**File:** `Polyclinic/src/main/java/com/pkdk/repository/impl/MedicineRepositoryImpl.java` dòng 88-89

### 3. Bệnh nhân không xem được đơn thuốc
- Không có Tab "Đơn thuốc" trong `MedicalHistory.js`
- Không có endpoint nào cho bệnh nhân lấy danh sách đơn thuốc của mình

---

## Flow mới sau khi sửa

```
Bác sĩ tạo đơn → Lưu DB, KHÔNG trừ kho
  ↓
Bệnh nhân vào tab "Đơn thuốc"
  → Thấy: tên thuốc | số lượng | đơn giá | tổng tiền
  → Thấy QR thanh toán
  → Bấm "Tôi đã thanh toán" → confirm
  ↓
Hệ thống xác nhận payment → isPaid = true → trừ kho → hiện full đơn thuốc
  → Bệnh nhân thấy: liều dùng, số ngày, hướng dẫn sử dụng
  → Đến quầy dược phòng khám lấy thuốc
```

---

## Danh sách thay đổi cần làm

### Bước 1 — Database: thêm field vào bảng `Prescriptions`
```sql
ALTER TABLE prescriptions ADD COLUMN is_paid BOOLEAN DEFAULT FALSE;
ALTER TABLE prescriptions ADD COLUMN payment_id INT NULL;
ALTER TABLE prescriptions ADD CONSTRAINT fk_prescription_payment
    FOREIGN KEY (payment_id) REFERENCES payments(id);
```

**Entity cần cập nhật:** `Polyclinic/src/main/java/com/pkdk/pojo/Prescriptions.java`
- Thêm field `isPaid` (Boolean)
- Thêm field `paymentId` (FK → Payments, nullable)

---

### Bước 2 — Backend: sửa `PrescriptionServiceImpl.save()`
**File:** `Polyclinic/src/main/java/com/pkdk/service/impl/PrescriptionServiceImpl.java`

**Hiện tại:**
```java
public void save(Prescriptions prescription) {
    this.prescriptionRepo.save(prescription);
    for (PrescriptionItems item : prescription.getPrescriptionItemsCollection()) {
        this.medicineService.deductStock(item.getMedicineId().getId(), item.getQuantity());
    }
}
```

**Sửa thành:**
```java
public void save(Prescriptions prescription) {
    this.prescriptionRepo.save(prescription);
    // KHÔNG trừ kho ở đây — trừ khi bệnh nhân xác nhận thanh toán
}

public void confirmPaymentAndDeductStock(int prescriptionId, int paymentId) {
    Prescriptions p = this.prescriptionRepo.getById(prescriptionId);
    p.setIsPaid(true);
    p.setPaymentId(paymentId);
    this.prescriptionRepo.save(p);
    for (PrescriptionItems item : p.getPrescriptionItemsCollection()) {
        this.medicineService.deductStock(item.getMedicineId().getId(), item.getQuantity());
    }
}
```

---

### Bước 3 — Backend: fix `deductStock` báo lỗi khi thiếu hàng
**File:** `Polyclinic/src/main/java/com/pkdk/repository/impl/MedicineRepositoryImpl.java`

**Hiện tại:**
```java
int newQuantity = m.getStockQuantity() - quantity;
m.setStockQuantity(Math.max(newQuantity, 0)); // bug: im lặng
```

**Sửa thành:**
```java
if (m.getStockQuantity() < quantity) {
    throw new RuntimeException("Thuốc '" + m.getName() + "' không đủ số lượng trong kho. Tồn kho: " + m.getStockQuantity());
}
m.setStockQuantity(m.getStockQuantity() - quantity);
```

---

### Bước 4 — Backend: thêm 2 endpoints thanh toán đơn thuốc
**File:** `Polyclinic/src/main/java/com/pkdk/controllers/ApiPrescriptionController.java`

```
POST /api/secure/prescriptions/{prescriptionId}/payment/create
  → Tính totalAmount = sum(PrescriptionItem.quantity × PrescriptionItem.unitPrice)
  → Tạo Payment(amount, method, status=PENDING)
  → Tạo QR code
  → Trả về: { paymentId, qrUrl, totalAmount }

POST /api/secure/prescriptions/{prescriptionId}/payment/confirm
  → Cập nhật Payment.status = COMPLETED
  → Gọi prescriptionService.confirmPaymentAndDeductStock()
  → Trả về 200 OK
```

---

### Bước 5 — Backend: thêm endpoint bệnh nhân xem đơn thuốc
**File:** `Polyclinic/src/main/java/com/pkdk/controllers/ApiPrescriptionController.java`

```
GET /api/secure/patient/prescriptions
  → Lấy tất cả Prescriptions của bệnh nhân đang đăng nhập
  → Query: FROM Prescriptions p WHERE p.medicalRecordId.appointmentId.patientId.id = :patientId
  → Nếu isPaid = false: ẩn dosage, instructions trong response (chỉ trả tên thuốc + giá + tổng)
  → Nếu isPaid = true: trả full thông tin
```

**File mới cần thêm query:** `Polyclinic/src/main/java/com/pkdk/repository/impl/PrescriptionRepositoryImpl.java`

---

### Bước 6 — Frontend: thêm Tab "Đơn thuốc" trong MedicalHistory
**File:** `polyclinic-web/src/screens/Patient/MedicalHistory.js`

**Thêm state:**
```js
const [prescriptions, setPrescriptions] = useState([]);
```

**Thêm fetch trong `loadHistory()`:**
```js
authApis().get(endpoints['patient-prescriptions'])
```

**Thêm Tab thứ 3:**
```
Tab "Đơn thuốc":
  Nếu isPaid = false:
    - Bảng: Tên thuốc | Số lượng | Đơn giá | Thành tiền
    - Dòng tổng cộng
    - Hiển thị QR thanh toán (gọi create payment)
    - Nút "Xác nhận đã thanh toán"

  Nếu isPaid = true:
    - Bảng đầy đủ: Tên thuốc | Liều dùng | Số ngày | Hướng dẫn
    - Badge "Đã thanh toán" màu xanh
```

**Thêm endpoint vào `Api.js`:**
```js
'patient-prescriptions': '/secure/patient/prescriptions',
'prescription-payment-create': (id) => `/secure/prescriptions/${id}/payment/create`,
'prescription-payment-confirm': (id) => `/secure/prescriptions/${id}/payment/confirm`,
```

---

## Tóm tắt thứ tự thực hiện

```
1. Sửa DB (thêm is_paid, payment_id vào prescriptions)
2. Cập nhật Prescriptions.java entity
3. Sửa PrescriptionServiceImpl.save() → bỏ deductStock
4. Thêm confirmPaymentAndDeductStock() vào PrescriptionService/Impl
5. Fix MedicineRepositoryImpl.deductStock() → throw exception
6. Thêm 2 endpoints payment vào ApiPrescriptionController
7. Thêm endpoint GET /api/secure/patient/prescriptions
8. Thêm 3 endpoints vào Api.js (frontend)
9. Thêm Tab "Đơn thuốc" vào MedicalHistory.js
```
