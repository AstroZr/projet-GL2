package Groupe6.utilz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire centralisé de la localisation (i18n) du jeu.
 * 
 * Utilise ResourceBundle avec fichiers .properties pour charger les textes traduits.
 * Format: langue_XX.properties (ex: langue_fr.properties, langue_en.properties)
 * 
 * Clés exemple: menu.jouer, menu.quitter, grille.victoire, parametres.volume, etc.
 * 
 * Fonctionnement:
 * - Charge le bundle en fonction de la langue courante
 * - Fallback automatique vers le français si clé manquante
 * - UTF-8 support complet via Utf8Control personnalisé
 * 
 * Utilisation:
 *   LangManager.setLangue("en");          // Basculer en anglais
 *   String titre = LangManager.get("menu.jouer");  // Charger une chaîne
 */
public final class LangManager {

    private static final Logger logger = LoggerFactory.getLogger(LangManager.class);

    // ====== ÉTAT GLOBAL ======
    private static String langueCode = "fr";       // Langue courante ("fr", "en", etc.)
    private static ResourceBundle bundle = null;   // Bundle des chaînes traduits (null force rechargement)

    // ====== SINGLETON (privates) ======
    private LangManager() {}  // Constructeur privé (classe statique uniquement)

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
            logger.warn("Clé absente : '{}' pour la langue '{}'", key, langueCode);
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
            logger.warn("Bundle introuvable pour '{}', fallback fr", code);
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
