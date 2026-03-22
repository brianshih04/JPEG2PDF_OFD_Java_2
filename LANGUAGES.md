# 支持的语言列表

## 📊 当前状态

**RapidOCR-Java 基于 PaddleOCR，支持 80+ 种语言！**

---

## ✅ 已支持语言

### 主要语言
| 语言 | 代码 | 说明 |
|------|------|------|
| **中文简体** | `ch` | 简体中文 |
| **中文繁体** | `chinese_cht` | 繁体中文 |
| **英文** | `en` | 英语 |
| **日文** | `japan` | 日本语 |
| **韩文** | `korean` | 韩国语 |

---

### 🇪🇺 拉丁語系（羅曼語族）⭐

基于拉丁语的罗曼语族语言：

| 语言 | 代码 | 使用地区 |
|------|------|---------|
| **法文** | `french` | 法国、加拿大、比利时、瑞士等 |
| **西班牙文** | `spanish` | 西班牙、拉丁美洲大部分国家 |
| **葡萄牙文** | `portuguese` | 葡萄牙、巴西、安哥拉等 |
| **意大利文** | `italian` | 意大利、瑞士、圣马力诺 |
| **罗马尼亚文** | `romanian` | 罗马尼亚、摩尔多瓦 |
| **奥克西坦文** | `oc` | 法国南部、意大利、西班牙 |
| **加泰罗尼亚文** | `ca` | 西班牙加泰罗尼亚、安道尔 |
| **加利西亚文** | `gl` | 西班牙加利西亚地区 |

**使用示例：**
```python
# 法文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "french"
})
```

---

### 🇷🇺 斯拉夫語系 ⭐

使用西里尔字母和拉丁字母的斯拉夫语言：

#### 西里尔字母
| 语言 | 代码 | 使用地区 |
|------|------|---------|
| **俄文** | `russian` | 俄罗斯、白俄罗斯、哈萨克斯坦等 |
| **乌克兰文** | `uk` | 乌克兰 |
| **白俄罗斯文** | `be` | 白俄罗斯 |
| **保加利亚文** | `bg` | 保加利亚 |
| **塞尔维亚文（西里尔）** | `rs_cyrillic` | 塞尔维亚 |
| **马其顿文** | `mk` | 北马其顿 |

#### 拉丁字母
| 语言 | 代码 | 使用地区 |
|------|------|---------|
| **波兰文** | `pl` | 波兰 |
| **捷克文** | `cs` | 捷克共和国 |
| **斯洛伐克文** | `sk` | 斯洛伐克 |
| **斯洛文尼亚文** | `sl` | 斯洛文尼亚 |
| **克罗地亚文** | `hr` | 克罗地亚 |
| **波斯尼亚文** | `bs` | 波斯尼亚和黑塞哥维那 |
| **塞尔维亚文（拉丁）** | `rs_latin` | 塞尔维亚 |

**使用示例：**
```python
# 俄文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "russian"
})

# 波兰文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "pl"
})
```

---

### 欧洲其他语言
| 语言 | 代码 | 语言 | 代码 |
|------|------|------|------|
| 法文 | `fr` | 德文 | `german` |
| 西班牙文 | `es` | 意大利文 | `it` |
| 葡萄牙文 | `pt` | 俄文 | `ru` |
| 荷兰文 | `nl` | 波兰文 | `pl` |
| 瑞典文 | `sv` | 丹麦文 | `da` |
| 挪威文 | `no` | 芬兰文 | `fi` |
| 希腊文 | `el` | 捷克文 | `cs` |
| 匈牙利文 | `hu` | 罗马尼亚文 | `ro` |
| 斯洛伐克文 | `sk` | 斯洛文尼亚文 | `sl` |
| 保加利亚文 | `bg` | 乌克兰文 | `uk` |
| 白俄罗斯文 | `be` | 塞尔维亚文（拉丁） | `rs_latin` |
| 塞尔维亚文（西里尔） | `rs_cyrillic` | 克罗地亚文 | `hr` |
| 波斯尼亚文 | `bs` | 阿尔巴尼亚文 | `sq` |
| 立陶宛文 | `lt` | 拉脱维亚文 | `lv` |
| 爱沙尼亚文 | `et` | 爱尔兰文 | `ga` |
| 威尔士文 | `cy` | 冰岛文 | `is` |
| 马耳他文 | `mt` | 奥克西坦文 | `oc` |

