package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.OfdService;
import lombok.extern.slf4j.Slf4j;
import org.ofdrw.converter.ofdconverter.PDFConverter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * OFD 服务实现
 * 使用 ofdrw-converter 将 PDF 转换为 OFD（保留 OCR 文字层）
 */
@Slf4j
@Service
public class OfdrwServiceImpl implements OfdService {
    
    private final AppConfig config;
    
    public OfdrwServiceImpl(AppConfig config) {
        this.config = config;
    }
    
    @Override
    public Path generateSearchableOfd(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.warn("OFD 生成需要 PDF 文件作为输入");
        log.info("请通过 OcrController 调用，它会先生成 PDF 再转换为 OFD");
        throw new UnsupportedOperationException("请使用 PDF 转 OFD 方式");
    }
    
    /**
     * 将 PDF 转换为 OFD（保留 OCR 文字层）
     * 
     * @param pdfPath PDF 文件路径
     * @param ofdPath 输出 OFD 文件路径
     */
    public void convertPdfToOfd(Path pdfPath, Path ofdPath) throws Exception {
        log.info("开始转换 PDF 到 OFD: {} -> {}", pdfPath, ofdPath);
        
        try (PDFConverter converter = new PDFConverter(ofdPath)) {
            converter.convert(pdfPath);
            log.info("PDF 转 OFD 成功: {}", ofdPath);
        }
    }
}
