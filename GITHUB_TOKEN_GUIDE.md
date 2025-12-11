# Hướng dẫn tạo Personal Access Token để push code lên GitHub

## Các bước:

1. ✅ **Note**: Đã điền "Facility Helpdesk Project" - OK!

2. ✅ **Expiration**: Đã chọn "30 days" - OK!

3. ⚠️ **Select scopes**: **QUAN TRỌNG** - Cần tích vào checkbox "repo"
   - Tìm checkbox có label "repo" với mô tả "Full control of private repositories"
   - **Tích vào checkbox này** để có quyền push code

4. Sau khi tích "repo", scroll xuống và click nút **"Generate token"** (màu xanh lá)

5. **Copy token ngay lập tức** (token chỉ hiển thị 1 lần, không thể xem lại)

6. Quay lại terminal và chạy:
   ```bash
   git push -u origin main
   ```
   - Username: `Thongcao`
   - Password: Dán token vừa copy (KHÔNG phải mật khẩu GitHub)

## Lưu ý:
- Token sẽ hết hạn sau 30 ngày
- Giữ token an toàn, không chia sẻ công khai
- Nếu mất token, phải tạo token mới




