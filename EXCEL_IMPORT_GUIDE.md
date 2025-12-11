# Hướng Dẫn Import User Từ File Excel

## Tổng Quan

Chức năng import user từ file Excel cho phép Admin tạo nhiều user cùng lúc một cách nhanh chóng, thay vì phải nhập từng user một.

## Cấu Trúc File Excel

File Excel phải có định dạng `.xlsx` hoặc `.xls` với các cột sau:

### Cột Bắt Buộc (Required)

1. **Email** - Địa chỉ email của user (phải unique, không trùng lặp)
2. **Password** - Mật khẩu của user (lưu dạng plain text)
3. **Full Name** - Họ và tên đầy đủ của user
4. **Role** - Vai trò của user: `STUDENT`, `STAFF`, hoặc `ADMIN` (không phân biệt hoa thường)

### Cột Tùy Chọn (Optional)

#### Cho STUDENT:
- **Student Code** - Mã số sinh viên (phải unique nếu có)
- **Class Name** - Tên lớp học (mặc định: "N/A" nếu để trống)

#### Cho STAFF:
- **Position** - Chức vụ (mặc định: "Staff" nếu để trống)
- **Department Name** - Tên phòng ban (hiện tại chưa được sử dụng, có thể thêm trong tương lai)

## Ví Dụ File Excel

### Cấu Trúc Header (Dòng 1):

| Email | Password | Full Name | Role | Student Code | Class Name | Position | Department Name |
|-------|----------|-----------|------|--------------|------------|----------|------------------|
| student1@fpt.edu.vn | 12345 | Nguyễn Văn A | STUDENT | SE12345 | SE1701 | | |
| student2@fpt.edu.vn | 12345 | Trần Thị B | STUDENT | SE12346 | SE1702 | | |
| staff1@fpt.edu.vn | 12345 | Lê Văn C | STAFF | | | IT Support | IT Department |
| admin1@fpt.edu.vn | 12345 | Phạm Thị D | ADMIN | | | | |

### Lưu Ý:

1. **Dòng đầu tiên phải là header** với tên cột chính xác (không phân biệt hoa thường)
2. **Email phải unique** - nếu email đã tồn tại, dòng đó sẽ bị bỏ qua
3. **Role phải là một trong:** `STUDENT`, `STAFF`, hoặc `ADMIN`
4. **Student Code** (nếu có) phải unique cho STUDENT
5. Các cột không bắt buộc có thể để trống

## Cách Sử Dụng

1. Đăng nhập với tài khoản **ADMIN**
2. Vào trang **User Management** (`/admin/users`)
3. Tìm phần **"Import Users from Excel"**
4. **Tải file template mẫu** (khuyến nghị):
   - Click nút **"📥 Download Excel Template"**
   - File `user_import_template.xlsx` sẽ được tải về với header và dữ liệu mẫu
   - Mở file và điền thông tin user của bạn
5. Chọn file Excel đã điền (`.xlsx` hoặc `.xls`)
6. Click nút **"Import Excel"**
7. Hệ thống sẽ hiển thị kết quả:
   - Số lượng user import thành công
   - Số lượng user import thất bại và lý do (nếu có)

## Xử Lý Lỗi

Hệ thống sẽ báo lỗi và bỏ qua các dòng có vấn đề:

- Email đã tồn tại
- Email, Password, Full Name, hoặc Role bị thiếu
- Role không hợp lệ
- Student Code trùng lặp (cho STUDENT)
- Định dạng file không đúng (không phải .xlsx hoặc .xls)

## Giới Hạn

- Kích thước file tối đa: **10MB**
- Không giới hạn số lượng user trong một file (nhưng nên chia nhỏ nếu quá nhiều để dễ xử lý lỗi)

## Ví Dụ File Excel Mẫu

Bạn có thể tạo file Excel với cấu trúc như sau:

```
Email                  | Password | Full Name      | Role    | Student Code | Class Name | Position
-----------------------|----------|----------------|---------|--------------|------------|----------
student1@fpt.edu.vn   | 12345    | Nguyễn Văn A   | STUDENT | SE12345      | SE1701     |
student2@fpt.edu.vn   | 12345    | Trần Thị B     | STUDENT | SE12346      | SE1702     |
staff1@fpt.edu.vn     | 12345    | Lê Văn C       | STAFF   |              |            | IT Support
admin2@fpt.edu.vn     | 12345    | Phạm Thị D     | ADMIN   |              |            |
```

## Lưu Ý Bảo Mật

- Mật khẩu được lưu dạng **plain text** (không mã hóa) - chỉ dùng cho môi trường development
- Trong production, nên mã hóa mật khẩu trước khi import hoặc yêu cầu user đổi mật khẩu sau lần đăng nhập đầu tiên

