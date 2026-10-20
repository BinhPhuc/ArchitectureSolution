# Refactor sang Ecommerce System tập trung Flash Sale

## Mục đích

Chuyển ArchitectureSolution từ bài toán tuyển dụng sang thương mại điện tử tập trung chương trình flash sale. Tái sử dụng các entity cần thiết từ `/home/binhphuc/Documents/workspace/EcommerceMicroservices`, giữ kiến trúc monolithic và một database MySQL `ecommerce`.

## Công việc và tiến độ

| Công việc | Tiến độ |
| --- | --- |
| Đọc luồng flash sale và các phụ thuộc đồng bộ/bất đồng bộ trong source | Hoàn thành |
| Bỏ entity tuyển dụng và các controller/service/repository/DTO phụ thuộc | Hoàn thành |
| Thêm Customer/Seller, cập nhật role và luồng đăng ký | Hoàn thành theo mapping hồ sơ hiện có |
| Copy và điều chỉnh entity, enum cần cho flash sale | Hoàn thành |
| Đơn giản hóa: bỏ ProductVariant/Inventory, chuyển giá và tồn kho vào Product | Hoàn thành |
| Khôi phục upload file và bucket initializer cho ảnh sản phẩm của seller | Hoàn thành |
| Xóa OrderOutbox, OrderOutboxItem, ProcessedEvent và bảng/khóa ngoại tương ứng | Hoàn thành |
| Xóa Category, categoryId trong Product và schema/dữ liệu mẫu liên quan | Hoàn thành |
| Gộp schema, thêm khóa ngoại, đồng bộ cột và ràng buộc với entity | Hoàn thành |
| Viết lại seed role và dữ liệu mẫu Ecommerce | Hoàn thành |
| Cập nhật cấu hình database và README | Hoàn thành |
| Build, đối chiếu Hibernate/schema, kiểm tra dữ liệu và ràng buộc | Đạt kiểm tra độc lập; xem giới hạn bên dưới |
| Chạy script và toàn bộ ứng dụng trên MySQL thật | Chưa xác nhận do môi trường database chưa sẵn sàng |

## Kết quả khảo sát source

`flash-sale-service` gọi HTTP tới `product-service` để lấy sản phẩm/biến thể và `inventory-service` để kiểm tra tồn kho. Quartz trong chính flash-sale-service lên lịch prewarm; Kafka phát sự kiện prewarm tới product/inventory và tạo đơn tới order. Order tạo outbox và các variant tương ứng để yêu cầu payment xử lý. Payment thất bại gửi sự kiện cho order và flash sale để đổi trạng thái đơn, hoàn lại tồn kho. Không có lời gọi tới schedule-service trong luồng flash sale này.

| Nguồn | Database nguồn | Entity hiện tại và lý do |
| --- | --- | --- |
| ArchitectureSolution | hire_me | User, Role, UserRole, BaseEntity phục vụ xác thực, phân quyền và audit |
| Mới | ecommerce | Customer: phone, shippingAddress; Seller: shopName |
| ArchitectureSolution | ecommerce | Khôi phục File, FileStatus, FileRepository, FileUploadResponse để lưu metadata ảnh do seller upload |
| product-service | products | Product; giá và số lượng tồn kho lưu trực tiếp trong Product theo yêu cầu đơn giản hóa |
| inventory-service | inventories | Không giữ entity Inventory; không còn bảng tồn kho riêng |
| flash-sale-service | flash_sales | Campaign, CampaignItem |
| order-service | orders | Order, OrderItem tham chiếu trực tiếp Product |
| payment-service và inventory-service | payments, inventories | Không giữ entity xử lý sự kiện |

Không lấy Review, ProductStatistic, Collection, CollectionProduct, ProductImage, SellerOrder, OrderSnapshot, OrderItemSnapshot, các entity favorite/media hoặc entity user-service. Chúng không được truy cập trong luồng flash sale đã khảo sát; SellerOrder và trường sellerOrderId còn được source đánh dấu deprecated. Hồ sơ người dùng tiếp tục sử dụng xác thực của ArchitectureSolution.

## Convention enum

Toàn bộ enum dùng API chuẩn Java: `name()` lấy định danh chuỗi, `EnumType.valueOf(value)` chuyển định danh chuỗi về enum, `==` so sánh enum và `values()` lấy danh sách hằng. Chuỗi đầu vào phải khớp chính xác tên hằng; `valueOf` ném IllegalArgumentException nếu không hợp lệ. Không thêm interface chung, helper hoặc getter lặp lại tên hằng.

