# Architecture Solution — Flash Sale

Backend thương mại điện tử quy mô nhỏ, xoay quanh **các chiến dịch flash sale**: người bán tạo chiến dịch có thời gian giới hạn, sản phẩm và tồn kho; khách hàng đặt mua các sản phẩm đó; hệ thống gọi cổng thanh toán bên ngoài (được mô phỏng) ngay trong giao dịch tạo đơn hàng.

## Mục lục

- [Chức năng](#chức-năng)
- [Kiến trúc](#kiến-trúc)
- [Cơ sở dữ liệu](#cơ-sở-dữ-liệu)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Hướng dẫn chạy](#hướng-dẫn-chạy)
- [Tài liệu API](#tài-liệu-api)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Đóng gói và triển khai](#đóng-gói-và-triển-khai)
- [Chín vấn đề được cố ý giữ lại](#chín-vấn-đề-được-cố-ý-giữ-lại)

## Chức năng

| Nhóm | Chức năng |
| --- | --- |
| Tài khoản | Đăng ký với vai trò `SELLER` hoặc `CUSTOMER`, đăng nhập, làm mới token. JWT access token có thời hạn 5 phút; refresh token có thời hạn 7 ngày và được thay mới sau mỗi lần sử dụng. Mật khẩu được băm bằng BCrypt. |
| Hình ảnh | Người bán tải lên ảnh thu nhỏ JPEG/PNG. Dữ liệu ảnh được lưu vào kho tương thích S3 (RustFS), thông tin mô tả ảnh được lưu vào MySQL. |
| Chiến dịch | Người bán tạo chiến dịch gồm tên, mô tả, thời gian bắt đầu/kết thúc và một hoặc nhiều sản phẩm, mỗi sản phẩm có tên, giá, tồn kho và ảnh thu nhỏ tùy chọn. |
| Sản phẩm trong chiến dịch | Bất kỳ ai cũng có thể xem danh sách sản phẩm của một chiến dịch. |
| Đơn hàng | Khách hàng đặt mua sản phẩm. Dịch vụ kiểm tra tồn kho, tính tổng tiền từ giá đã lưu, trừ tồn kho, lưu đơn hàng ở trạng thái `PENDING` và gọi cổng thanh toán trong cùng một giao dịch cơ sở dữ liệu. Ngoại lệ thanh toán khiến các thay đổi trong cơ sở dữ liệu bị hoàn tác, nhưng không thể đảo ngược một khoản thanh toán bên ngoài đã hoàn tất. |
| Thông báo | Khi tạo chiến dịch, hệ thống lên lịch một tác vụ trong bộ nhớ để chạy vào thời điểm `start_at`. Khi tác vụ chạy, việc gửi email “Flash Sale is starting now!” cho từng người dùng được *mô phỏng* bằng log. |
| Vận hành | Swagger UI, tài liệu OpenAPI JSON và endpoint kiểm tra sức khỏe qua Actuator. |

## Kiến trúc

![Sơ đồ kiến trúc](docs/architecture/architecture.jpg)

Hệ thống khi chạy gồm một tiến trình Spring Boot và ba dịch vụ phụ trợ:

| Thành phần | Vai trò |
| --- | --- |
| **Ứng dụng nguyên khối Spring Boot** (`:8080`) | REST API, bảo mật, nghiệp vụ và lập lịch. |
| **MySQL 8.4** | Một cơ sở dữ liệu duy nhất là `ecommerce`. Lược đồ được tạo bởi `infrastructure/database/init/01-schema.sql`; Hibernate chỉ *kiểm tra tính tương thích* của lược đồ (`ddl-auto: validate`). |
| **RustFS** (`:9000`, giao diện quản trị `:9001`) | Kho lưu trữ đối tượng tương thích S3. Bucket `item-images` được tự động tạo khi ứng dụng khởi động. |
| **Payment mock** (`:3001`) | Ứng dụng Express độc lập trong `payment-mock/`, mô phỏng nhà cung cấp dịch vụ thanh toán và lưu các khoản thanh toán trong bộ nhớ. |

Bên trong ứng dụng nguyên khối, yêu cầu đi qua các lớp quen thuộc:

1. **Bảo mật** — `JwtAuthFilter` đọc header `Authorization: Bearer …`, xác thực token HS256, tải thông tin người dùng và thiết lập `ROLE_SELLER` / `ROLE_CUSTOMER`. `SecurityConfig` khai báo các đường dẫn công khai; controller dùng `@PreAuthorize` để kiểm tra vai trò.
2. **Controller** (`controller/`) — các endpoint REST mỏng. Annotation OpenAPI được khai báo trong các interface `controller/docs/*Api`.
3. **Service** (`service/`) — xử lý nghiệp vụ qua `AuthService`, `FileService`, `FlashSaleService`, `SchedulerService`, `NotificationService`, `EmailService`.
4. **Adapter** — các repository Spring Data JPA (`repository/`), các dịch vụ S3 (`infrastructure/s3/`) và `PaymentClient` (`client/payment/`, dùng `WebClient` với thời gian chờ tối đa 5 giây).
5. **Xử lý xuyên suốt** — `GlobalExceptionHandler` chuyển ngoại lệ thành `ErrorResponse` thống nhất; JPA auditing điền `created_by` / `last_modified_by` từ ngữ cảnh bảo mật; Bean Validation kiểm tra các DTO đầu vào.

## Cơ sở dữ liệu

![Sơ đồ cơ sở dữ liệu](docs/database/database.png)

Gồm tám bảng, tất cả đều dùng chuỗi UUID làm khóa chính và kế thừa các cột theo dõi `created_at`, `created_by`, `last_modified_at`, `last_modified_by` từ `BaseEntity`:

| Bảng | Mục đích | Quan hệ chính |
| --- | --- | --- |
| `users` | Thông tin đăng nhập, một `role`, `refresh_token` mới nhất | — |
| `customers` | Hồ sơ khách hàng: số điện thoại, địa chỉ giao hàng | Khóa chính = `users.id` |
| `sellers` | Hồ sơ người bán: tên cửa hàng | Khóa chính = `users.id` |
| `files` | Thông tin mô tả ảnh và `object_key` trong S3 | `owner_id → sellers` |
| `campaigns` | Chiến dịch flash sale với `start_at` / `end_at` | `seller_id → sellers` |
| `campaign_items` | Sản phẩm bán trong chiến dịch: tên, giá, tồn kho, ảnh thu nhỏ | `campaign_id → campaigns`, `thumbnail_id → files` |
| `orders` | Đơn hàng của khách với `total_amount` và `status` | `customer_id → customers` |
| `order_items` | Sản phẩm và số lượng trong đơn hàng | `order_id → orders`, `campaign_item_id → campaign_items` |

## Công nghệ sử dụng

| Hạng mục | Công nghệ |
| --- | --- |
| Framework | Spring Boot 4.1.1 (Web MVC, Security, Data JPA, Validation, Actuator, WebFlux client) |
| Lưu trữ dữ liệu | Hibernate + MySQL 8.4 |
| Xác thực | JJWT 0.11.5, BCrypt |
| Lưu trữ đối tượng | AWS SDK for Java v2 (`s3`) kết nối tới RustFS |
| Tài liệu API | springdoc-openapi 3.1.0 |
| Mô phỏng thanh toán | Node.js ≥ 18, Express 5 |
| Đóng gói | Maven Wrapper, Docker, Docker Compose v2 |
| CI | GitHub Actions đóng gói, đẩy và ký image trên GHCR khi có thay đổi trên `main` |

## Hướng dẫn chạy

### Yêu cầu

- JDK 17
- Docker với Compose v2
- Node.js 18+ và npm để chạy payment mock
- `openssl` để tạo khóa JWT

Chạy các lệnh từ thư mục gốc của repository, trừ khi có chỉ dẫn khác.

### 1. Tạo tệp biến môi trường

`deploy/env/.env` được Git bỏ qua và chứa cấu hình cục bộ, bao gồm các giá trị bí mật. Tạo tệp từ mẫu:

```bash
cp deploy/env/.env.example deploy/env/.env
```

Sau đó điền các giá trị. Ví dụ cấu hình để chạy cục bộ:

```dotenv
RUSTFS_ACCESS_KEY=rustfsadmin
RUSTFS_SECRET_KEY=rustfsadmin
RUSTFS_REGION=us-east-1
RUSTFS_ENDPOINT=http://localhost:9000

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecommerce
MYSQL_ROOT_PASSWORD=rootpassword
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=rootpassword

JWT_SECRET_KEY='replace-with-a-generated-base64-key'
PAYMENT_GATEWAY_URL=http://localhost:3001
```

Tạo khóa JWT và dán kết quả vào `JWT_SECRET_KEY` trong tệp môi trường:

```bash
openssl rand -base64 32
```

### 2. Khởi động MySQL và RustFS

```bash
bash deploy/script/infra.sh
```

Script tạo Docker network `architecture_solution` nếu chưa có, rồi khởi động hai container từ `deploy/compose/compose-infra.yaml`. Trong lần khởi động **đầu tiên**, MySQL chạy `01-schema.sql` và `02-fake-init.sql`; nếu volume dữ liệu đã tồn tại, dữ liệu được giữ nguyên và các script này không chạy lại.

### 3. Khởi động payment mock

Trong terminal thứ hai:

```bash
cd payment-mock
npm ci
npm start          # lắng nghe tại :3001, có thể đổi bằng PORT=...
```

### 4. Khởi động backend

Xuất các biến môi trường vào shell trước khi chạy Spring Boot:

```bash
set -a
source deploy/env/.env
set +a
./mvnw spring-boot:run
```

MySQL và RustFS phải sẵn sàng kết nối khi khởi động vì ứng dụng kiểm tra lược đồ và tạo bucket ngay lúc này.

### 5. Kiểm tra

| Dịch vụ | URL |
| --- | --- |
| Kiểm tra sức khỏe backend | http://localhost:8080/actuator/health |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Kiểm tra sức khỏe payment mock | http://localhost:3001/health |
| Giao diện quản trị RustFS | http://localhost:9001 |
| MySQL | `localhost:3306`, cơ sở dữ liệu `ecommerce` |

## Tài liệu API

| Phương thức | Endpoint | Quyền truy cập | Mô tả |
| --- | --- | --- | --- |
| POST | `/api/v1/auth/register` | Công khai | Đăng ký `SELLER` (cần `shop_name`) hoặc `CUSTOMER` (`phone`, `shipping_address` là tùy chọn) |
| POST | `/api/v1/auth/login` | Công khai | Trả về access token và refresh token |
| POST | `/api/v1/auth/refresh` | Công khai (cần refresh token hợp lệ) | Cấp cặp token mới thay cho cặp cũ |
| POST | `/api/v1/files/item-images` | `SELLER` | Tải ảnh qua trường multipart `file`; chỉ nhận JPEG/PNG |
| POST | `/api/v1/flash-sales/campaign/create` | `SELLER` | Tạo chiến dịch cùng các sản phẩm |
| GET | `/api/v1/flash-sales/campaign/{campaignId}/items` | Công khai | Liệt kê sản phẩm của chiến dịch |
| POST | `/api/v1/flash-sales/campaign/order` | `CUSTOMER` | Tạo đơn hàng và thực hiện thanh toán; chiến dịch phải đang diễn ra |
| GET | `/actuator/health` | Công khai | Kiểm tra trạng thái hoạt động |
| GET | `/swagger-ui/index.html`, `/v3/api-docs` | Công khai | Tài liệu API |

JSON trong yêu cầu và phản hồi dùng `snake_case`. Một số quy tắc kiểm tra dữ liệu cần biết:

- Đăng ký: bắt buộc có `email` (địa chỉ hợp lệ), `username` (≤ 50 ký tự), `password`, `retype_password`, `role`; `phone` phải gồm 9–15 chữ số.
- Chiến dịch: cần `name`, `start_at < end_at` và danh sách `items` không rỗng; mỗi sản phẩm cần `name`, `price > 0`, `stock > 0`; `thumbnail_id` là tùy chọn và phải thuộc người bán.
- Đơn hàng: danh sách `items` không rỗng; mỗi mục cần `campaign_item_id` và `quantity > 0`.

Thao tác tạo đơn hàng **chưa có tính lũy đẳng (idempotency)**: endpoint không đọc `Idempotency-Key` và không lưu thông tin để loại bỏ yêu cầu trùng. Nhấn thanh toán hai lần hoặc gửi lại yêu cầu sau khi mất phản hồi có thể tạo thêm đơn hàng, trừ tồn kho lần nữa và yêu cầu thanh toán thêm lần nữa. Chỉ gửi header này không mang lại khả năng chống trùng. Xem vấn đề số 9 trong [Chín vấn đề được cố ý giữ lại](#chín-vấn-đề-được-cố-ý-giữ-lại).

### Cấu trúc phản hồi

Thành công:

```json
{
  "status_code": <mã trạng thái HTTP>,
  "message": "<thông báo cho người đọc>",
  "data": <dữ liệu: đối tượng, mảng hoặc null>
}
```

Lỗi:

```json
{
  "status_code": <mã trạng thái HTTP>,
  "error": "<cụm từ mô tả trạng thái HTTP>",
  "message": "<mô tả lỗi>",
  "path": "<đường dẫn yêu cầu>",
  "timestamp": "<thời điểm theo ISO 8601>"
}
```

| HTTP | Trường hợp |
| --- | --- |
| 400 | Dữ liệu không hợp lệ, mật khẩu không khớp, hết hàng, ảnh không hợp lệ, `end_at` trước `start_at`, chiến dịch chưa bắt đầu hoặc đã kết thúc |
| 401 | Thiếu token, token không hợp lệ hoặc hết hạn, sai thông tin đăng nhập, refresh token không hợp lệ |
| 403 | Đã xác thực nhưng sai vai trò, hoặc thiếu hồ sơ người bán |
| 404 | Không tìm thấy chiến dịch, sản phẩm trong chiến dịch hoặc ảnh thu nhỏ |
| 409 | Tên đăng nhập/email đã tồn tại, hoặc vi phạm ràng buộc cơ sở dữ liệu |
| 500 | Lỗi lưu trữ hoặc trạng thái máy chủ ngoài dự kiến |
| 502 | Cổng thanh toán báo lỗi, hết thời gian chờ hoặc không thể kết nối |

### Payment mock

| Phương thức | Endpoint | Mô tả |
| --- | --- | --- |
| GET | `/health` | Kiểm tra sức khỏe dịch vụ |
| POST | `/payments` | Mô phỏng thanh toán cho khách hàng (`customer_id`, `total_amount`); header `Fault: true` buộc yêu cầu thất bại |

## Cấu trúc thư mục

```text
.
├── src/main/java/com/architecture/solution/
│   ├── annotation/          # Annotation @ValueOfEnum và trình kiểm tra
│   ├── client/payment/      # PaymentClient (WebClient) và các DTO
│   ├── config/              # Cấu hình bảo mật, WebClient, auditing, scheduler
│   ├── controller/          # REST controller; docs/ chứa interface OpenAPI
│   ├── dto/                 # Mô hình yêu cầu/phản hồi (JSON snake_case)
│   ├── entity/              # Entity JPA (BaseEntity chứa các cột theo dõi)
│   ├── enums/               # RoleName, OrderStatus, TokenType, ...
│   ├── exception/           # Ngoại lệ tùy chỉnh và GlobalExceptionHandler
│   ├── filter/              # JwtAuthFilter
│   ├── infrastructure/s3/   # Cấu hình S3, dịch vụ bucket/object, khởi tạo
│   ├── repository/          # Repository Spring Data JPA
│   ├── security/            # UserDetails, bộ xử lý lỗi 401/403
│   ├── service/             # Interface dịch vụ và impl/
│   └── util/                # JwtUtils, FileUtils, SecurityUtils, ErrorResponseUtils
├── src/main/resources/
│   └── application.yaml         # Được Git theo dõi: giá trị mặc định và biến môi trường
├── src/test/                    # Kiểm thử khởi tạo Spring context
├── infrastructure/database/init/
│   ├── 01-schema.sql            # Cơ sở dữ liệu, bảng, khóa ngoại
│   └── 02-fake-init.sql         # Người dùng và chiến dịch mẫu
├── payment-mock/                # Dịch vụ mô phỏng thanh toán bằng Express
├── deploy/
│   ├── compose/                 # compose-infra.yaml (MySQL + RustFS), compose-app.yaml
│   ├── env/                     # .env.example (được theo dõi), .env (không được theo dõi)
│   └── script/                  # infra.sh, deploy.sh
├── docs/                        # architecture.drawio, database.dbml, ảnh đã xuất
├── Dockerfile
└── .github/workflows/docker-publish.yml
```

## Đóng gói và triển khai

Chạy các lệnh từ thư mục gốc của repository.

### JAR

Yêu cầu JDK 17. Trước khi chạy, hoàn tất việc thiết lập dịch vụ và biến môi trường trong [Hướng dẫn chạy](#hướng-dẫn-chạy).

```bash
./mvnw clean package -DskipTests
set -a
source deploy/env/.env
set +a
java -jar target/solution-0.0.1-SNAPSHOT.jar
```

### Docker image

```bash
docker build -t architecture-solution:local .
```

### Docker Compose

Điền cấu hình vào `deploy/env/.env`. Dùng các URL sau khi chạy trong container:

```dotenv
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce
RUSTFS_ENDPOINT=http://rustfs:9000
```

Chạy payment mock riêng và đặt `PAYMENT_GATEWAY_URL` thành URL mà container backend có thể truy cập (`localhost` trỏ tới chính container đó).

```bash
bash deploy/script/deploy.sh
```

Script khởi động MySQL và RustFS, đợi các kiểm tra sức khỏe thành công, sau đó tải về và chạy `ghcr.io/binhphuc/architecturesolution:latest`. Image Docker được đóng gói cục bộ không được sử dụng trong bước này.

Kiểm tra tại: http://localhost:8080/actuator/health

### CI

Đẩy thay đổi lên `main` → đóng gói, công bố và ký image trên GHCR. Khi chạy workflow thủ công, image chỉ được công bố nếu bật `push_image`. Chạy `deploy.sh` trên máy triển khai để triển khai ứng dụng.

## Chín vấn đề được cố ý giữ lại

| # | Vấn đề | Vị trí hiện tại | Hệ quả |
| --- | --- | --- | --- |
| 1 | **Tranh chấp đồng thời / bán vượt tồn kho** | `FlashSaleServiceImpl.createOrder`: `findById` → `stock < quantity` → `setStock(stock - quantity)` trong một `@Transactional` thông thường | Hai khách hàng cùng mua sản phẩm cuối: cả hai đều thấy tồn kho bằng 1, cùng vượt qua bước kiểm tra và đặt hàng thành công. Cửa hàng bán nhiều hơn số hàng thực có. |
| 2 | **Nút thắt từ API bên thứ ba** | `PaymentClientImpl.processPayment` gọi `WebClient … .block()` với thời gian chờ tối đa 5 giây, ngay trên luồng xử lý yêu cầu | Mỗi đơn hàng đều phải đợi nhà cung cấp thanh toán. Khi dịch vụ này chậm hoặc ngừng hoạt động, mọi đơn hàng đều chậm hoặc thất bại, và toàn bộ ứng dụng có thể không còn khả năng phục vụ yêu cầu. |
| 3 | **Thông báo dồn dập** | `SchedulerServiceImpl`: khi chiến dịch bắt đầu, tải **toàn bộ** người dùng rồi gửi thông báo cho từng người bằng vòng lặp trên một luồng | Ứng dụng cố thông báo cho tất cả người dùng cùng lúc. Với lượng người dùng lớn, việc này gây quá tải cho ứng dụng và nhà cung cấp email/push. |
| 4 | **Xung đột tác vụ lập lịch khi mở rộng ngang** | `SchedulerConfig` dùng `SimpleAsyncTaskScheduler`, lưu tác vụ trong bộ nhớ JVM, nên mỗi instance có bộ lập lịch riêng | Tác vụ nằm trong bộ nhớ của một instance.<br>1. Khởi động lại instance thì mất tác vụ.<br>2. Chạy nhiều instance có thể khiến tác vụ thực thi nhiều lần. |
| 5 | **Xử lý đồng bộ chậm** | `createOrder` thực hiện mọi bước (kiểm tra tồn kho, ghi cơ sở dữ liệu, gọi thanh toán) ngay trong yêu cầu HTTP và chỉ phản hồi khi tất cả hoàn tất | Khách hàng phải chờ bước chậm nhất. Ngay cả khi cổng thanh toán nhanh, thời gian phản hồi vẫn tăng theo từng bước được thêm vào, và không có bước nào được hoãn để xử lý sau. |
| 6 | **Ghi vào hai hệ thống (dual write)** | 1. `createOrder` ghi vào MySQL rồi gọi dịch vụ thanh toán trong cùng một giao dịch.<br>2. `FileServiceImpl.uploadItemImage` ghi vào RustFS rồi vào MySQL. | Ghi vào hai hệ thống trong một thao tác nhưng không có cách hoàn tác đồng thời cả hai.<br>1. Khách có thể bị trừ tiền trong khi đơn hàng bị hoàn tác.<br>2. Tệp có thể đã được lưu dù không ghi được bản ghi tương ứng trong cơ sở dữ liệu. |
| 7 | **Phụ thuộc chặt** | `NotificationServiceImpl` gắn trực tiếp với một kênh: nhận `EmailService` qua dependency injection và gọi thẳng dịch vụ này | Thêm kênh mới (ví dụ `SmsService`) đòi hỏi sửa `NotificationServiceImpl` thay vì chỉ bổ sung bộ gửi mới. Mỗi kênh mới đều làm thay đổi mã hiện có. |
| 8 | **Không có bộ nhớ đệm** | 1. `getItemsByCampaignId` và các lần tìm từng sản phẩm trong `createOrder` đều truy vấn MySQL mỗi lần gọi.<br>2. Không có cơ chế cache (không Redis, không Spring Cache). | Mỗi yêu cầu lại đọc cùng dữ liệu chiến dịch từ MySQL. Trong flash sale, hàng nghìn lượt đọc giống nhau đồng thời dồn vào cơ sở dữ liệu. |
| 9 | **Không có tính lũy đẳng (idempotency)** | `createOrder` không có khóa idempotency hay kiểm tra yêu cầu trùng | Nhấn hai lần hoặc gửi lại yêu cầu có thể tạo đơn hàng trùng, trừ tồn kho lần nữa và thu tiền khách hàng nhiều lần. |
