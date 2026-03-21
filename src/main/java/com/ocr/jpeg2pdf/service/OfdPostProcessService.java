package com.ocr.jpeg2pdf.service;

import java.nio.file.Path;
import java.awt.image.BufferedImage;
import java.util.List;
import com.ocr.jpeg2pdf.model.OcrResult;

/**
 * OFD 后处理服务 - 在 OFD 中添加文字层
 */
public interface OfdPostProcessService {
    
    /**
     * 在 OFD 文件中添加文字层
     * 
     * @param ofdPath 已生成的 OFD 文件路径（包含图片）
     * @param ocrResults OCR 结果
     * @param outputPath 输出 OFD 文件路径
     * @return 输出文件路径
     */
    Path addTextLayerToOfd(Path ofdPath, List<OcrResult> ocrResults, Path outputPath) throws Exception;
}
