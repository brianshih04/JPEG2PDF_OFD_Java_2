# -*- coding: utf-8 -*-
"""Test OCR -> Text, PDF, OFD"""
import requests
import json
import os
from pathlib import Path

BASE_URL = "http://localhost:8000/api"

print("=" * 70)
print("Test OCR -> Text, PDF, OFD")
print("=" * 70)

# 1. Upload
print("\n[1/4] Uploading image...")
test_image = r"C:\Users\Brian\.openclaw\media\inbound\test---724870e9-5949-4d64-9e08-755d2c8e4f14.jpg"

with open(test_image, "rb") as f:
    files = {'files': ('test.jpg', f, 'image/jpeg')}
    response = requests.post(f"{BASE_URL}/upload", files=files)
    
if response.status_code != 200:
    print(f"Upload failed: {response.status_code}")
    exit(1)

result = response.json()
image_id = result['images'][0]['id']
print(f"Upload success: {image_id}")

# 2. OCR
print("\n[2/4] Performing OCR...")
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "chinese_cht"
})

if ocr_response.status_code != 200:
    print(f"OCR failed: {ocr_response.status_code}")
    exit(1)

print(f"OCR success")

# 3. Export
formats = ["text", "searchable_pdf", "ofd"]  # 使用 "ofd" 测试方案 B

for i, fmt in enumerate(formats, 3):
    print(f"\n[{i}/4] Exporting {fmt}...")
    
    export_response = requests.post(f"{BASE_URL}/export", json={
        "image_ids": [image_id],
        "format": fmt
    })
    
    if export_response.status_code != 200:
        print(f"Export {fmt} failed: {export_response.status_code}")
        print(export_response.text)
        continue
    
    result = export_response.json()
    output_file = result.get('output_file')
    
    if output_file and os.path.exists(output_file):
        file_size = os.path.getsize(output_file)
        print(f"{fmt} exported: {file_size / 1024:.2f} KB")
        print(f"File: {output_file}")

print("\n" + "=" * 70)
print("Test completed!")
print("=" * 70)
