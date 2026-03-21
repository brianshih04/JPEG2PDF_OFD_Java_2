package com.ocr.jpeg2pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ocr.jpeg2pdf.config.AppConfig;

/**
 * Web 頁面控制器
 */
@Controller
public class WebController {
    
    @Autowired
    private AppConfig config;
    
    /**
     * 首頁
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * 設定頁面
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", config);
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        return "settings";
    }
    
    /**
     * 更新設定
     */
    @PostMapping("/settings")
    @ResponseBody
    public java.util.Map<String, Object> updateSettings(@RequestBody java.util.Map<String, Object> settings) {
        try {
            // 更新配置
            if (settings.containsKey("outputFolder")) {
                config.setOutputFolder((String) settings.get("outputFolder"));
            }
            if (settings.containsKey("watchFolder")) {
                config.setWatchFolder((String) settings.get("watchFolder"));
            }
            if (settings.containsKey("saveFolder")) {
                config.setSaveFolder((String) settings.get("saveFolder"));
            }
            if (settings.containsKey("defaultLanguage")) {
                config.setDefaultLanguage((String) settings.get("defaultLanguage"));
            }
            if (settings.containsKey("ocrEngine")) {
                config.setOcrEngine((String) settings.get("ocrEngine"));
            }
            if (settings.containsKey("fontPath")) {
                config.setFontPath((String) settings.get("fontPath"));
            }
            if (settings.containsKey("ofdFontPath")) {
                config.setOfdFontPath((String) settings.get("ofdFontPath"));
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "設定已儲存");
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
}
