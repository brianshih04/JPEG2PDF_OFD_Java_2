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

### 欧洲语言
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

### 当前状态
- ✅ 后端已支持所有 80+ 种语言
- ✅ API 已接受 `language` 参数
- ⚠️ Web 界面需要添加语言选择器

### 待实现
1. **Web UI 语言选择器**
   - 下拉菜单选择语言
   - 默认选择繁体中文

2. **语言检测**
   - 自动检测图片语言
   - 提高识别准确率

3. **多语言字体支持**
   - 日文字体
   - 韩文字体
   - 阿拉伯字体

---

## 📞 技术支持

**PaddleOCR 文档**: https://github.com/PaddlePaddle/PaddleOCR

**RapidOCR Java**: https://github.com/RapidAI/RapidOCR

**GitHub**: https://github.com/brianshih04/JPEG2PDF_OFD_Java_2
