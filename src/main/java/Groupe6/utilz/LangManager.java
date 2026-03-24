package Groupe6.utilz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Gestionnaire de langue : charge les fichiers {@code langue_XX.properties}
 * depuis le classpath et expose {@link #get(String)} pour récupérer une chaîne traduite.
 * <p>
 * Utilisation : {@code LangManager.get("menu.jouer")}
 */
public final class LangManager {

    private static String langueCode = "fr";
    private static ResourceBundle bundle = null;

    private LangManager() {}

    /** Définit la langue active et recharge le bundle. */
    public static void setLangue(String code) {
        if (code == null || code.isBlank()) return;
        langueCode = code;
        bundle = null; // force rechargement au prochain get()
    }

    /** Retourne le code langue actif ("fr", "en", …). */
    public static String getLangueCode() {
        return langueCode;
    }

    /**
     * Retourne la chaîne traduite associée à {@code key} dans la langue courante.
     * En cas d'absence, tente un fallback vers le français, puis retourne la clé elle-même.
     */
    public static String get(String key) {
        if (bundle == null) {
            bundle = chargerBundle(langueCode);
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.printf("[LangManager] Clé absente : '%s' pour la langue '%s'%n", key, langueCode);
            // Fallback vers le français
            if (!"fr".equals(langueCode)) {
                try {
                    ResourceBundle fallback = chargerBundle("fr");
                    return fallback.getString(key);
                } catch (MissingResourceException ignored) {}
            }
            return key;
        }
    }

    private static ResourceBundle chargerBundle(String code) {
        Locale locale = Locale.forLanguageTag(code);
        try {
            return ResourceBundle.getBundle("langue", locale, new Utf8Control());
        } catch (MissingResourceException e) {
            System.err.printf("[LangManager] Bundle introuvable pour '%s', fallback fr%n", code);
            try {
                return ResourceBundle.getBundle("langue", Locale.forLanguageTag("fr"), new Utf8Control());
            } catch (MissingResourceException e2) {
                throw new IllegalStateException("Impossible de charger le bundle de langue.", e2);
            }
        }
    }

    /** Control personnalisé pour forcer le chargement des .properties en UTF-8. */
    private static class Utf8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            if ("java.properties".equals(format)) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");
                try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                    if (stream != null) {
                        try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                            return new PropertyResourceBundle(reader);
                        }
                    }
                }
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }
    }
}
