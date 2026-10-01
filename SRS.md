# Software Requirements Specification (SRS)

## Hệ thống tuyển dụng UET-Hire-Me-Plz

| Thông tin      | Giá trị                                    |
|----------------|--------------------------------------------|
| Dự án          | UET-Hire-Me-Plz (`architecture-solution`)  |
| Phiên bản tài liệu | 1.0                                    |
| Ngày           | 2026-09-17                                 |
| Cơ sở dữ liệu  | MySQL – schema `hire_me`                   |
| Công nghệ      | Spring Boot, Spring Security (JWT), Spring Data JPA |

---

## Mục lục

1. [Giới thiệu](#1-giới-thiệu)
2. [Mô tả tổng quan](#2-mô-tả-tổng-quan)
3. [Mô hình dữ liệu](#3-mô-hình-dữ-liệu)
4. [Quy tắc nghiệp vụ chung](#4-quy-tắc-nghiệp-vụ-chung)
5. [Yêu cầu chức năng – Anonymous (Public)](#5-yêu-cầu-chức-năng--anonymous-public)
6. [Yêu cầu chức năng – Người dùng đã đăng nhập (chung)](#6-yêu-cầu-chức-năng--người-dùng-đã-đăng-nhập-chung)
7. [Yêu cầu chức năng – Candidate](#7-yêu-cầu-chức-năng--candidate)
8. [Yêu cầu chức năng – Recruiter](#8-yêu-cầu-chức-năng--recruiter)
9. [Yêu cầu chức năng – Admin](#9-yêu-cầu-chức-năng--admin)
10. [Ma trận phân quyền](#10-ma-trận-phân-quyền)
11. [Yêu cầu phi chức năng](#11-yêu-cầu-phi-chức-năng)
12. [Quy ước API](#12-quy-ước-api)

---

## 1. Giới thiệu

### 1.1 Mục đích

Tài liệu mô tả các yêu cầu chức năng và phi chức năng của hệ thống **UET-Hire-Me-Plz** – một trang
tuyển dụng việc làm: Recruiter đăng bài tuyển dụng, Candidate có nhu cầu sẽ ứng tuyển. Đây là hệ thống
mang tính học tập, vì vậy thiết kế giữ ở mức đơn giản.
Tài liệu là cơ sở để phát triển, kiểm thử và nghiệm thu backend API.

Độ ưu tiên dùng trong tài liệu: **P0** – bắt buộc cho MVP · **P1** – nên có · **P2** – mở rộng.

### 1.2 Phạm vi

UET-Hire-Me-Plz là một REST API backend cho phép:

- Nhà tuyển dụng đăng tin tuyển dụng, phân loại tin theo danh mục và xử lý hồ sơ ứng tuyển.
- Ứng viên quản lý hồ sơ cá nhân, tìm kiếm việc làm và nộp đơn ứng tuyển.
- Quản trị viên quản lý người dùng, danh mục và kiểm duyệt nội dung.
- Bất kỳ ai (không cần đăng nhập) đều có thể xem và tìm kiếm tin tuyển dụng.

Ngoài phạm vi phiên bản này: giao diện người dùng (frontend), thanh toán, nhắn tin/chat,
gửi email thông báo. Upload file CV trực tiếp là hướng mở rộng (P1); hệ thống lưu CV dưới dạng `cv_url`.

### 1.3 Thuật ngữ

| Thuật ngữ        | Ý nghĩa |
|------------------|---------|
| Anonymous        | Người truy cập chưa đăng nhập. Chỉ gọi được Public API. |
| Candidate        | Ứng viên tìm việc (role `CANDIDATE`). |
| Recruiter        | Nhà tuyển dụng, đại diện cho một công ty (role `RECRUITER`). |
| Admin            | Quản trị viên hệ thống (role `ADMIN`). |
| Job              | Tin tuyển dụng. |
| Job Application  | Đơn ứng tuyển của Candidate vào một Job. |
| Category         | Danh mục/ngành nghề dùng để phân loại Job. |
| Soft delete      | Xoá mềm – đặt `is_deleted = true` thay vì xoá bản ghi. |
| JWT              | JSON Web Token dùng để xác thực stateless. |

---

## 2. Mô tả tổng quan

### 2.1 Các tác nhân (Actors)

| Actor      | Mô tả | Cách có tài khoản |
|------------|-------|-------------------|
| Anonymous  | Khách truy cập, không cần token. | – |
| Candidate  | Người tìm việc. | Tự đăng ký. |
| Recruiter  | Nhà tuyển dụng. | Tự đăng ký (kèm tên công ty). |
| Admin      | Quản trị hệ thống. | Tài khoản Admin đầu tiên được hệ thống tạo sẵn khi khởi động; Admin khác do Admin tạo. Không thể tự đăng ký. |

Quan hệ kế thừa: Candidate, Recruiter và Admin đều là **người dùng đã đăng nhập**, do đó đều có
các chức năng chung ở mục 6, đồng thời vẫn truy cập được mọi Public API ở mục 5.

### 2.2 Luồng nghiệp vụ chính

```
Recruiter đăng ký ──► Tạo Job (OPEN) ──► Gắn Category
                                              │
Anonymous / Candidate tìm kiếm Job ◄──────────┘
                │
Candidate đăng ký/đăng nhập ──► Nộp đơn (PENDING)
                                      │
Recruiter xem đơn ──► ACCEPTED / REJECTED
                                      │
Recruiter cập nhật Job ──► FILLED / CLOSED
```

### 2.3 Ràng buộc & giả định

- Hệ thống là REST API stateless; xác thực bằng JWT gửi qua header `Authorization: Bearer <token>`.
- Quan hệ `users` ↔ `roles` là nhiều–nhiều qua `user_roles`. Khi đăng ký, tài khoản nhận đúng một role
  (`CANDIDATE` hoặc `RECRUITER`); về mặt dữ liệu một user có thể giữ nhiều role (bổ sung role thứ hai là P2).
- Role, bản ghi `user_roles` và hồ sơ tương ứng (`candidates`/`recruiters`) luôn được tạo trong cùng một transaction.
- Toàn bộ khoá chính là UUID (`varchar(36)`).
- Mọi bảng có cột audit (`created_at`, `created_by`, `last_modified_at`, `last_modified_by`) và cột
  `is_deleted` cho soft delete.

---

## 3. Mô hình dữ liệu

### 3.1 Sơ đồ quan hệ

```
users 1───* user_roles *───1 roles
  │
  ├──1───0..1 recruiters 1───* jobs *───* categories   (qua job_categories)
  │                               │
  └──1───0..1 candidates 1───* job_applications *───1 jobs
```

### 3.2 Mô tả các bảng

| Bảng               | Mô tả | Trường chính |
|--------------------|-------|--------------|
| `users`            | Tài khoản đăng nhập. | `email` (unique), `username` (unique), `password_hash`, `displayed_name` |
| `roles`            | Danh sách role cố định. | `name` ∈ {`ADMIN`, `RECRUITER`, `CANDIDATE`} |
| `user_roles`       | Gán role cho user. | unique (`user_id`, `role_id`) |
| `recruiters`       | Hồ sơ nhà tuyển dụng (1-1 với `users`). | `company_name` (bắt buộc) |
| `candidates`       | Hồ sơ ứng viên (1-1 với `users`). | `bio`, `cv_url`, `phone` |
| `jobs`             | Tin tuyển dụng của Recruiter. | `title`, `description`, `salary_min`, `salary_max`, `status`, `job_type` |
| `categories`       | Danh mục ngành nghề. | `name` (unique) |
| `job_categories`   | Gán Category cho Job (n-n). | unique (`job_id`, `category_id`) |
| `job_applications` | Đơn ứng tuyển. | `status`, `cv_url` (bắt buộc), unique (`job_id`, `candidate_id`) |

### 3.3 Các giá trị liệt kê (Enum)

| Enum                | Giá trị | Ý nghĩa |
|---------------------|---------|---------|
| `JobStatus`         | `OPEN`  | Đang tuyển, nhận đơn ứng tuyển. |
|                     | `FILLED`| Đã tuyển đủ, không nhận đơn mới. |
|                     | `CLOSED`| Đã đóng tin, không nhận đơn mới. |
| `JobType`           | `FULL_TIME`, `PART_TIME` | Hình thức làm việc. |
| `ApplicationStatus` | `PENDING` | Đơn mới nộp, chờ xử lý. |
|                     | `ACCEPTED`| Được nhà tuyển dụng chấp nhận. |
|                     | `REJECTED`| Bị từ chối. |
| `RoleName`          | `ADMIN`, `RECRUITER`, `CANDIDATE` | Vai trò người dùng. |

### 3.4 Vòng đời trạng thái

**Job**

```
          tạo mới
            │
            ▼
         ┌──────┐   tuyển đủ     ┌────────┐
         │      │ ─────────────► │ FILLED │
         │ OPEN │ ◄───────────── └────────┘
         │      │    mở lại
         │      │   ngừng tuyển  ┌────────┐
         │      │ ─────────────► │ CLOSED │
         └──────┘ ◄───────────── └────────┘
                     mở lại
```

- Chuyển hợp lệ: `OPEN → FILLED`, `OPEN → CLOSED`, `FILLED → OPEN`, `CLOSED → OPEN`. Mọi chuyển khác → `400`.

**Job Application**

```
┌─────────┐  recruiter chấp nhận  ┌──────────┐
│ PENDING │ ────────────────────► │ ACCEPTED │
└─────────┘                       └──────────┘
     │      recruiter từ chối     ┌──────────┐
     └──────────────────────────► │ REJECTED │
                                  └──────────┘
```

- Chỉ đơn ở trạng thái `PENDING` mới được chuyển trạng thái. `ACCEPTED` và `REJECTED` là trạng thái cuối.

---

## 4. Quy tắc nghiệp vụ chung

| Mã     | Quy tắc |
|--------|---------|
| BR-01  | `username` và `email` là duy nhất trên toàn hệ thống (kể cả tài khoản đã bị xoá mềm). |
| BR-02  | Mật khẩu được lưu dưới dạng hash BCrypt; không bao giờ trả `password_hash` ra API. |
| BR-03  | Bản ghi có `is_deleted = true` được coi như không tồn tại với mọi API nghiệp vụ (trừ API quản trị có tham số xem bản ghi đã xoá). |
| BR-04  | Tài khoản bị xoá mềm không thể đăng nhập; token cũ của tài khoản đó bị từ chối. |
| BR-05  | Lương: hoặc cả `salary_min` và `salary_max` đều rỗng (thoả thuận), hoặc cả hai đều có giá trị với `0 ≤ salary_min ≤ salary_max`. |
| BR-06  | Mỗi Candidate chỉ được ứng tuyển **một lần** vào một Job (unique `job_id`, `candidate_id`). |
| BR-07  | Chỉ Job có `status = OPEN` và chưa bị xoá mới nhận đơn ứng tuyển. |
| BR-08  | `cv_url` của đơn ứng tuyển là bắt buộc; nếu Candidate không truyền, hệ thống lấy `cv_url` hiện tại trong hồ sơ Candidate. Nếu cả hai đều trống → lỗi. Giá trị được lưu như "ảnh chụp" tại thời điểm nộp, không đổi khi hồ sơ thay đổi. |
| BR-09  | Recruiter chỉ được thao tác trên Job do chính mình tạo và đơn ứng tuyển thuộc các Job đó. |
| BR-10  | Candidate chỉ được xem/thao tác trên đơn ứng tuyển của chính mình. |
| BR-11  | Tên Category là duy nhất (không phân biệt hoa thường). Không được xoá Category đang gắn với Job `OPEN`. |
| BR-12  | Các cột audit được hệ thống tự điền từ người dùng đang đăng nhập; client không được gửi các trường này. |
| BR-13  | Thông tin liên hệ nhạy cảm của Candidate (`phone`, `email`, `cv_url`) không được lộ qua Public API; chỉ Recruiter có đơn ứng tuyển vào Job của mình và Admin mới được xem. |
| BR-14  | `recruiter_id` của Job và `candidate_id` của đơn luôn lấy từ người dùng đang đăng nhập, không nhận từ request body. |
| BR-15  | Hệ thống luôn còn ít nhất một Admin đang hoạt động: không được khoá hoặc gỡ role của Admin cuối cùng. |

---

## 5. Yêu cầu chức năng – Anonymous (Public)

> Public API: **không yêu cầu token**, ai cũng có thể gọi. Người dùng đã đăng nhập cũng dùng được các API này.

### FR-PUB-01: Đăng ký tài khoản

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/auth/register` |
| Mô tả | Tạo tài khoản mới với vai trò Candidate hoặc Recruiter. |
| Input | `username`, `email`, `password`, `retypePassword`, `displayedName` (tuỳ chọn), `role` ∈ {`CANDIDATE`, `RECRUITER`}, `companyName` (bắt buộc nếu `role = RECRUITER`) |
| Xử lý | 1. Validate dữ liệu đầu vào.<br>2. Kiểm tra `password == retypePassword`.<br>3. Kiểm tra `username`, `email` chưa tồn tại.<br>4. Tạo bản ghi `users`, gán role trong `user_roles`.<br>5. Tạo bản ghi hồ sơ tương ứng trong `candidates` hoặc `recruiters`.<br>Toàn bộ trong một transaction. |
| Output | `201 Created` |
| Lỗi | `400` dữ liệu không hợp lệ / mật khẩu không khớp / thiếu `companyName`; `400` nếu `role = ADMIN`; `409` username hoặc email đã tồn tại. |
| Ràng buộc | `username`: 4–50 ký tự, chữ, số, `_`, `.`. `email`: đúng định dạng. `password`: tối thiểu 8 ký tự. |

### FR-PUB-02: Đăng nhập

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/auth/login` |
| Input | `username` (chấp nhận username hoặc email), `password` |
| Xử lý | Xác thực thông tin; tài khoản không bị xoá mềm; sinh JWT chứa `userId` và danh sách role. |
| Output | `200 OK` – access token (và refresh token nếu có). |
| Lỗi | `401` sai username hoặc mật khẩu (không tiết lộ trường nào sai). |

### FR-PUB-03: Làm mới token (P1)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/auth/refresh` |
| Input | `refreshToken` |
| Output | `200 OK` – access token mới. |
| Lỗi | `401` refresh token không hợp lệ hoặc hết hạn. |

### FR-PUB-04: Tìm kiếm & xem danh sách việc làm

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/jobs` |
| Mô tả | Danh sách Job công khai, có phân trang, lọc và sắp xếp. |
| Tham số | `keyword` (tìm theo `title`, `description`), `categoryIds` (nhiều giá trị), `jobType`, `salaryMin`, `salaryMax`, `recruiterId`, `status` (mặc định `OPEN`), `page`, `size`, `sort` (`createdAt`, `salaryMax`; mặc định mới nhất trước) |
| Output | Danh sách tóm tắt: `id`, `title`, `companyName`, `jobType`, `salaryMin`, `salaryMax`, `status`, `categories`, `createdAt` + thông tin phân trang. |
| Quy tắc | Không trả về Job đã bị xoá mềm hoặc Job của Recruiter đã bị xoá mềm. |

### FR-PUB-05: Xem chi tiết việc làm

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/jobs/{jobId}` |
| Output | Toàn bộ thông tin Job: `title`, `description`, lương, `jobType`, `status`, danh sách Category, thông tin Recruiter công khai (`recruiterId`, `companyName`, `displayedName`), `createdAt`, `lastModifiedAt`. |
| Lỗi | `404` Job không tồn tại hoặc đã bị xoá. |

### FR-PUB-06: Xem danh sách danh mục

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/categories` |
| Output | Danh sách Category (`id`, `name`), kèm số lượng Job `OPEN` thuộc mỗi Category (tuỳ chọn `withJobCount=true`). |

### FR-PUB-07: Xem hồ sơ công khai của nhà tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/recruiters/{recruiterId}` |
| Output | `companyName`, `displayedName`, số Job đang `OPEN`. Danh sách Job của Recruiter lấy qua `GET /api/v1/jobs?recruiterId=...`. |
| Lỗi | `404` Recruiter không tồn tại hoặc đã bị xoá. |

### FR-PUB-08: Tài liệu API & health check

| Endpoint | Mô tả |
|----------|-------|
| `GET /swagger-ui.html`, `GET /v3/api-docs/**` | Tài liệu OpenAPI (chỉ bật ở profile `dev`). |
| `GET /actuator/health` | Kiểm tra trạng thái dịch vụ. |

---

## 6. Yêu cầu chức năng – Người dùng đã đăng nhập (chung)

> Áp dụng cho mọi role: `ADMIN`, `RECRUITER`, `CANDIDATE`. Yêu cầu JWT hợp lệ.

### FR-USR-01: Xem thông tin tài khoản hiện tại

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/users/me` |
| Output | `id`, `username`, `email`, `displayedName`, `roles`, `createdAt`, kèm hồ sơ Candidate/Recruiter nếu có. |

### FR-USR-02: Cập nhật thông tin tài khoản

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/users/me` |
| Input | `displayedName`, `email` |
| Lỗi | `400` email sai định dạng; `409` email đã được dùng. |
| Quy tắc | `username` không được phép thay đổi. |

### FR-USR-03: Đổi mật khẩu

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PUT /api/v1/users/me/password` |
| Input | `currentPassword`, `newPassword`, `retypeNewPassword` |
| Xử lý | Kiểm tra mật khẩu hiện tại đúng; mật khẩu mới khớp và khác mật khẩu cũ; lưu hash mới. |
| Lỗi | `400` mật khẩu mới không hợp lệ / không khớp; `401` mật khẩu hiện tại sai. |

### FR-USR-04: Đăng xuất (P1)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/auth/logout` |
| Xử lý | Vô hiệu hoá refresh token hiện tại. Access token hết hiệu lực khi hết hạn. |
| Output | `200 OK` |

---

## 7. Yêu cầu chức năng – Candidate

> Yêu cầu JWT với role `CANDIDATE`.

### 7.1 Quản lý hồ sơ ứng viên

#### FR-CAN-01: Xem hồ sơ ứng viên của mình

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/candidates/me` |
| Output | `bio`, `cvUrl`, `phone` + thông tin user (`username`, `email`, `displayedName`). |

#### FR-CAN-02: Cập nhật hồ sơ ứng viên

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/candidates/me` |
| Input | `bio`, `cvUrl`, `phone` (đều tuỳ chọn) |
| Ràng buộc | `cvUrl`: URL hợp lệ (http/https), ≤ 255 ký tự. `phone`: chỉ gồm số, `+`, khoảng trắng, 8–15 chữ số. |
| Lỗi | `400` dữ liệu không hợp lệ. |

### 7.2 Ứng tuyển

#### FR-CAN-03: Nộp đơn ứng tuyển

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/jobs/{jobId}/applications` |
| Input | `cvUrl` (tuỳ chọn – mặc định lấy từ hồ sơ, xem BR-08) |
| Xử lý | 1. Kiểm tra Job tồn tại và `status = OPEN` (BR-07).<br>2. Kiểm tra Candidate chưa ứng tuyển Job này (BR-06).<br>3. Xác định `cvUrl`.<br>4. Tạo `job_applications` với `status = PENDING`. |
| Output | `201 Created` – thông tin đơn ứng tuyển. |
| Lỗi | `404` Job không tồn tại; `400` Job không ở trạng thái `OPEN`; `400` không có CV; `409` đã ứng tuyển trước đó. |

#### FR-CAN-04: Xem danh sách đơn đã nộp

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/candidates/me/applications` |
| Tham số | `status`, `page`, `size`, `sort` |
| Output | `applicationId`, `status`, `cvUrl`, `createdAt`, `lastModifiedAt`, thông tin tóm tắt Job (`jobId`, `title`, `companyName`, `jobStatus`). |

#### FR-CAN-05: Xem chi tiết đơn ứng tuyển

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/applications/{applicationId}` (dùng chung với FR-REC-10; hệ thống kiểm tra quyền theo role) |
| Lỗi | `404` đơn không tồn tại **hoặc không thuộc về Candidate** (không tiết lộ sự tồn tại – BR-10). |

#### FR-CAN-06: Rút đơn ứng tuyển (P2)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `DELETE /api/v1/applications/{applicationId}` |
| Xử lý | Chỉ cho phép khi đơn đang `PENDING`. Xoá mềm đơn (`is_deleted = true`). |
| Output | `204 No Content` |
| Lỗi | `404` đơn không tồn tại/không thuộc Candidate; `400` đơn đã được xử lý (`ACCEPTED`/`REJECTED`). |
| Quy tắc | Sau khi rút đơn, Candidate được nộp lại vào cùng Job; hệ thống khôi phục đơn cũ với `status = PENDING` và `cvUrl` mới. |

#### FR-CAN-07: Kiểm tra trạng thái ứng tuyển của một Job (P1)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/jobs/{jobId}/applications/me` |
| Mô tả | Giúp client hiển thị "Đã ứng tuyển" trên trang chi tiết Job. |
| Output | Thông tin đơn nếu có; `404` nếu chưa ứng tuyển. |

---

## 8. Yêu cầu chức năng – Recruiter

> Yêu cầu JWT với role `RECRUITER`.

### 8.1 Quản lý hồ sơ nhà tuyển dụng

#### FR-REC-01: Xem hồ sơ nhà tuyển dụng của mình

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/recruiters/me` |
| Output | `companyName` + thông tin user, thống kê nhanh: tổng số Job theo trạng thái, số đơn `PENDING`. |

#### FR-REC-02: Cập nhật hồ sơ nhà tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/recruiters/me` |
| Input | `companyName` (không được rỗng, ≤ 255 ký tự) |
| Lỗi | `400` dữ liệu không hợp lệ. |

### 8.2 Quản lý tin tuyển dụng

#### FR-REC-03: Tạo tin tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/jobs` |
| Input | `title` (bắt buộc, ≤ 255), `description`, `jobType` (bắt buộc), `salaryMin`, `salaryMax`, `categoryIds` (danh sách) |
| Xử lý | 1. Validate theo BR-05.<br>2. Kiểm tra mọi `categoryId` tồn tại.<br>3. Tạo `jobs` với `recruiter_id` = người dùng hiện tại, `status = OPEN`.<br>4. Tạo các bản ghi `job_categories`.<br>Toàn bộ trong một transaction. |
| Output | `201 Created` – chi tiết Job. |
| Lỗi | `400` dữ liệu không hợp lệ / khoảng lương sai; `404` Category không tồn tại. |

#### FR-REC-04: Xem danh sách tin tuyển dụng của mình

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/recruiters/me/jobs` |
| Tham số | `keyword`, `status` (mọi trạng thái), `jobType`, `categoryIds`, `page`, `size`, `sort` |
| Output | Như FR-PUB-04, bổ sung số lượng đơn ứng tuyển theo từng trạng thái cho mỗi Job. |

#### FR-REC-05: Cập nhật tin tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PUT /api/v1/jobs/{jobId}` |
| Input | `title`, `description`, `jobType`, `salaryMin`, `salaryMax`, `categoryIds` |
| Xử lý | Kiểm tra quyền sở hữu (BR-09); validate BR-05; đồng bộ lại `job_categories` (thêm mới, xoá mềm các liên kết bị bỏ). |
| Lỗi | `404` Job không tồn tại hoặc không thuộc Recruiter; `400` dữ liệu không hợp lệ. |

#### FR-REC-06: Thay đổi trạng thái tin tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/jobs/{jobId}/status` |
| Input | `status` ∈ {`OPEN`, `FILLED`, `CLOSED`} |
| Xử lý | Kiểm tra quyền sở hữu; kiểm tra chuyển trạng thái hợp lệ theo mục 3.4. Các đơn `PENDING` giữ nguyên trạng thái khi Job chuyển sang `FILLED`/`CLOSED`. |
| Lỗi | `404`; `400` chuyển trạng thái không hợp lệ. |

#### FR-REC-07: Xoá tin tuyển dụng (P1)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `DELETE /api/v1/jobs/{jobId}` |
| Xử lý | Kiểm tra quyền sở hữu. Nếu Job **chưa có đơn ứng tuyển** → xoá mềm Job và các `job_categories`. Nếu **đã có đơn** → từ chối, yêu cầu chuyển sang `CLOSED`. |
| Output | `204 No Content` |
| Lỗi | `404`; `409` Job đã có đơn ứng tuyển. |

### 8.3 Xử lý đơn ứng tuyển

#### FR-REC-08: Xem danh sách đơn ứng tuyển của một Job

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/jobs/{jobId}/applications` |
| Tham số | `status`, `page`, `size`, `sort` |
| Output | `applicationId`, `status`, `cvUrl`, `createdAt`, thông tin ứng viên (`candidateId`, `displayedName`, `email`, `phone`, `bio`). |
| Lỗi | `404` Job không tồn tại hoặc không thuộc Recruiter. |

#### FR-REC-09: Xem tất cả đơn ứng tuyển vào các Job của mình

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/recruiters/me/applications` |
| Tham số | `jobId`, `status`, `page`, `size`, `sort` |
| Output | Như FR-REC-08, kèm `jobId`, `jobTitle`. |

#### FR-REC-10: Xem chi tiết đơn ứng tuyển

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/applications/{applicationId}` |
| Output | Toàn bộ thông tin đơn và hồ sơ ứng viên. |
| Lỗi | `404` đơn không tồn tại hoặc không thuộc Job của Recruiter. |

#### FR-REC-11: Xem hồ sơ ứng viên

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/candidates/{candidateId}` |
| Output | `displayedName`, `email`, `bio`, `phone`, `cvUrl`. |
| Quy tắc | Chỉ xem được nếu ứng viên có ít nhất một đơn ứng tuyển vào Job của Recruiter (BR-13). |
| Lỗi | `404` ứng viên không tồn tại hoặc không có đơn vào Job của Recruiter. |

#### FR-REC-12: Chấp nhận / Từ chối đơn ứng tuyển

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/applications/{applicationId}/status` |
| Input | `status` ∈ {`ACCEPTED`, `REJECTED`} |
| Xử lý | Kiểm tra quyền sở hữu (BR-09); đơn phải đang `PENDING`; cập nhật trạng thái. |
| Lỗi | `404`; `400` trạng thái đích không hợp lệ hoặc đơn đã được xử lý. |

---

## 9. Yêu cầu chức năng – Admin

> Yêu cầu JWT với role `ADMIN`. Tiền tố endpoint: `/api/v1/admin`.

### 9.1 Quản lý người dùng

#### FR-ADM-01: Xem danh sách người dùng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/admin/users` |
| Tham số | `keyword` (username, email, displayedName), `role`, `includeDeleted` (mặc định `false`), `page`, `size`, `sort` |
| Output | `id`, `username`, `email`, `displayedName`, `roles`, `isDeleted`, `createdAt`. |

#### FR-ADM-02: Xem chi tiết người dùng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/admin/users/{userId}` |
| Output | Thông tin user, roles, hồ sơ Candidate/Recruiter, thống kê (số Job đã đăng / số đơn đã nộp), thông tin audit. |

#### FR-ADM-03: Tạo tài khoản (P2)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/admin/users` |
| Input | Như FR-PUB-01, cho phép `role = ADMIN`. |
| Mô tả | Admin tạo tài khoản Admin khác hoặc tạo sẵn tài khoản Recruiter/Candidate. |
| Lỗi | `400`; `409` username/email đã tồn tại. |

#### FR-ADM-04: Cấp / thu hồi role

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/admin/users/{userId}/roles` (cấp) · `DELETE /api/v1/admin/users/{userId}/roles/{roleName}` (thu hồi) |
| Input | `role` ∈ {`ADMIN`, `RECRUITER`, `CANDIDATE`}; `companyName` bắt buộc khi cấp `RECRUITER` mà user chưa có hồ sơ Recruiter |
| Xử lý | Cấp role: tạo `user_roles` và hồ sơ tương ứng nếu chưa có (khôi phục nếu đã xoá mềm). Thu hồi role: xoá mềm `user_roles`. |
| Ràng buộc | Không thu hồi role `ADMIN` của Admin cuối cùng (BR-15); user phải còn ít nhất một role. |
| Lỗi | `404`; `409` user đã có role; `400` vi phạm ràng buộc. |

#### FR-ADM-05: Khoá (xoá mềm) tài khoản

| Mục | Nội dung |
|-----|----------|
| Endpoint | `DELETE /api/v1/admin/users/{userId}` |
| Xử lý | Đặt `is_deleted = true` cho user (và hồ sơ Candidate/Recruiter). Tài khoản không thể đăng nhập (BR-04). Với Recruiter: các Job đang `OPEN` được chuyển sang `CLOSED`. |
| Ràng buộc | Admin không được tự khoá chính mình; không được khoá Admin cuối cùng còn hoạt động (BR-15). |
| Output | `204 No Content` |
| Lỗi | `404`; `400` vi phạm ràng buộc. |

#### FR-ADM-06: Khôi phục tài khoản

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/admin/users/{userId}/restore` |
| Xử lý | Đặt `is_deleted = false` cho user và hồ sơ đi kèm. Job đã bị đóng ở FR-ADM-05 **không** tự mở lại. |
| Lỗi | `404`; `400` tài khoản chưa bị khoá. |

#### FR-ADM-07: Đặt lại mật khẩu cho người dùng (P2)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PUT /api/v1/admin/users/{userId}/password` |
| Input | `newPassword`, `retypeNewPassword` |
| Lỗi | `404`; `400` mật khẩu không hợp lệ. |

### 9.2 Quản lý danh mục

#### FR-ADM-08: Tạo danh mục

| Mục | Nội dung |
|-----|----------|
| Endpoint | `POST /api/v1/admin/categories` |
| Input | `name` (bắt buộc, ≤ 255, BR-11) |
| Output | `201 Created` |
| Lỗi | `400`; `409` tên đã tồn tại. |

#### FR-ADM-09: Cập nhật danh mục

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PUT /api/v1/admin/categories/{categoryId}` |
| Input | `name` |
| Lỗi | `404`; `409` tên đã tồn tại. |

#### FR-ADM-10: Xoá danh mục

| Mục | Nội dung |
|-----|----------|
| Endpoint | `DELETE /api/v1/admin/categories/{categoryId}` |
| Xử lý | Nếu Category đang gắn với Job `OPEN` → từ chối (BR-11). Ngược lại xoá mềm Category và các liên kết `job_categories` tương ứng. |
| Output | `204 No Content` |
| Lỗi | `404`; `409` Category đang được dùng bởi Job `OPEN`. |

### 9.3 Kiểm duyệt tin tuyển dụng & đơn ứng tuyển

#### FR-ADM-11: Xem tất cả tin tuyển dụng

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/admin/jobs` |
| Tham số | Như FR-PUB-04, bổ sung `status` (mọi trạng thái), `includeDeleted`. |

#### FR-ADM-12: Đóng / gỡ tin tuyển dụng vi phạm

| Mục | Nội dung |
|-----|----------|
| Endpoint | `PATCH /api/v1/jobs/{jobId}/status` (đổi trạng thái) · `DELETE /api/v1/jobs/{jobId}` (xoá mềm) – cùng endpoint với Recruiter, Admin được bỏ qua kiểm tra quyền sở hữu |
| Mô tả | Admin có thể đóng hoặc gỡ bất kỳ Job nào, không phụ thuộc quyền sở hữu và không bị giới hạn bởi việc Job đã có đơn. |
| Lỗi | `404`; `400` trạng thái không hợp lệ. |

#### FR-ADM-13: Xem tất cả đơn ứng tuyển

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/admin/applications` |
| Tham số | `jobId`, `candidateId`, `recruiterId`, `status`, `page`, `size`, `sort` |
| Mô tả | Chỉ đọc, phục vụ tra soát/hỗ trợ. Admin **không** chấp nhận/từ chối đơn thay Recruiter. |

### 9.4 Thống kê

#### FR-ADM-14: Dashboard thống kê (P1)

| Mục | Nội dung |
|-----|----------|
| Endpoint | `GET /api/v1/admin/stats` |
| Tham số | `from`, `to` (khoảng thời gian, tuỳ chọn) |
| Output | Tổng số user theo role; số Job theo `status` và `jobType`; số đơn theo `status`; top Category theo số Job; số user/Job/đơn mới trong khoảng thời gian. |

---

## 10. Ma trận phân quyền

Ký hiệu: ✅ được phép · 🔒 chỉ trên dữ liệu của chính mình · ❌ không được phép

| Chức năng | Anonymous | Candidate | Recruiter | Admin |
|-----------|:---------:|:---------:|:---------:|:-----:|
| Đăng ký (Candidate/Recruiter) | ✅ | ❌ | ❌ | ✅ (FR-ADM-03) |
| Đăng nhập / làm mới token | ✅ | ✅ | ✅ | ✅ |
| Xem, tìm kiếm Job công khai | ✅ | ✅ | ✅ | ✅ |
| Xem Category | ✅ | ✅ | ✅ | ✅ |
| Xem hồ sơ công khai Recruiter | ✅ | ✅ | ✅ | ✅ |
| Xem/sửa tài khoản, đổi mật khẩu, đăng xuất | ❌ | 🔒 | 🔒 | 🔒 |
| Quản lý hồ sơ Candidate | ❌ | 🔒 | ❌ | ✅ (xem) |
| Nộp / rút đơn ứng tuyển | ❌ | 🔒 | ❌ | ❌ |
| Xem đơn ứng tuyển | ❌ | 🔒 | 🔒 (Job của mình) | ✅ |
| Quản lý hồ sơ Recruiter | ❌ | ❌ | 🔒 | ✅ (xem) |
| Tạo / sửa Job | ❌ | ❌ | 🔒 | ❌ |
| Đổi trạng thái / xoá Job | ❌ | ❌ | 🔒 | ✅ |
| Chấp nhận / từ chối đơn | ❌ | ❌ | 🔒 | ❌ |
| Quản lý Category | ❌ | ❌ | ❌ | ✅ |
| Xem hồ sơ ứng viên | ❌ | 🔒 | 🔒 (ứng viên có đơn vào Job của mình) | ✅ |
| Quản lý người dùng, cấp/thu hồi role | ❌ | ❌ | ❌ | ✅ |
| Thống kê hệ thống | ❌ | ❌ | ❌ | ✅ |

---

## 11. Yêu cầu phi chức năng

### 11.1 Bảo mật

| Mã | Yêu cầu |
|----|---------|
| NFR-SEC-01 | Mọi endpoint ngoài danh sách Public API (mục 5) yêu cầu JWT hợp lệ; thiếu/sai token → `401`, sai role → `403`. |
| NFR-SEC-02 | Phân quyền kiểm tra ở cả tầng URL (theo role) và tầng service (quyền sở hữu dữ liệu). |
| NFR-SEC-03 | Access token có thời hạn ngắn (đề xuất 15–60 phút); refresh token có thời hạn dài hơn và có thể thu hồi. |
| NFR-SEC-04 | Mật khẩu hash bằng BCrypt; secret JWT và mật khẩu admin mặc định được cấu hình qua biến môi trường, không commit vào mã nguồn. |
| NFR-SEC-05 | Swagger UI chỉ bật ở môi trường `dev`. |
| NFR-SEC-06 | Truy cập tài nguyên không thuộc quyền sở hữu trả `404` thay vì `403` để tránh lộ sự tồn tại của dữ liệu. |
| NFR-SEC-07 | Giới hạn tần suất (rate limit) cho `/auth/login` và `/auth/register` để chống brute-force. |

### 11.2 Hiệu năng

| Mã | Yêu cầu |
|----|---------|
| NFR-PERF-01 | API danh sách bắt buộc phân trang; `size` mặc định 20, tối đa 100. |
| NFR-PERF-02 | Thời gian phản hồi p95 < 500ms với các API đọc ở tải thông thường. |
| NFR-PERF-03 | Đánh chỉ mục cho `jobs(status, is_deleted, created_at)`, `jobs(recruiter_id)`, `job_applications(candidate_id)`. |

### 11.3 Toàn vẹn dữ liệu & kiểm toán

| Mã | Yêu cầu |
|----|---------|
| NFR-DATA-01 | Các thao tác ghi nhiều bảng (đăng ký, tạo/sửa Job, khoá user) thực hiện trong một transaction. |
| NFR-DATA-02 | Không xoá cứng dữ liệu nghiệp vụ; dùng soft delete. |
| NFR-DATA-03 | Mọi bản ghi lưu thông tin audit (người tạo, thời điểm tạo, người sửa cuối, thời điểm sửa cuối) và được hệ thống tự động ghi nhận. |
| NFR-DATA-04 | Ràng buộc DB (unique, foreign key, check lương) là lớp bảo vệ cuối; lỗi vi phạm ràng buộc được ánh xạ sang mã lỗi HTTP phù hợp (`409`/`400`), không trả `500`. |

### 11.4 Khả năng bảo trì & vận hành

| Mã | Yêu cầu |
|----|---------|
| NFR-OPS-01 | Toàn bộ API được mô tả bằng OpenAPI. |
| NFR-OPS-02 | Ghi log có cấu trúc cho mọi request lỗi; không log mật khẩu hoặc token. |
| NFR-OPS-03 | Môi trường phát triển được dựng bằng container, khởi tạo sẵn schema và danh sách role. |
| NFR-OPS-04 | Endpoint `/actuator/health` phục vụ health check. |

---

## 12. Quy ước API

### 12.1 Định dạng chung

- Base path: `/api/v1`
- Content-Type: `application/json`
- Thời gian theo chuẩn ISO-8601, UTC.

### 12.2 Phản hồi thành công

```json
{
  "status_code": 200,
  "message": "OK",
  "data": { }
}
```

Danh sách có phân trang:

```json
{
  "status_code": 200,
  "message": "OK",
  "data": {
    "items": [ ],
    "page": 0,
    "size": 20,
    "total_elements": 135,
    "total_pages": 7
  }
}
```

### 12.3 Phản hồi lỗi

```json
{
  "status_code": 409,
  "error": "Conflict",
  "message": "User with username john already exists",
  "path": "/api/v1/auth/register",
  "timestamp": "2026-09-17T08:00:00Z"
}
```

### 12.4 Mã trạng thái HTTP

| Mã  | Ý nghĩa trong hệ thống |
|-----|------------------------|
| 200 | Thành công. |
| 201 | Tạo mới thành công. |
| 204 | Xoá/rút thành công, không có nội dung trả về. |
| 400 | Dữ liệu không hợp lệ hoặc vi phạm quy tắc nghiệp vụ. |
| 401 | Chưa xác thực / token không hợp lệ / sai thông tin đăng nhập. |
| 403 | Đã xác thực nhưng không đủ quyền (sai role). |
| 404 | Không tìm thấy tài nguyên (hoặc không thuộc quyền sở hữu). |
| 409 | Xung đột dữ liệu (trùng username, email, tên Category, đã ứng tuyển...). |
| 500 | Lỗi hệ thống không mong đợi. |
