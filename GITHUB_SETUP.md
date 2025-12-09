# Hướng dẫn đưa project lên GitHub

## Bước 1: Tạo repository trên GitHub

1. Đăng nhập vào GitHub: https://github.com
2. Click nút **"New"** hoặc **"+"** → **"New repository"**
3. Điền thông tin:
   - **Repository name**: `FacilityFeedbackAndHelpdeskRequestSystem` (hoặc tên bạn muốn)
   - **Description**: Facility Feedback & Helpdesk Request System - Spring Boot Application
   - **Visibility**: Public hoặc Private (tùy bạn)
   - **KHÔNG** tích vào "Initialize this repository with a README" (vì project đã có README)
4. Click **"Create repository"**

## Bước 2: Kết nối local repository với GitHub

Sau khi tạo repository, GitHub sẽ hiển thị các lệnh. Chạy các lệnh sau trong terminal:

```bash
# Đã chạy: git init
# Đã chạy: git add .

# Commit code
git commit -m "Initial commit: Facility Feedback & Helpdesk Request System"

# Thêm remote repository (thay YOUR_USERNAME bằng username GitHub của bạn)
git remote add origin https://github.com/YOUR_USERNAME/FacilityFeedbackAndHelpdeskRequestSystem.git

# Đổi tên branch thành main (nếu cần)
git branch -M main

# Push code lên GitHub
git push -u origin main
```

## Bước 3: Xác thực với GitHub

Khi push, GitHub có thể yêu cầu xác thực:
- **Personal Access Token (PAT)**: Nếu dùng HTTPS
- **SSH Key**: Nếu dùng SSH

### Tạo Personal Access Token (nếu chưa có):
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token (classic)
3. Chọn quyền: `repo` (full control)
4. Copy token và dùng khi push

## Lưu ý quan trọng

✅ **Đã được bảo vệ**:
- File `application.properties` đã được thêm vào `.gitignore` (không commit password)
- File `application.properties.example` đã được commit (template không có password thật)

⚠️ **Sau khi clone project**:
1. Copy `application.properties.example` → `application.properties`
2. Cập nhật database credentials trong `application.properties`

## Các lệnh Git thường dùng

```bash
# Xem trạng thái
git status

# Thêm file mới
git add <file>

# Commit thay đổi
git commit -m "Mô tả thay đổi"

# Push lên GitHub
git push

# Pull từ GitHub
git pull

# Xem lịch sử commit
git log
```

