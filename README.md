1.Yêu cầu hệ thống
Node.js (phiên bản 16.x hoặc cao hơn)

npm hoặc pnpm

Cơ sở dữ liệu (MySQL)

Angular CLI nếu phía khách hàng sử dụng Angular

Spring Boot nếu phía máy chủ sử dụng Java Spring

2.Tải về mã nguồn
git clone https://github.com/Cuyen510/Microservice

3.Cấu hình cơ sở dữ liệu
Mở file cấu hình tại src/main/resources/application.yml của từng service
Cập nhật thông tin cơ sở dữ liêu

spring:
  datasource:
    url: jdbc:mysql://your-database-ip:port/database-name
    username: your-username
    password: your-password

4.Cài đặt các dependency
mvn clean install

5.Chạy phía máy chủ (backend)
java -jar target/userservice.jar
java -jar target/orderservice.jar
java -jar target/productservice.jar
java -jar target/apigateway.jar

Sau khi chạy, dịch vụ API sẽ khả dụng tại: http://localhost:9000

6.Cài đặt frontend
cd frontend
npm install
ng serve

Sau khi hoàn tất, truy cập tại: http://localhost:4200




