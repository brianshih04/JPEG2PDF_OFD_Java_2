// 全局變數
let selectedFiles = [];
let uploadedImageId = null;

// DOM 元素
const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('file-input');
const fileList = document.getElementById('file-list');
const filesList = document.getElementById('files');
const uploadBtn = document.getElementById('upload-btn');
const languageSelect = document.getElementById('language');
const formatSelect = document.getElementById('format');
const resultSection = document.getElementById('result-section');
const loadingOverlay = document.getElementById('loading-overlay');
const loadingMessage = document.getElementById('loading-message');

// API 基礎 URL
const API_BASE = '/api';

// ==================== 檔案上傳 ====================

// 點擊拖放區域
dropZone.addEventListener('click', () => fileInput.click());

// 拖放事件
dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('dragover');
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.classList.remove('dragover');
    
    const files = Array.from(e.dataTransfer.files);
    addFiles(files);
});

// 檔案選擇
fileInput.addEventListener('change', (e) => {
    const files = Array.from(e.target.files);
    addFiles(files);
});

// 新增檔案到列表
function addFiles(files) {
    const imageFiles = files.filter(file => file.type.startsWith('image/'));
    
    if (imageFiles.length === 0) {
        showToast('請選擇圖片檔案', 'error');
        return;
    }
    
    selectedFiles = [...selectedFiles, ...imageFiles];
    updateFileList();
}

// 更新檔案列表
function updateFileList() {
    if (selectedFiles.length === 0) {
        fileList.classList.add('hidden');
        uploadBtn.disabled = true;
        return;
    }
    
    fileList.classList.remove('hidden');
    uploadBtn.disabled = false;
    
    filesList.innerHTML = selectedFiles.map((file, index) => `
        <li class="file-item">
            <div>
                <span class="file-name">${file.name}</span>
                <span class="file-size">${formatFileSize(file.size)}</span>
            </div>
            <button class="remove-btn" onclick="removeFile(${index})">✕</button>
        </li>
    `).join('');
}

// 移除檔案
function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
}

// 格式化檔案大小
function formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

// ==================== 上傳與 OCR ====================

uploadBtn.addEventListener('click', async () => {
    if (selectedFiles.length === 0) return;
    
    showLoading('正在上傳圖片...');
    
    try {
        // 上傳圖片
        const formData = new FormData();
        selectedFiles.forEach(file => {
            formData.append('files', file);
        });
        
        const uploadResponse = await fetch(`${API_BASE}/upload`, {
            method: 'POST',
            body: formData
        });
        
        const uploadResult = await uploadResponse.json();
        
        if (!uploadResult.success) {
            throw new Error(uploadResult.error || '上傳失敗');
        }
        
        uploadedImageId = uploadResult.images[0].id;
        showToast('圖片上傳成功', 'success');
        
        // 執行 OCR
        showLoading('正在執行 OCR 辨識...');
        
        const ocrResponse = await fetch(`${API_BASE}/ocr`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                image_ids: [uploadedImageId],
                language: languageSelect.value
            })
        });
        
        const ocrResult = await ocrResponse.json();
        
        if (!ocrResult.success) {
            throw new Error('OCR 辨識失敗');
        }
        
        // 顯示 OCR 結果
        displayOcrResult(ocrResult.results[0]);
        
        // 匯出檔案
        showLoading('正在匯出檔案...');
        
        const exportResponse = await fetch(`${API_BASE}/export`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                image_ids: [uploadedImageId],
                format: formatSelect.value,
                language: languageSelect.value
            })
        });
        
        const exportResult = await exportResponse.json();
        
        if (!exportResult.success) {
            throw new Error('匯出失敗');
        }
        
        // 顯示結果
        displayExportResult(exportResult);
        hideLoading();
        showToast('處理完成！', 'success');
        
    } catch (error) {
        hideLoading();
        showToast(error.message, 'error');
        console.error('處理失敗:', error);
    }
});

// ==================== 顯示結果 ====================

function displayOcrResult(result) {
    resultSection.classList.remove('hidden');
    
    document.getElementById('line-count').textContent = result.line_count || 0;
    document.getElementById('confidence').textContent = 
        (result.confidence * 100).toFixed(1) + '%';
    document.getElementById('ocr-text').value = result.text || '';
}

function displayExportResult(result) {
    const outputFile = document.getElementById('output-file');
    const downloadBtn = document.getElementById('download-btn');
    
    outputFile.textContent = `輸出檔案：${result.output_file}`;
    
    downloadBtn.onclick = () => {
        // 下載檔案
        const link = document.createElement('a');
        link.href = `/api/download?file=${encodeURIComponent(result.output_file)}`;
        link.download = result.output_file.split('/').pop();
        link.click();
    };
}

// ==================== 新任務 ====================

document.getElementById('new-task-btn')?.addEventListener('click', () => {
    selectedFiles = [];
    uploadedImageId = null;
    updateFileList();
    resultSection.classList.add('hidden');
    fileInput.value = '';
});

// ==================== 載入中 ====================

function showLoading(message) {
    loadingMessage.textContent = message;
    loadingOverlay.classList.remove('hidden');
}

function hideLoading() {
    loadingOverlay.classList.add('hidden');
}

// ==================== Toast 通知 ====================

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toast-message');
    
    toastMessage.textContent = message;
    toast.querySelector('div').className = 
        `px-6 py-3 rounded-lg shadow-lg flex items-center ${type === 'error' ? 'bg-red-500' : 'bg-green-500'} text-white`;
    
    toast.classList.remove('hidden');
    
    setTimeout(() => {
        hideToast();
    }, 3000);
}

function hideToast() {
    document.getElementById('toast').classList.add('hidden');
}

// ==================== 初始化 ====================

document.addEventListener('DOMContentLoaded', () => {
    console.log('JPEG2PDF_OFD_Java 已載入');
});