ProductStatus đã bỏ trường status, constructor và getStatus vì giá trị trùng hoàn toàn tên hằng. JWT tạo claim type bằng TokenType.name(), thống nhất với phần kiểm tra token. Kiểm tra `./mvnw -q -Dtest=FileServiceImplTests clean package` thành công (5 test đạt), `git diff --check` đạt.

Các enum khai báo mỗi hằng trên một dòng; giữ nguyên tên hằng để tương thích JSON, JWT và JPA EnumType.STRING. AuthErrorType giữ message bất biến và getMessage() để trả thông báo lỗi; định danh enum vẫn lấy qua name().

## Convention và mapping

Giữ package `com.architecture.solution.entity`, Lombok, UUID dạng String dài 36 ký tự, BaseEntity audit, tên bảng/cột snake_case và cách mapping quan hệ bằng trường ID. Khóa ngoại được khai báo trong SQL như dự án hiện tại. Không thêm comment vào code hoặc script SQL.

Candidate/Recruiter cũ thực tế extends BaseEntity, liên kết User qua user_id, không trực tiếp extends User. Customer/Seller hiện giữ đúng convention này, hỗ trợ một tài khoản có nhiều vai trò. Đây là giả định triển khai cho yêu cầu “như hiện tại”; chưa chuyển sang JPA inheritance trực tiếp. Đã hỏi người dùng về điểm khác biệt này; khi chưa có trả lời, giữ mapping hiện tại.

Role mới gồm ADMIN, CUSTOMER, SELLER. RegisterRequest thay company_name bằng shop_name, bổ sung shipping_address; đăng ký SELLER bắt buộc shop_name. Giữ cơ chế đăng nhập và refresh token hiện có.

Các entity nguồn được copy và điều chỉnh sang monolith. Sau yêu cầu đơn giản hóa, đã xóa ProductVariant và Inventory; Product giữ price (BigDecimal, precision 19/scale 2) và stock (Long, mặc định 0). Không giữ SKU, attributes hoặc reserveStock. CampaignItem và OrderItem bỏ variantId; CampaignItem có unique (campaign_id, product_id). SQL, khóa ngoại và dữ liệu mẫu được cập nhật tương ứng. CampaignItem.stock tiếp tục là số lượng dành cho chiến dịch, còn Product.stock là số lượng sản phẩm hiện tại.

Theo yêu cầu đơn giản hóa tiếp theo, đã xóa OrderOutbox, OrderOutboxItem và ProcessedEvent, cùng các bảng order_outboxes, order_outbox_items, processed_events, index và khóa ngoại tương ứng. Không có service/repository đang phụ thuộc ba entity này. Giữ nguyên idempotencyKey trên Order vì đây là thuộc tính riêng của đơn hàng.

## Kiểu ảnh được hỗ trợ

ImageType tập trung các định dạng JPEG/PNG và MIME type tương ứng, sử dụng hằng MediaType có sẵn của Spring. FileUtils tra MIME type qua ImageType.values(), đối chiếu định dạng nội dung thực tế bằng ImageType.name(); không còn chuỗi MIME hoặc định dạng ảnh hardcode trong utility. contentType là metadata của enum, tương tự message trong AuthErrorType.

## Khôi phục upload ảnh sản phẩm

Khôi phục FileService/FileServiceImpl, FileUtils, FileRepository, File, FileStatus, FileUploadResponse, BucketConstant và BucketInitializer từ chức năng cũ; thay nghiệp vụ CV bằng ảnh sản phẩm. Giữ S3ObjectService và S3BucketService hiện có.

- `POST /api/v1/files/product-images`: multipart field `file`, Bearer token vai trò SELLER và hồ sơ seller chưa bị xóa.
- Nhận JPEG/PNG; kiểm tra định dạng thực tế và khả năng đọc nội dung ảnh. Giữ giới hạn multipart 10 MB/file từ cấu hình hiện tại.
- Khởi tạo bucket `ecommerce-product-images` lúc startup nếu chưa tồn tại.
- Upload object theo key `<sellerId>/<UUID>`, lưu metadata READY vào bảng files với owner_id tham chiếu sellers.user_id, trả về ID file và metadata.
- Chưa gắn file vào Product hoặc thay đổi API đăng bán; phần đó chờ bước triển khai nghiệp vụ sản phẩm. Không khôi phục các entity tuyển dụng hoặc validation PDF/CV.
- Giữ hành vi upload S3 trước khi lưu metadata như triển khai cũ; transaction database không tự hoàn tác object S3 nếu bước lưu metadata thất bại.

