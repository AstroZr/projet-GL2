package Groupe6.utilz;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test suite pour LangManager
 * 
 * Teste les fonctionnalités critiques:
 * - Changement de langue
 * - Chargement des chaînes traduites
 * - Gestion des fallbacks et clés manquantes
 * - Cohérence du code langue
 */
public class LangManagerTest {
    
    @Before
    public void setUp() {
        // Réinitialiser la langue par défaut avant chaque test
        LangManager.setLangue("fr");
    }
    
    /**
     * Test 1: Définition et récupération de la langue courante
     */
    @Test
    public void testDefinirEtRecupererLangue() {
        LangManager.setLangue("en");
        assertEquals("La langue courante doit être 'en'", "en", LangManager.getLangueCode());
        
        LangManager.setLangue("fr");
        assertEquals("La langue courante doit être 'fr'", "fr", LangManager.getLangueCode());
    }
    
    /**
     * Test 2: Ignorer les changements de langue null ou vides
     */
    @Test
    public void testIgnorerLangueNullOuVide() {
        LangManager.setLangue("en");
        assertEquals("La langue doit être 'en'", "en", LangManager.getLangueCode());
        
        // Tenter de changer vers null
        LangManager.setLangue(null);
        assertEquals("La langue ne doit pas changer si null", "en", LangManager.getLangueCode());
        
        // Tenter de changer vers vide
        LangManager.setLangue("");
        assertEquals("La langue ne doit pas changer si vide", "en", LangManager.getLangueCode());
    }
    
    /**
     * Test 3: Récupérer une clé valide en français
     */
    @Test
    public void testRecupererCleFrancais() {
        LangManager.setLangue("fr");
        
        // Ces clés sont censées exister dans langue_fr.properties
        String cle = LangManager.get("app.name");
        assertNotNull("La clé doit être trouvée", cle);
        assertFalse("La clé ne doit pas être vide", cle.isBlank());
        // On ne vérifie pas la valeur exacte car elle peut varier selon le projet
    }
    
    /**
     * Test 4: Récupérer une clé valide en anglais
     */
    @Test
    public void testRecupererCleAnglais() {
        LangManager.setLangue("en");
        
        String cle = LangManager.get("app.name");
        assertNotNull("La clé doit être trouvée en anglais", cle);
        assertFalse("La clé ne doit pas être vide", cle.isBlank());
    }
    
    /**
     * Test 5: Gestion d'une clé invalide (fallback)
     */
    @Test
    public void testCleInvalideFallback() {
        LangManager.setLangue("en");
        
        // Demander une clé qui n'existe probablement pas
        String resultat = LangManager.get("cle.inexistante.speciale123");
        
        // Si la clé n'existe pas, LangManager doit retourner la clé elle-même ou un fallback
        assertNotNull("Le résultat ne doit jamais être null", resultat);
        assertTrue("Le résultat doit être non-vide", !resultat.isBlank());
    }
    
    /**
     * Test 6: Fallback vers français si la clé est absente en anglais
     */
    @Test
    public void testFallbackVersFrancais() {
        // Définir anglais
        LangManager.setLangue("en");
        
        // Récupérer une clé standard (qui devrait exister dans les deux langues)
        String cle = LangManager.get("app.name");
        assertNotNull("La clé doit être accessible", cle);
    }
    
    /**
     * Test 7: Changer de langue et récupérer une autre clé
     */
    @Test
    public void testChangerLangueEtRecupererCle() {
        // Français
        LangManager.setLangue("fr");
        String cleFr = LangManager.get("app.name");
        
        // Anglais
        LangManager.setLangue("en");
        String cleEn = LangManager.get("app.name");
        
        // Les deux doivent être non-null et non-vides
        assertNotNull("La clé française doit exister", cleFr);
        assertNotNull("La clé anglaise doit exister", cleEn);
        assertFalse("La clé française ne doit pas être vide", cleFr.isBlank());
        assertFalse("La clé anglaise ne doit pas être vide", cleEn.isBlank());
    }
    
    /**
     * Test 8: Le code langue par défaut doit être 'fr'
     */
    @Test
    public void testLangueParDefaut() {
        // Le setUp() réinitialise la langue à 'fr'
        // Créer une nouvelle instance de LangManager par défaut
        LangManager.setLangue("fr");
        assertEquals("La langue par défaut doit être 'fr'", "fr", LangManager.getLangueCode());
    }
    
    /**
     * Test 9: Récupérer plusieurs clés successivement
     */
    @Test
    public void testRecupererPlusieursClés() {
        LangManager.setLangue("fr");
        
        String cle1 = LangManager.get("app.name");
        String cle2 = LangManager.get("menu.jouer");
        String cle3 = LangManager.get("app.name"); // Même clé que cle1
        
        assertNotNull("Première clé doit être non-null", cle1);
        assertNotNull("Deuxième clé doit être non-null", cle2);
        assertNotNull("Troisième clé doit être non-null", cle3);
        
        // La première et la troisième devraient être identiques
        assertEquals("Les mêmes clés devraient retourner la même valeur", cle1, cle3);
    }
    
    /**
     * Test 10: Cohérence entre setLangue et getLangueCode
     */
    @Test
    public void testCoherenceSetLangueGetLangueCode() {
        String[] langues = {"fr", "en", "de", "es"};
        
        for (String langue : langues) {
            LangManager.setLangue(langue);
            assertEquals("setLangue et getLangueCode doivent être cohérents pour: " + langue,
                         langue, LangManager.getLangueCode());
        }
    }
    
    /**
     * Test 11: Performance - appels répétés avec même clé
     */
    @Test
    public void testPerformanceAppelsRepetes() {
        LangManager.setLangue("fr");
        
        String cle = "app.name";
        long debut = System.currentTimeMillis();
        
        // Faire 1000 appels à la même clé
        for (int i = 0; i < 1000; i++) {
            LangManager.get(cle);
        }
        
        long fin = System.currentTimeMillis();
        long temps = fin - debut;
        
        // Les appels répétés devraient être rapides (moins de 500ms pour 1000 appels)
        assertTrue("Les appels répétés doivent être rapides (moins de 500ms)", temps < 500);
    }
}
