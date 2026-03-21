package com.ocr.jpeg2pdf;

import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * Test ofdrw-layout API for direct OFD generation
 */
@Slf4j
public class TestOfdLayoutApi {
    
    @Test
    public void testOfdLayoutClasses() {
        log.info("Exploring ofdrw-layout API...");
        log.info("======================================================================");
        
        // Explore Img class
        try {
            Class<?> imgClass = Class.forName("org.ofdrw.layout.element.Img");
            log.info("\n[1] Img class: {}", imgClass.getName());
            
            log.info("\n  Constructors:");
            for (java.lang.reflect.Constructor<?> constructor : imgClass.getConstructors()) {
                log.info("    {}", constructor);
            }
            
            log.info("\n  Methods:");
            for (java.lang.reflect.Method method : imgClass.getMethods()) {
                if (method.getDeclaringClass().getName().startsWith("org.ofdrw")) {
                    log.info("    {} -> {}", method.getName(), method.getReturnType().getSimpleName());
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Img class not found");
        }
        
        // Explore Span class
        try {
            Class<?> spanClass = Class.forName("org.ofdrw.layout.element.Span");
            log.info("\n[2] Span class: {}", spanClass.getName());
            
            log.info("\n  Constructors:");
            for (java.lang.reflect.Constructor<?> constructor : spanClass.getConstructors()) {
                log.info("    {}", constructor);
            }
            
            log.info("\n  Methods:");
            for (java.lang.reflect.Method method : spanClass.getMethods()) {
                if (method.getDeclaringClass().getName().startsWith("org.ofdrw")) {
                    log.info("    {} -> {}", method.getName(), method.getReturnType().getSimpleName());
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Span class not found");
        }
        
        // Explore Paragraph class
        try {
            Class<?> paraClass = Class.forName("org.ofdrw.layout.element.Paragraph");
            log.info("\n[3] Paragraph class: {}", paraClass.getName());
            
            log.info("\n  Methods:");
            for (java.lang.reflect.Method method : paraClass.getMethods()) {
                if (method.getDeclaringClass().getName().startsWith("org.ofdrw")) {
                    log.info("    {} -> {}", method.getName(), method.getReturnType().getSimpleName());
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Paragraph class not found");
        }
        
        log.info("\n======================================================================");
    }
}
