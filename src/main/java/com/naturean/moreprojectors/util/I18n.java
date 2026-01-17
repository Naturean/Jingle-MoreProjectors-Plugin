package com.naturean.moreprojectors.util;

import com.naturean.moreprojectors.MoreProjectors;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Internationalization (i18n) utility for MoreProjectors
 */
public class I18n {
    private static final Properties MESSAGES = new Properties();
    public static final String DEFAULT_LANGUAGE = "en_US";

    static {
        loadMessages();
    }

    /**
     * Load messages from unified resource path
     */
    private static void loadMessages() {
        String resourcePath = MoreProjectors.IS_DEV ? String.format("/lang/%s.properties", DEFAULT_LANGUAGE) : "/language.properties";

        try (InputStream input = I18n.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                MoreProjectors.log(Level.WARN, "Language file not found: " + resourcePath);
                return;
            }

            MESSAGES.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            MoreProjectors.log(Level.INFO, "Loaded language properties");
        } catch (IOException e) {
            MoreProjectors.logError("Failed to load language file: " + resourcePath, e);
        }
    }

    /**
     * Get translated message
     * @param key message key
     * @return translated message, or key if not found
     */
    public static String get(String key) {
        return MESSAGES.getProperty(key, key);
    }

    /**
     * Get translated message with format arguments
     * @param key message key
     * @param args format arguments
     * @return formatted translated message
     */
    public static String format(String key, Object... args) {
        String message = get(key);
        try {
            return String.format(message, args);
        } catch (Exception e) {
            MoreProjectors.logError("Failed to format message: " + key, e);
            return message;
        }
    }

    /**
     * Get current language code
     */
    public static String getCurrentLanguage() {
        return get("global.current.language");
    }
}