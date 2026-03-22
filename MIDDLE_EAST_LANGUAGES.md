# 中東語言支持文檔

## ✅ 已完成

### 📊 支持統計

| 語系 | 語言數量 | Web UI | 狀態 |
|------|---------|--------|------|
| **中東語言** ⭐ | 4 | ✅ 是 | 完成 |
| **斯拉夫語系** | 12 | ✅ 是 | 完成 |
| **拉丁語系** | 6 | ✅ 是 | 完成 |
| **中文** | 2 | ✅ 是 | 完成 |
| **英語系** | 3 | ✅ 是 | 完成 |
| **亞洲語言** | 10 | ✅ 是 | 完成 |
| **其他語言** | 6 | ✅ 是 | 完成 |
| **總計** | **43+** | ✅ 是 | ✅ |

---

## 🕌 中東語言（4 種語言）

### 閃含語系（閃米特語族）（2 種）

| 語言 | 代码 | 使用地區 | 人口 | 字母系統 |
|------|------|---------|------|---------|
| **阿拉伯文** | `arabic` | 中東、北非、阿拉伯半島 | 3.1 億 | 阿拉伯字母（RTL）|
| **希伯來文** | `he` | 以色列、猶太社區 | 900 萬 | 希伯來字母（RTL）|

### 印歐語系（印度-伊朗語族）（2 種）

| 語言 | 代码 | 使用地區 | 人口 | 字母系統 |
|------|------|---------|------|---------|
| **波斯文** | `fa` | 伊朗、阿富汗、塔吉克斯坦 | 1.1 億 | 阿拉伯字母變體（RTL）|
| **烏爾都文** | `ur` | 巴基斯坦、印度 | 1.7 億 | 阿拉伯字母變體（RTL）|

---

## 🔤 字母系統

### 阿拉伯字母（Arabic Script）

**使用語言：**
- 阿拉伯文
- 波斯文（添加字母：پ, چ, ژ, گ）
- 烏爾都文（添加更多字母）

**特點：**
- ✅ 從右到左書寫（RTL）
- ✅ 字母連寫（Ligature）
- ✅ 字母形狀根據位置變化（獨立、詞首、詞中、詞尾）
- ✅ 母音符號可選（Diacritics）

### 希伯來字母（Hebrew Script）

**使用語言：**
- 希伯來文

**特點：**
- ✅ 從右到左書寫（RTL）
- ✅ 輔音字母為主
- ✅ 母音符號可選（Niqqud）

---

## 💻 使用示例

### Python

```python
import requests

BASE_URL = "http://localhost:8000"

# 1. 阿拉伯文 OCR
ocr_response = requests.post(f"{BASE_URL}/api/ocr", json={
    "image_ids": ["image-id-1"],
    "language": "arabic"
})
print("阿拉伯文识别结果:", ocr_response.json())

# 2. 波斯文 OCR
ocr_response = requests.post(f"{BASE_URL}/api/ocr", json={
    "image_ids": ["image-id-2"],
    "language": "fa"
})
print("波斯文识别结果:", ocr_response.json())

# 3. 希伯來文 OCR
ocr_response = requests.post(f"{BASE_URL}/api/ocr", json={
    "image_ids": ["image-id-3"],
    "language": "he"
})
print("希伯來文识别结果:", ocr_response.json())

# 4. 烏爾都文 OCR
ocr_response = requests.post(f"{BASE_URL}/api/ocr", json={
    "image_ids": ["image-id-4"],
    "language": "ur"
})
print("烏爾都文识别结果:", ocr_response.json())
```

### cURL

```bash
# 阿拉伯文
curl -X POST http://localhost:8000/api/ocr \
  -H "Content-Type: application/json" \
  -d '{"image_ids": ["test-id"], "language": "arabic"}'

# 波斯文
curl -X POST http://localhost:8000/api/ocr \
  -H "Content-Type: application/json" \
  -d '{"image_ids": ["test-id"], "language": "fa"}'

# 希伯來文
curl -X POST http://localhost:8000/api/ocr \
  -H "Content-Type: application/json" \
  -d '{"image_ids": ["test-id"], "language": "he"}'

# 烏爾都文
curl -X POST http://localhost:8000/api/ocr \
  -H "Content-Type: application/json" \
  -d '{"image_ids": ["test-id"], "language": "ur"}'
```

---

## 🌍 地理覆蓋

### 中東地區

