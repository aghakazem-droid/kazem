# راهنمای ساخت APK با GitHub Actions

## مراحل کار

### ۱. ساخت حساب GitHub
اگر ندارید به [github.com](https://github.com) بروید و ثبت‌نام کنید.

### ۲. ساخت Repository جدید
- روی **+** بالای صفحه کلیک کنید → **New repository**
- نام بگذارید: `HermesQCPlan`
- گزینه **Private** را انتخاب کنید (پروژه خصوصی بماند)
- روی **Create repository** کلیک کنید

### ۳. آپلود فایل‌های پروژه
دو روش دارید:

#### روش الف — آپلود مستقیم در مرورگر (ساده‌تر)
1. در صفحه repository روی **Add file → Upload files** کلیک کنید
2. پوشه `HermesQCPlan` را باز کنید
3. **همه فایل‌ها و پوشه‌ها** را drag & drop کنید
4. روی **Commit changes** کلیک کنید

#### روش ب — با Git (اگر Git نصب دارید)
```bash
cd HermesQCPlan
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/USERNAME/HermesQCPlan.git
git push -u origin main
```

### ۴. اجرای خودکار Build
- بعد از آپلود، GitHub Actions به صورت **خودکار** شروع به build می‌کند
- برای دیدن وضعیت: روی تب **Actions** در repository کلیک کنید
- حدود **۵ تا ۱۰ دقیقه** طول می‌کشد

### ۵. دانلود APK
1. بعد از اتمام build، روی **Actions** بروید
2. آخرین run را باز کنید
3. در پایین صفحه بخش **Artifacts** را ببینید
4. روی **HermesQCPlan-debug** کلیک کنید → فایل zip دانلود می‌شود
5. zip را باز کنید → فایل `app-debug.apk` داخل آن است

### ۶. نصب APK روی گوشی
1. فایل APK را به گوشی منتقل کنید
2. در تنظیمات گوشی: **نصب از منابع ناشناس** را فعال کنید
3. روی فایل APK ضربه بزنید و نصب کنید

---

## نکات مهم
- فایل `.github/workflows/build-apk.yml` باید در پروژه آپلود شود (همین فایل موجود است)
- هر بار که کد را تغییر دهید و push کنید، APK جدید ساخته می‌شود
- APK ساخته شده **debug** است و برای تست کافی است
- برای انتشار در Google Play باید APK را **sign** (امضا) کنید

---

## اگر build شکست خورد
به تب **Actions** بروید، روی run کلیک کنید و خطاها را ببینید.
می‌توانید متن خطا را برای من بفرستید تا کمک کنم.