## SQL và dữ liệu mẫu

Ba script hiện có đã được viết lại cho database ecommerce:

1. 01-schema.sql: toàn bộ bảng, unique/index và khóa ngoại trong một database; cột audit khớp BaseEntity.
2. 02-seed-roles.sql: ADMIN, SELLER, CUSTOMER.
3. 03-fake-init.sql: tài khoản admin, seller01, customer01; product có giá 1.000.000 và stock 100, campaign và campaign item có giá flash sale 700.000 và stock 20 hợp lệ. Campaign bắt đầu sau thời điểm seed một ngày; không tạo đơn giả cho campaign chưa bắt đầu.

Tài khoản phát triển: admin / Admin@123; seller01 và customer01 / Demo@123. Các script hướng tới database mới, không chuyển đổi dữ liệu hire_me cũ. Docker chỉ tự chạy init script khi khởi tạo volume MySQL mới. Cập nhật datasource tới ecommerce trước khi chạy ứng dụng; không tự xóa volume hoặc dữ liệu cũ.

## Kiểm chứng

Kết quả kiểm tra lại sau khi bỏ ProductVariant/Inventory:

- `./mvnw -q clean package -DskipTests`: thành công ở bước đơn giản hóa.
- Khôi phục upload: 5 unit test kiểm tra metadata và upload ảnh hợp lệ, seller không tồn tại/đã xóa, file không đọc được, định dạng không hợp lệ và khởi tạo bucket đã đạt. Build package có chạy nhóm test này đã thành công.
- Ba script SQL chạy thành công trên H2 MySQL mode sau điều chỉnh cú pháp tạo database, transaction và cộng thời gian chỉ trong bộ kiểm tra tạm.
- Hibernate schema validation với MySQLDialect: khớp cả 11 entity hiện tại sau khi xóa Category.
- Dữ liệu mẫu liên kết trực tiếp CampaignItem → Product → Seller: giá và số lượng sản phẩm/chiến dịch đúng; thời gian campaign hợp lệ.
- `Product.builder().build().getStock()` mặc định bằng 0. Các kiểm tra outbox và chống trùng sự kiện của phiên bản trước không còn áp dụng.
- Không còn tham chiếu variant/inventory hoặc ba entity outbox/processed event trong Java và SQL; `git diff --check` đạt.
- Sau khi xóa ba entity: `./mvnw -q -Dtest=FileServiceImplTests clean package` thành công, 5 test upload/bucket đạt; cả ba script SQL và Hibernate schema validation trên H2 MySQL mode đạt.

Giới hạn môi trường ghi nhận ở lần trước: contextLoads không khởi tạo được datasource/JDBC metadata và Docker daemon không kết nối được. Kiểm tra H2 không thay thế kiểm chứng trên MySQL thật hoặc kiểm thử API toàn ứng dụng.

Kiểm tra sau khi xóa Category: build `./mvnw -q -Dtest=FileServiceImplTests clean package` thành công, 5 test upload/bucket đạt; ba script SQL và Hibernate schema validation trên H2 MySQL mode đạt. Không còn tham chiếu Category/category_id trong Java hoặc SQL. Chưa chạy kiểm chứng trên MySQL thật.

## Phạm vi tiếp theo

Chờ hướng dẫn tiếp theo để triển khai service/controller/repository nghiệp vụ Ecommerce và flash sale. Bước này mới chuẩn bị mô hình dữ liệu và giữ xác thực hoạt động ở mức code/build; chưa copy Kafka, Redis, Quartz, HTTP client hoặc toàn bộ business logic từ hệ thống microservice. Mô hình hiện tại không còn outbox hoặc bảng xử lý sự kiện.

Các sơ đồ ảnh cũ trong docs chưa được vẽ lại. Upload và bucket initializer đã được khôi phục cho ảnh sản phẩm; chưa kiểm chứng upload hoặc khởi tạo bucket trên S3/RustFS thật trong môi trường này.