### 亚洲语言
| 语言 | 代码 | 语言 | 代码 |
|------|------|------|------|
| 阿拉伯文 | `ar` | 印地文 | `hi` |
| 波斯文 | `fa` | 乌尔都文 | `ur` |
| 土耳其文 | `tr` | 越南文 | `vi` |
| 泰文 | `th` | 印尼文 | `id` |
| 马来文 | `ms` | 他加禄文 | `tl` |
| 旁遮普文 | `pa` | 古吉拉特文 | `gu` |
| 泰卢固文 | `te` | 泰米尔文 | `ta` |
| 卡纳达文 | `kn` | 马拉雅拉姆文 | `ml` |
| 孟加拉文 | `bn` | 尼泊尔文 | `ne` |
| 僧伽罗文 | `si` | 缅甸文 | `my` |
| 高棉文 | `km` | 老挝文 | `lo` |

### 其他语言
| 语言 | 代码 | 语言 | 代码 |
|------|------|------|------|
| 维吾尔文 | `ug` | 蒙古文 | `mn` |
| 库尔德文 | `ku` | 阿塞拜疆文 | `az` |
| 乌兹别克文 | `uz` | 斯瓦希里文 | `sw` |
| 南非荷兰文 | `af` | 毛利文 | `mi` |

---

## 🎯 Web UI 語言選擇器

### 已分組顯示

**Web UI 已按語系分組，包含以下類別：**

#### 1. 中文
- 繁體中文 (`chinese_cht`) - 默認
- 简体中文 (`ch`)

#### 2. 英語系
- English (`en`)
- Deutsch 德文 (`german`)
- Nederlands 荷蘭文 (`dutch`)

#### 3. 拉丁語系（羅曼語族）⭐
- Français 法文 (`french`)
- Español 西班牙文 (`spanish`)
- Português 葡萄牙文 (`portuguese`)
- Italiano 意大利文 (`italian`)
- Română 羅馬尼亞文 (`romanian`)
- Occitan 奧克西坦文 (`oc`)

#### 4. 斯拉夫語系 ⭐
**西里爾字母：**
- Русский 俄文 (`russian`)
- Українська 烏克蘭文 (`ukrainian`)
- Беларуская 白俄羅斯文 (`be`)
- Български 保加利亞文 (`bg`)
- Српски ћирилица 塞爾維亞西里爾 (`rs_cyrillic`)

**拉丁字母：**
- Srpski latinica 塞爾維亞拉丁 (`rs_latin`)
- Hrvatski 克羅地亞文 (`hr`)
- Bosanski 波斯尼亞文 (`bs`)
- Slovenščina 斯洛文尼亞文 (`sl`)
- Slovenčina 斯洛伐克文 (`sk`)
- Čeština 捷克文 (`cs`)
- Polski 波蘭文 (`pl`)

#### 5. 亞洲語言
- 日本語 (`japan`)
- 한국어 韓文 (`korean`)
- हिन्दी 印地文 (`hindi`)
- Tiếng Việt 越南文 (`vietnamese`)
- ไทย 泰文 (`thai`)
- العربية 阿拉伯文 (`arabic`)
- فارسی 波斯文 (`persian`)
- Türkçe 土耳其文 (`turkish`)

#### 6. 其他語言
- Magyar 匈牙利文 (`hungarian`)
- Ελληνικά 希臘文 (`greek`)
- Shqip 阿爾巴尼亞文 (`albanian`)
- Lietuvių 立陶宛文 (`lithuanian`)
- Latviešu 拉脫維亞文 (`latvian`)
- Eesti 愛沙尼亞文 (`estonian`)

---

## 🔧 使用方法

### API 调用
```bash
POST /api/ocr
Content-Type: application/json

{
  "image_ids": ["图片ID"],
  "language": "japan"  # 日文
}
```

### Python 测试
```python
# 日文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "japan"
})

# 韩文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "korean"
})

# 英文 OCR
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "en"
})
```

---

## ⚙️ 配置文件

### application.yml
```yaml
app:
  # 默认语言
  default_language: chinese_cht
  
  # 支持的语言列表
  supported_languages:
    - code: ch
      name: 中文简体
    - code: chinese_cht
      name: 中文繁體
    - code: en
      name: English
    - code: japan
      name: 日本語
    - code: korean
      name: 한국어
    - code: french
      name: Français
    - code: german
      name: Deutsch
    - code: spanish
      name: Español
```

---

## 📝 完整语言列表

### 80 种语言支持

<details>
<summary>点击查看完整列表</summary>

