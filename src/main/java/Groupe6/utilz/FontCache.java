package Groupe6.utilz;

import java.awt.Font;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache global de polices pour éviter de recréer des instances identiques.
 */
public final class FontCache {

    private static final Map<String, Font> CACHE = new ConcurrentHashMap<>();

    private FontCache() {
    }

    public static Font get(String family, int style, int size) {
        int safeSize = Math.max(1, size);
        String key = family + "|" + style + "|" + safeSize;
        return CACHE.computeIfAbsent(key, k -> new Font(family, style, safeSize));
    }
}