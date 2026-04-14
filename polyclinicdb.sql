-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: polyclinicdb
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `payment_id` int DEFAULT NULL,
  `scheduled_at` datetime NOT NULL COMMENT 'Thời điểm hẹn khám',
  `status` enum('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') NOT NULL DEFAULT 'PENDING',
  `symptoms` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Triệu chứng bệnh nhân mô tả',
  `meeting_url` varchar(500) DEFAULT NULL COMMENT 'Link video call (Zoom, Google Meet...)',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `cancelled_by` enum('PATIENT','DOCTOR','ADMIN') DEFAULT NULL,
  `ngay_tao` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_appt_patient` (`patient_id`),
  KEY `fk_appt_doctor` (`doctor_id`),
  KEY `fk_appt_payment` (`payment_id`),
  CONSTRAINT `fk_appt_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appt_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appt_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,1,1,1,'2026-04-06 08:30:00','CONFIRMED','Sốt, ho, đau họng nhẹ','https://meet.google.com/abc-defg-hij',NULL,NULL,'2026-04-01 09:30:00'),(2,2,1,2,'2026-04-08 14:00:00','PENDING','Đau đầu, mệt mỏi, mất ngủ','https://meet.google.com/klm-nopq-rst',NULL,NULL,'2026-04-01 09:35:00'),(3,1,1,NULL,'2026-04-10 09:00:00','COMPLETED','Ho kéo dài, sốt nhẹ',NULL,NULL,NULL,'2026-04-01 09:45:00');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_schedules`
--

DROP TABLE IF EXISTS `doctor_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_schedules` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_schedule_doctor` (`doctor_id`),
  CONSTRAINT `fk_schedule_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_schedules`
--

