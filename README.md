# Ecommerce System — Flash Sale

Monolithic Spring Boot backend cho hệ thống thương mại điện tử tập trung vào flash sale. Mô hình dữ liệu được chuyển từ EcommerceMicroservices và gộp vào một database MySQL `ecommerce`.

## Phạm vi hiện tại

- Xác thực JWT: đăng ký, đăng nhập, refresh token; vai trò `ADMIN`, `CUSTOMER`, `SELLER`.
- Customer và Seller dùng bảng hồ sơ riêng, liên kết với User qua `user_id`, theo convention cũ.
- Entity và SQL cho sản phẩm, chiến dịch flash sale, đơn hàng và ảnh sản phẩm.
- Giá bán và số lượng hiện tại lưu trực tiếp tại `Product.price` và `Product.stock`; campaign item tham chiếu sản phẩm.
- Seller upload ảnh JPEG/PNG; bucket `ecommerce-product-images` được khởi tạo khi ứng dụng chạy.
- API nghiệp vụ flash sale sẽ triển khai ở bước tiếp theo. Các API tuyển dụng đã được xóa.

Chi tiết phạm vi, nguồn entity và tiến độ: [REFACTOR.md](REFACTOR.md).

## Công nghệ

Java 17+, Spring Boot, Spring Data JPA, Spring Security, JWT, MySQL, Lombok và OpenAPI. Hạ tầng S3/RustFS hiện có được giữ lại.

## Database

Chạy lần lượt trên database mới:

1. `infrastructure/database/init/01-schema.sql`
2. `infrastructure/database/init/02-seed-roles.sql`
3. `infrastructure/database/init/03-fake-init.sql` nếu cần dữ liệu phát triển.

Script thứ ba tạo `admin` / `Admin@123`, `seller01` / `Demo@123`, `customer01` / `Demo@123` và một chiến dịch mẫu bắt đầu sau thời điểm seed một ngày. Chỉ dùng tài khoản mẫu cho phát triển.

Docker Compose mount thư mục script vào `/docker-entrypoint-initdb.d`; MySQL chỉ tự chạy các script khi khởi tạo volume mới. Đây là schema khởi tạo mới, không phải migration cho database tuyển dụng cũ. Không cần xóa volume cũ để xem hoặc kiểm tra thay đổi.

## Cấu hình và chạy

Cấu hình datasource bằng `SPRING_DATASOURCE_URL` (database `ecommerce`), `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`. Thiết lập các thuộc tính JWT, S3 và admin theo cấu hình môi trường hiện có trong `deploy/env` hoặc `application-dev.yaml`.

```bash
./mvnw spring-boot:run
```

Ứng dụng mặc định chạy tại cổng 8080. Swagger UI: `/swagger-ui/index.html`; health: `/actuator/health`.

## API hiện tại

| Method | Endpoint | Chức năng |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | Đăng ký customer/seller |
| POST | `/api/v1/auth/login` | Đăng nhập |
| POST | `/api/v1/auth/refresh` | Làm mới token |
| POST | `/api/v1/files/product-images` | Seller upload ảnh, multipart field `file` |

Đăng ký sử dụng `roles`, `displayed_name`, `retype_password`; customer có `phone`, `shipping_address`; seller cần `shop_name`. Không cho tự đăng ký vai trò `ADMIN`.

Upload cần Bearer token có vai trò `SELLER` và hồ sơ seller còn hoạt động. Giới hạn multipart hiện tại: 10 MB mỗi file. API trả về ID file, bucket, object key và metadata; metadata lưu trong bảng `files`, nội dung ảnh lưu trên S3/RustFS. Chưa tự gắn ảnh vào sản phẩm vì API đăng bán chưa được triển khai. Không seed metadata ảnh giả khi chưa có object thực tế.

Bucket initializer dùng cấu hình S3 hiện có, tạo bucket khi chưa tồn tại. Cần S3/RustFS hoạt động khi khởi động ứng dụng.

## Triển khai

Giữ cấu trúc hiện có tại `deploy/compose` và `deploy/script`. Cấu hình biến môi trường theo từng môi trường trước khi chạy các script triển khai. Các sơ đồ ảnh cũ trong `docs` chưa được cập nhật cho mô hình Ecommerce.