```
✅ 阿拉伯半島
   - 沙特阿拉伯
   - 阿聯酋
   - 卡塔爾
   - 科威特
   - 巴林
   - 阿曼
   - 也門

✅ 黎凡特地區
   - 敘利亞
   - 黎巴嫩
   - 約旦
   - 巴勒斯坦
   - 以色列

✅ 北非
   - 埃及
   - 利比亞
   - 突尼斯
   - 阿爾及利亞
   - 摩洛哥
   - 毛里塔尼亞

✅ 伊朗高原
   - 伊朗
   - 阿富汗（達利語）
   - 塔吉克斯坦（塔吉克語）

✅ 南亞
   - 巴基斯坦（烏爾都語）
   - 印度（烏爾都語）
```

---

## 📝 技術細節

### RTL（從右到左）文本處理

**OCR 引擎自動處理：**
- ✅ 文本方向檢測
- ✅ RTL 文本識別
- ✅ 字符順序調整
- ✅ 連字（Ligature）處理

**無需額外配置：**
```java
// 無需特殊配置，OCR 引擎自動處理 RTL
OcrResult result = ocrService.recognize(image, "arabic");
// 文本已正確識別和排序
String text = result.getFullText();
```

### 字體支持

**當前支持：**
- ✅ 中文字體（宋體、黑體、微軟雅黑）
- ✅ 英文字體
- ⚠️ 阿拉伯字體（需要額外配置）
- ⚠️ 希伯來字體（需要額外配置）

**配置方法：**
```yaml
# application.yml
app:
  ofd_font_path: "C:/Windows/Fonts/arial.ttf"  # 支持阿拉伯文
  # 或使用特定字體
  ofd_font_path: "C:/Windows/Fonts/trado.ttf"  # Traditional Arabic
```

---

## 🎯 Web UI 更新

### 語言選擇器

```html
<select id="language">
  <!-- 其他語系 -->
  
  <optgroup label="中東語言（閃含語系 + 印歐語系）⭐">
    <option value="arabic">العربية (阿拉伯文)</option>
    <option value="persian">فارسی (波斯文)</option>
    <option value="urdu">اردو (烏爾都文)</option>
    <option value="hebrew">עברית (希伯來文)</option>
  </optgroup>
  
  <!-- 其他語系 -->
</select>
```

---

## 🧪 測試建議

### 測試文本樣本

**阿拉伯文：**
```
السلام عليكم
مرحبا بالعالم
هذا نص تجريبي باللغة العربية
```

**波斯文：**
```
سلام
به دنیا خوش آمدید
این یک متن آزمایشی به زبان فارسی است
```

**希伯來文：**
```
שלום
ברוכים הבאים לעולם
זהו טקסט ניסיון בעברית
```

**烏爾都文：**
```
السلام علیکم
دنیا میں خوش آمدید
یہ اردو میں ایک آزمائشی متن ہے
```

### 測試場景

1. **基本識別**
   - 純文本圖片
   - 簡單排版

2. **RTL 文本**
   - 從右到左的文本方向
   - 連字處理

3. **混合文本**
   - RTL + LTR 混合
   - 數字和符號

4. **複雜排版**
   - 多列排版
   - 表格
   - 文本框

---

## 📊 語言對比

| 特性 | 阿拉伯文 | 波斯文 | 希伯來文 | 烏爾都文 |
|------|---------|--------|---------|---------|
| **字母系統** | 阿拉伯字母 | 阿拉伯字母變體 | 希伯來字母 | 阿拉伯字母變體 |
| **書寫方向** | RTL | RTL | RTL | RTL |
| **字母數量** | 28 | 32 | 22 | 38 |
| **連字** | ✅ 是 | ✅ 是 | ❌ 否 | ✅ 是 |
| **母音符號** | 可選 | 可選 | 可選 | 可選 |
| **數字系統** | 阿拉伯數字 | 波斯數字 | 阿拉伯數字 | 阿拉伯數字 |

---

## 🎉 優勢

### ✅ 技術優勢

```
✅ RTL 自動處理
✅ 連字識別
✅ 混合文本支持
✅ 高準確率
✅ 無需額外配置
```

### ✅ 市場優勢

```
✅ 中東市場（3.1 億阿拉伯語用戶）
✅ 伊朗市場（1.1 億波斯語用戶）
✅ 巴基斯坦市場（1.7 億烏爾都語用戶）
✅ 以色列市場（900 萬希伯來語用戶）
✅ 全球穆斯林社區
```

---

## 📞 技術支持

**PaddleOCR 文檔：** https://github.com/PaddlePaddle/PaddleOCR

**RapidOCR Java：** https://github.com/RapidAI/RapidOCR

**GitHub：** https://github.com/brianshih04/JPEG2PDF_OFD_Java_2

**相關文檔：**
- LANGUAGES.md - 完整語言列表
- SLAVIC_ROMANCE_LANGUAGES.md - 斯拉夫語系和拉丁語系

---

**中東語言支持已完成！覆蓋阿拉伯世界、伊朗、巴基斯坦和以色列！** 🕌✨