LOCK TABLES `doctor_schedules` WRITE;
/*!40000 ALTER TABLE `doctor_schedules` DISABLE KEYS */;
INSERT INTO `doctor_schedules` VALUES (1,1,'2026-04-06 08:00:00','2026-04-06 11:30:00',1),(2,1,'2026-04-08 13:30:00','2026-04-08 17:00:00',1),(3,1,'2026-04-10 08:00:00','2026-04-10 11:30:00',1);
/*!40000 ALTER TABLE `doctor_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `specialty_id` int NOT NULL,
  `bio` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `consultation_fee` decimal(12,2) NOT NULL DEFAULT '0.00',
  `available_online` tinyint(1) NOT NULL DEFAULT '1',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT 'Điểm đánh giá trung bình',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `fk_doctors_specialty` (`specialty_id`),
  CONSTRAINT `fk_doctors_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `specialties` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_doctors_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,2,1,'Bác sĩ chuyên khoa Nội tổng quát với 10 năm kinh nghiệm',200000.00,1,4.80),(2,3,2,'Bác sĩ Nhi khoa tận tâm, giàu kinh nghiệm khám cho trẻ nhỏ',220000.00,1,4.70),(3,4,3,'Bác sĩ Da liễu chuyên điều trị mụn, viêm da, dị ứng da',210000.00,1,4.60),(4,5,4,'Bác sĩ Tim mạch chuyên theo dõi tăng huyết áp và bệnh tim',250000.00,1,4.90),(5,6,5,'Bác sĩ Tai mũi họng chuyên viêm xoang, viêm họng, ù tai',200000.00,1,4.50),(6,7,6,'Bác sĩ Sản phụ khoa chuyên chăm sóc sức khỏe phụ nữ',260000.00,1,4.85),(7,8,7,'Bác sĩ Chấn thương chỉnh hình chuyên xương khớp',240000.00,1,4.65),(8,9,8,'Bác sĩ Thần kinh chuyên đau đầu, mất ngủ, chóng mặt',230000.00,1,4.55),(9,10,9,'Bác sĩ Tiêu hóa chuyên dạ dày, đại tràng, gan mật',225000.00,1,4.75),(10,11,10,'Bác sĩ Nhãn khoa chuyên khám mắt và theo dõi tật khúc xạ',235000.00,1,4.70),(11,14,1,'Bác sĩ Nội tổng quát chuyên điều trị bệnh hô hấp và tiêu hóa',210000.00,1,4.72),(12,15,1,'Bác sĩ Nội tổng quát theo dõi bệnh mạn tính và khám sức khỏe định kỳ',215000.00,1,4.78),(13,16,2,'Bác sĩ Nhi khoa chuyên điều trị bệnh hô hấp và sốt siêu vi ở trẻ',225000.00,1,4.68),(14,17,2,'Bác sĩ Nhi khoa giàu kinh nghiệm tư vấn dinh dưỡng và tăng trưởng',230000.00,1,4.74),(15,18,3,'Bác sĩ Da liễu chuyên điều trị mụn, sẹo mụn và viêm da cơ địa',220000.00,1,4.66),(16,19,3,'Bác sĩ Da liễu tư vấn dị ứng da, nấm da và chăm sóc da chuyên sâu',225000.00,1,4.71),(17,20,4,'Bác sĩ Tim mạch chuyên tư vấn tăng huyết áp và rối loạn nhịp tim',255000.00,1,4.88),(18,21,4,'Bác sĩ Tim mạch theo dõi bệnh mạch vành, suy tim và điện tim',260000.00,1,4.91),(19,22,5,'Bác sĩ Tai mũi họng chuyên điều trị viêm mũi dị ứng và viêm xoang',205000.00,1,4.58),(20,23,5,'Bác sĩ Tai mũi họng chuyên viêm họng, ù tai và bệnh lý thanh quản',210000.00,1,4.63),(21,24,6,'Bác sĩ Sản phụ khoa chuyên khám thai định kỳ và tư vấn sức khỏe sinh sản',265000.00,1,4.86),(22,25,6,'Bác sĩ Sản phụ khoa theo dõi thai kỳ nguy cơ thấp và bệnh phụ khoa thường gặp',270000.00,1,4.89),(23,26,7,'Bác sĩ Chấn thương chỉnh hình chuyên đau cột sống, thoái hóa khớp và phục hồi vận động',245000.00,1,4.67),(24,27,7,'Bác sĩ Chấn thương chỉnh hình điều trị bệnh lý xương khớp và chấn thương thể thao',250000.00,1,4.73),(25,28,8,'Bác sĩ Thần kinh chuyên đau đầu mạn tính, chóng mặt và rối loạn giấc ngủ',235000.00,1,4.61),(26,29,8,'Bác sĩ Thần kinh theo dõi rối loạn tiền đình, mất ngủ và bệnh thần kinh ngoại biên',240000.00,1,4.69),(27,30,9,'Bác sĩ Tiêu hóa chuyên điều trị dạ dày, trào ngược và rối loạn tiêu hóa',230000.00,1,4.77),(28,31,9,'Bác sĩ Tiêu hóa theo dõi bệnh lý gan mật, đại tràng và đường ruột',235000.00,1,4.80),(29,32,10,'Bác sĩ Nhãn khoa chuyên khám khô mắt, viêm kết mạc và tật khúc xạ',240000.00,1,4.72),(30,33,10,'Bác sĩ Nhãn khoa theo dõi cận thị, loạn thị và tư vấn chăm sóc thị lực',245000.00,1,4.76);
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lab_results`
--

DROP TABLE IF EXISTS `lab_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lab_results` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `test_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Tên xét nghiệm: Công thức máu, Glucose...',
  `test_code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Mã xét nghiệm',
  `result` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `unit` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `is_abnormal` tinyint(1) DEFAULT '0',
  `performed_at` datetime DEFAULT NULL,
  `ngay_tao` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lab_appointment` (`appointment_id`),
  CONSTRAINT `fk_lab_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lab_results`
--

LOCK TABLES `lab_results` WRITE;
/*!40000 ALTER TABLE `lab_results` DISABLE KEYS */;
INSERT INTO `lab_results` VALUES (1,3,'Công thức máu','LAB001','Chỉ số bạch cầu tăng nhẹ','10^9/L',1,'2026-04-10 09:15:00','2026-04-10 09:20:00'),(2,3,'CRP','LAB002','Tăng nhẹ','mg/L',1,'2026-04-10 09:17:00','2026-04-10 09:22:00');
/*!40000 ALTER TABLE `lab_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_records`
--

DROP TABLE IF EXISTS `medical_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_records` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `chief_complaint` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Lý do khám chính',
  `diagnosis` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Chẩn đoán bệnh',
  `treatment_plan` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Phác đồ điều trị',
  `follow_up_date` datetime DEFAULT NULL COMMENT 'Ngày tái khám',
  `notes` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Ghi chú thêm của bác sĩ',
  PRIMARY KEY (`id`),
  KEY `fk_record_appointment` (`appointment_id`),
  CONSTRAINT `fk_record_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_records`
--

LOCK TABLES `medical_records` WRITE;
/*!40000 ALTER TABLE `medical_records` DISABLE KEYS */;
INSERT INTO `medical_records` VALUES (1,3,'Ho kéo dài kèm sốt nhẹ','Viêm họng cấp','Uống thuốc theo đơn, nghỉ ngơi, uống nhiều nước ấm','2026-04-17 09:00:00','Nếu sốt cao hơn hoặc khó thở thì tái khám sớm');
/*!40000 ALTER TABLE `medical_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicines`
--

DROP TABLE IF EXISTS `medicines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicines` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL COMMENT 'Mã thuốc nội bộ',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `generic_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Hoạt chất',
  `category` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Nhóm thuốc (kháng sinh, giảm đau...)',
  `unit` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Đơn vị: viên, chai, ống...',
  `concentration` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Hàm lượng: 500mg, 250mg/5ml...',
  `manufacturer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `stock_quantity` int NOT NULL DEFAULT '0',
  `expiry_date` datetime DEFAULT NULL,
  `price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicines`
--

LOCK TABLES `medicines` WRITE;
/*!40000 ALTER TABLE `medicines` DISABLE KEYS */;
INSERT INTO `medicines` VALUES (1,'MED001','Paracetamol','Acetaminophen','Giảm đau - hạ sốt','Viên','500mg','DHG Pharma',500,'2027-12-31 00:00:00',1500.00,1),(2,'MED002','Cetirizine','Cetirizine Hydrochloride','Chống dị ứng','Viên','10mg','Traphaco',300,'2027-10-31 00:00:00',2500.00,1),(3,'MED003','Amoxicillin','Amoxicillin','Kháng sinh','Viên','500mg','Imexpharm',400,'2027-08-31 00:00:00',3000.00,1);
/*!40000 ALTER TABLE `medicines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `ngay_tao` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notif_user` (`user_id`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,3,'Nhắc lịch khám','Bạn có lịch khám với Bác sĩ Lê Minh Cường vào ngày 06/04/2026 lúc 08:30.',0,'2026-04-05 18:00:00'),(2,4,'Thanh toán đang chờ xử lý','Lịch khám của bạn đang chờ xác nhận thanh toán.',0,'2026-04-01 10:00:00'),(3,2,'Có lịch khám mới','Bạn có bệnh nhân mới đặt lịch khám vào ngày 06/04/2026 lúc 08:30.',0,'2026-04-01 10:10:00');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `gender` enum('MALE','FEMALE','OTHER') DEFAULT NULL,
  `address` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `fk_patients_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (1,3,'2005-08-15 00:00:00','MALE','TP. Hồ Chí Minh'),(2,4,'2004-05-20 00:00:00','MALE','Bến Tre');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(12,2) NOT NULL,
  `method` enum('MOMO','BANKING') NOT NULL DEFAULT 'MOMO',
  `status` enum('PENDING','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING',
  `transaction_id` varchar(255) DEFAULT NULL COMMENT 'Mã giao dịch từ cổng thanh toán',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ngay_tao` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,200000.00,'MOMO','COMPLETED','MOMO_TXN_0001','Thanh toán lịch khám nội tổng quát','2026-04-01 09:00:00'),(2,200000.00,'BANKING','PENDING','BANK_TXN_0002','Thanh toán lịch khám nội tổng quát','2026-04-01 09:10:00');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription_items`
--

DROP TABLE IF EXISTS `prescription_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `prescription_id` int NOT NULL,
  `medicine_id` int NOT NULL,
  `quantity` int NOT NULL COMMENT 'Số lượng thuốc',
  `dosage` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Liều dùng: 1 viên, 5ml...',
  `duration_days` int NOT NULL COMMENT 'Số ngày sử dụng',
  `instructions` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Hướng dẫn thêm: uống sau ăn...',
  `unit_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_presc_item_prescription` (`prescription_id`),
  KEY `fk_presc_item_medicine` (`medicine_id`),
  CONSTRAINT `fk_presc_item_medicine` FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_presc_item_prescription` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription_items`
--

LOCK TABLES `prescription_items` WRITE;
/*!40000 ALTER TABLE `prescription_items` DISABLE KEYS */;
INSERT INTO `prescription_items` VALUES (1,1,1,10,'1 viên/lần',5,'Ngày uống 2 lần sau ăn',1500.00),(2,1,3,14,'1 viên/lần',7,'Ngày uống 2 lần sau ăn',3000.00);
/*!40000 ALTER TABLE `prescription_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `medical_record_id` int NOT NULL,
  `ngay_tao` datetime NOT NULL,
  `note` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_prescription_record` (`medical_record_id`),
  CONSTRAINT `fk_prescription_record` FOREIGN KEY (`medical_record_id`) REFERENCES `medical_records` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (1,1,'2026-04-10 09:30:00','Uống thuốc đúng giờ, tái khám nếu không cải thiện');
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialties`
--

DROP TABLE IF EXISTS `specialties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialties` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `description` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialties`
--

LOCK TABLES `specialties` WRITE;
/*!40000 ALTER TABLE `specialties` DISABLE KEYS */;
INSERT INTO `specialties` VALUES (1,'Nội tổng quát','Khám và điều trị các bệnh nội khoa tổng quát',1),(2,'Nhi khoa','Khám và điều trị cho trẻ em',1),(3,'Da liễu','Khám và điều trị các bệnh về da',1),(4,'Tim mạch','Khám và điều trị bệnh tim mạch',1),(5,'Tai mũi họng','Khám và điều trị các bệnh tai mũi họng',1),(6,'Sản phụ khoa','Khám và theo dõi sức khỏe phụ nữ, thai kỳ và bệnh lý phụ khoa',1),(7,'Chấn thương chỉnh hình','Khám và điều trị bệnh lý xương khớp, cột sống và chấn thương',1),(8,'Thần kinh','Khám và điều trị các bệnh lý thần kinh như đau đầu, chóng mặt, tai biến',1),(9,'Tiêu hóa','Khám và điều trị các bệnh lý dạ dày, gan, ruột và hệ tiêu hóa',1),(10,'Nhãn khoa','Khám và điều trị các bệnh về mắt và thị lực',1);
/*!40000 ALTER TABLE `specialties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN') NOT NULL DEFAULT 'PATIENT',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `phone` varchar(20) NOT NULL DEFAULT '0123456789',
  `avatar` varchar(255) DEFAULT 'https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Quản trị viên hệ thống','ADMIN',1,'0123456789','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(2,'doctor_cuong','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Lê Minh Cường','DOCTOR',1,'0901000001','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(3,'doctor_ha','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Thu Hà','DOCTOR',1,'0901000002','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(4,'doctor_quan','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Trần Minh Quân','DOCTOR',1,'0901000003','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(5,'doctor_lan','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Phạm Ngọc Lan','DOCTOR',1,'0901000004','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(6,'doctor_hung','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Võ Gia Hùng','DOCTOR',1,'0901000005','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(7,'doctor_tram','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Lý Thanh Trâm','DOCTOR',1,'0901000006','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(8,'doctor_phuc','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Đặng Minh Phúc','DOCTOR',1,'0901000007','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(9,'doctor_yen','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Hoàng Bảo Yến','DOCTOR',1,'0901000008','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(10,'doctor_khanh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Quốc Khánh','DOCTOR',1,'0901000009','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(11,'doctor_tam','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Phan Đức Tâm','DOCTOR',1,'0901000010','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(12,'long01','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Tất Văn Long','PATIENT',1,'0912000001','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(13,'hung01','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Thái Lê Hùng','PATIENT',1,'0912000002','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(14,'doctor_an','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Hoài An','DOCTOR',1,'0902000001','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(15,'doctor_binh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Trần Gia Bình','DOCTOR',1,'0902000002','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(16,'doctor_chau','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Lê Minh Châu','DOCTOR',1,'0902000003','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(17,'doctor_duy','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Phạm Anh Duy','DOCTOR',1,'0902000004','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(18,'doctor_giang','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Võ Hà Giang','DOCTOR',1,'0902000005','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(19,'doctor_hai','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Đỗ Thanh Hải','DOCTOR',1,'0902000006','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(20,'doctor_kim','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Bảo Kim','DOCTOR',1,'0902000007','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(21,'doctor_linh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Trịnh Mỹ Linh','DOCTOR',1,'0902000008','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(22,'doctor_minh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Hoàng Minh','DOCTOR',1,'0902000009','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(23,'doctor_nam','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Lê Quốc Nam','DOCTOR',1,'0902000010','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(24,'doctor_phuong','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Phạm Ngọc Phương','DOCTOR',1,'0902000011','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(25,'doctor_quynh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Hồ Như Quỳnh','DOCTOR',1,'0902000012','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(26,'doctor_son','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Trương Đức Sơn','DOCTOR',1,'0902000013','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(27,'doctor_thao','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Thu Thảo','DOCTOR',1,'0902000014','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(28,'doctor_tri','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Đỗ Minh Trí','DOCTOR',1,'0902000015','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(29,'doctor_van','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Lê Khánh Vân','DOCTOR',1,'0902000016','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(30,'doctor_viet','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Phan Quốc Việt','DOCTOR',1,'0902000017','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(31,'doctor_xuan','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Trần Minh Xuân','DOCTOR',1,'0902000018','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(32,'doctor_yuri','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Võ Thanh Yuri','DOCTOR',1,'0902000019','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg'),(33,'doctor_zinh','$2a$10$cbuUDx4BGEs6GiGjM.VMVuoT2t.UYp1akYZyN42If8P8OKLRM2FU.','Bác sĩ Nguyễn Gia Zinh','DOCTOR',1,'0902000020','https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-14 14:11:09