| 语言 | 代码 | 语言 | 代码 | 语言 | 代码 |
|------|------|------|------|------|------|
| 中文简体 | `ch` | 中文繁体 | `chinese_cht` | 英文 | `en` |
| 日文 | `japan` | 韩文 | `korean` | 法文 | `fr` |
| 德文 | `german` | 西班牙文 | `es` | 意大利文 | `it` |
| 葡萄牙文 | `pt` | 俄文 | `ru` | 荷兰文 | `nl` |
| 波兰文 | `pl` | 瑞典文 | `sv` | 丹麦文 | `da` |
| 挪威文 | `no` | 芬兰文 | `fi` | 希腊文 | `el` |
| 捷克文 | `cs` | 匈牙利文 | `hu` | 罗马尼亚文 | `ro` |
| 斯洛伐克文 | `sk` | 斯洛文尼亚文 | `sl` | 保加利亚文 | `bg` |
| 乌克兰文 | `uk` | 白俄罗斯文 | `be` | 塞尔维亚文（拉丁） | `rs_latin` |
| 塞尔维亚文（西里尔） | `rs_cyrillic` | 克罗地亚文 | `hr` | 波斯尼亚文 | `bs` |
| 阿尔巴尼亚文 | `sq` | 立陶宛文 | `lt` | 拉脱维亚文 | `lv` |
| 爱沙尼亚文 | `et` | 爱尔兰文 | `ga` | 威尔士文 | `cy` |
| 冰岛文 | `is` | 马耳他文 | `mt` | 奥克西坦文 | `oc` |
| 阿拉伯文 | `ar` | 印地文 | `hi` | 波斯文 | `fa` |
| 乌尔都文 | `ur` | 土耳其文 | `tr` | 越南文 | `vi` |
| 泰文 | `th` | 印尼文 | `id` | 马来文 | `ms` |
| 他加禄文 | `tl` | 旁遮普文 | `pa` | 古吉拉特文 | `gu` |
| 泰卢固文 | `te` | 泰米尔文 | `ta` | 卡纳达文 | `kn` |
| 马拉雅拉姆文 | `ml` | 孟加拉文 | `bn` | 尼泊尔文 | `ne` |
| 僧伽罗文 | `si` | 缅甸文 | `my` | 高棉文 | `km` |
| 老挝文 | `lo` | 维吾尔文 | `ug` | 蒙古文 | `mn` |
| 库尔德文 | `ku` | 阿塞拜疆文 | `az` | 乌兹别克文 | `uz` |
| 斯瓦希里文 | `sw` | 南非荷兰文 | `af` | 毛利文 | `mi` |
| 马拉地文 | `mr` | 泰卢固文 | `te` | 比哈尔文 | `bh` |
| 迈蒂利文 | `mai` | 安吉卡文 | `ang` | 博杰普尔文 | `bho` |
| 马加希文 | `mah` | 纳格普尔文 | `sck` | 塔巴萨兰文 | `tab` |
| 阿瓦尔文 | `ava` | 阿迪格文 | `ady` | 卡巴尔达文 | `kbd` |
| 达尔金文 | `dar` | 印古什文 | `inh` | 拉克文 | `lbe` |
| 列兹金文 | `lez` | 阿巴扎文 | `abq` | 果阿孔卡尼文 | `gom` |
| 新瓦里文 | `new` | 沙特阿拉伯文 | `sa` | 马拉地文 | `mr` |
| 比哈尔文 | `bh` | 安吉卡文 | `ang` | 迈蒂利文 | `mai` |
| 马加希文 | `mah` | 纳格普尔文 | `sck` | - | - |

</details>

---

## 🎯 实现计划

### ✅ 已完成
1. **后端支持**
   - ✅ 后端已支持所有 80+ 种语言
   - ✅ API 已接受 `language` 参数

2. **Web UI 语言选择器** ⭐
   - ✅ 添加语言选择器
   - ✅ 按语系分组（中文、英語系、拉丁語系、斯拉夫語系、亞洲語言、其他）
   - ✅ 斯拉夫語系：12 种语言（俄文、烏克蘭文、波蘭文等）
   - ✅ 拉丁語系：6 种语言（法文、西班牙文、意大利文等）
   - ✅ 默认选择繁体中文

3. **文檔**
   - ✅ LANGUAGES.md 完整語言列表
   - ✅ 語系分類說明
   - ✅ 使用示例

### 📊 统计

**总支持语言：** 80+ 种

**Web UI 显示：** 40+ 种常用语言

**語系分類：**
- 中文：2 种
- 英語系：3 种
- 拉丁語系：6 种 ⭐
- 斯拉夫語系：12 种 ⭐
- 亞洲語言：8 种
- 其他語言：6 种

### 🚧 待實現（可選）
1. **语言自动检测**
   - 自动检测图片语言
   - 提高识别准确率

2. **多语言字体支持**
   - 日文字体
   - 韩文字体
   - 阿拉伯字体
   - 斯拉夫語系字體

3. **混合语言识别**
   - 同时识别多种语言
   - 处理混合文本

---

## 📞 技术支持

**PaddleOCR 文档**: https://github.com/PaddlePaddle/PaddleOCR

**RapidOCR Java**: https://github.com/RapidAI/RapidOCR

**GitHub**: https://github.com/brianshih04/JPEG2PDF_OFD_Java_2
