package com.distributed.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private int tolerance;

    public ConfigManager(String configPath) {
        loadConfig(configPath);
    }

    private void loadConfig(String configPath) {
        File configFile = new File(configPath);

        if (!configFile.exists()) {
            throw new RuntimeException("Konfigürasyon dosyası bulunamadı: " + configPath);
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            String toleranceStr = props.getProperty("tolerance");
            if (toleranceStr == null || toleranceStr.trim().isEmpty()) {
                throw new RuntimeException("tolerance değeri konfigürasyon dosyasında tanımlı değil");
            }
            this.tolerance = Integer.parseInt(toleranceStr.trim());
            System.out.println("[CONFIG] Tolerance değeri yüklendi: " + this.tolerance);
        } catch (IOException e) {
            throw new RuntimeException("Konfigürasyon okuma hatası: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Tolerance değeri geçersiz sayı formatında");
        }
    }

    public int getTolerance() {
        return tolerance;
    }
}
