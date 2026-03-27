package Groupe6.save;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Test suite pour SaveManager
 * 
 * Teste les fonctionnalités critiques:
 * - Gestion des profils joueurs (création, chargement, sauvegarde)
 * - Gestion des parties en cours (sauvegarde et chargement)
 * - Gestion des meilleurs temps
 * - Chargement des niveaux
 */
public class SaveManagerTest {
    
    private static final String TEST_SAVE_FOLDER = "saveGame/";
    private static final String TEST_PLAYER_NAME = "TestJoueur";
    
    @Before
    public void setUp() {
        // Nettoyer les fichiers de test avant chaque test
        cleanupTestFiles();
    }
    
    @After
    public void tearDown() {
        // Nettoyer après chaque test
        cleanupTestFiles();
    }
    
    private void cleanupTestFiles() {
        try {
            Path saveFolder = Paths.get(TEST_SAVE_FOLDER);
            if (Files.exists(saveFolder)) {
                Files.walk(saveFolder)
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            // Ignorer les erreurs de suppression
                        }
                    });
            }
        } catch (Exception e) {
            // Ignorer les erreurs
        }
    }
    
    /**
     * Test 1: Création et récupération d'un nouvel ID joueur
     */
    @Test
    public void testCreerNouveauJoueur() {
        // Vérifier que le joueur n'existe pas au départ
        List<String> joueursAvant = SaveManager.listerJoueurs();
        assertFalse("Le joueur de test ne doit pas exister au départ", 
                    joueursAvant.contains(TEST_PLAYER_NAME));
        
        // Créer les paramètres d'un nouveau joueur
        ParametresJoueur parametres = new ParametresJoueur(TEST_PLAYER_NAME, "Fr", 50, 0, 0);
        SaveManager.sauvegarderParametres(parametres);
        
        // Vérifier que le joueur existe maintenant
        List<String> joueursApres = SaveManager.listerJoueurs();
        assertTrue("Le joueur doit être créé après la sauvegarde", 
                   joueursApres.contains(TEST_PLAYER_NAME));
    }
    
    /**
     * Test 2: Sauvegarde et chargement des paramètres joueur
     */
    @Test
    public void testSauvegarderEtChargerParametres() {
        // Créer et sauvegarder des paramètres
        ParametresJoueur parametresOriginal = new ParametresJoueur(TEST_PLAYER_NAME, "En", 75, 2, 1);
        SaveManager.sauvegarderParametres(parametresOriginal);
        
        // Charger les paramètres
        ParametresJoueur parametresCharges = SaveManager.chargerParametres(TEST_PLAYER_NAME);
        
        // Vérifier que les paramètres ont été chargés correctement
        assertNotNull("Les paramètres doivent être chargés", parametresCharges);
        assertEquals("Le nom joueur doit correspondre", TEST_PLAYER_NAME, parametresCharges.getNomJoueur());
        assertEquals("La langue doit être 'En'", "En", parametresCharges.getLanguage());
        assertEquals("Le volume d'effet doit être 75", 75, parametresCharges.getVolumeEffet());
        assertEquals("Le mode sombre doit être 1", 1, parametresCharges.getModeSombre());
    }
    
    /**
     * Test 3: Chargement des paramètres pour un joueur inexistant
     */
    @Test
    public void testChargerParametresJoueurInexistant() {
        ParametresJoueur parametres = SaveManager.chargerParametres("JoueurInexistant");
        assertNull("Les paramètres d'un joueur inexistant doivent retourner null", parametres);
    }
    
    /**
     * Test 4: Sauvegarde et chargement d'une partie en cours
     */
    @Test
    public void testSauvegarderEtChargerPartie() {
        // Créer d'abord un joueur
        ParametresJoueur parametres = new ParametresJoueur(TEST_PLAYER_NAME, "Fr", 50, 0, 0);
        SaveManager.sauvegarderParametres(parametres);
        
        // Créer une partie de test avec les valeurs appropriées
        java.util.List<int[]> historique = new java.util.ArrayList<>();
        PartieSauvegardee partie = new PartieSauvegardee(null, historique, 12345);
        
        // Sauvegarder la partie
        SaveManager.sauvegarderPartie(TEST_PLAYER_NAME, "test", partie);
        
        // Charger la partie
        PartieSauvegardee partieChargee = SaveManager.chargerPartie(TEST_PLAYER_NAME, "test");
        
        // Vérifier que la partie a été chargée correctement
        assertNotNull("La partie sauvegardée doit être chargée", partieChargee);
        assertEquals("Le temps écoulé doit correspondre", 12345, partieChargee.getTempsEcoule());
        assertNotNull("L'historique doit être présent", partieChargee.getHistorique());
    }
    
    /**
     * Test 5: Chargement d'une partie inexistante
     */
    @Test
    public void testChargerPartieInexistante() {
        PartieSauvegardee partie = SaveManager.chargerPartie("JoueurInexistant", "levelInexistant");
        assertNull("Le chargement d'une partie inexistante doit retourner null", partie);
    }
    
    /**
     * Test 6: Enregistrement et chargement des meilleurs temps
     */
    @Test
    public void testEnregistrerMeilleurTemps() {
        // Créer un joueur
        ParametresJoueur parametres = new ParametresJoueur(TEST_PLAYER_NAME, "Fr", 50, 0, 0);
        SaveManager.sauvegarderParametres(parametres);
        
        // Enregistrer un meilleur temps
        long temps1 = 60000; // 1 minute
        SaveManager.enregistrerMeilleurTemps(TEST_PLAYER_NAME, "facile1", temps1);
        
        // Charger les meilleurs temps
        Map<String, Long> temps = SaveManager.chargerMeilleursTemps(TEST_PLAYER_NAME);
        
        assertNotNull("Le map des meilleurs temps doit exister", temps);
        assertTrue("Le niveau facile1 doit être dans le map", temps.containsKey("facile1"));
        assertEquals("Le meilleur temps doit correspondre", temps1, (long)temps.get("facile1"));
    }
    
    /**
     * Test 7: Mise à jour d'un meilleur temps si le nouveau temps est meilleur
     */
    @Test
    public void testMettreAJourMeilleurTemps() {
        // Créer un joueur
        ParametresJoueur parametres = new ParametresJoueur(TEST_PLAYER_NAME, "Fr", 50, 0, 0);
        SaveManager.sauvegarderParametres(parametres);
        
        // Enregistrer un premier temps
        SaveManager.enregistrerMeilleurTemps(TEST_PLAYER_NAME, "moyen1", 120000);
        
        // Enregistrer un temps meilleur
        SaveManager.enregistrerMeilleurTemps(TEST_PLAYER_NAME, "moyen1", 90000);
        
        // Vérifier que le meilleur temps a été mis à jour
        Map<String, Long> temps = SaveManager.chargerMeilleursTemps(TEST_PLAYER_NAME);
        assertEquals("Le meilleur temps doit être mis à jour", 90000, (long)temps.get("moyen1"));
    }
    
    /**
     * Test 8: Non mise à jour si le nouveau temps est moins bon
     */
    @Test
    public void testNePasMetAJourSiTempsPlusMauvais() {
        // Créer un joueur
        ParametresJoueur parametres = new ParametresJoueur(TEST_PLAYER_NAME, "Fr", 50, 0, 0);
        SaveManager.sauvegarderParametres(parametres);
        
        // Enregistrer un premier temps
        SaveManager.enregistrerMeilleurTemps(TEST_PLAYER_NAME, "moyen1", 90000);
        
        // Essayer d'enregistrer un temps plus mauvais
        SaveManager.enregistrerMeilleurTemps(TEST_PLAYER_NAME, "moyen1", 120000);
        
        // Vérifier que le meilleur temps n'a pas changé
        Map<String, Long> temps = SaveManager.chargerMeilleursTemps(TEST_PLAYER_NAME);
        assertEquals("Le meilleur temps ne doit pas changer si le nouveau est plus mauvais", 
                     90000, (long)temps.get("moyen1"));
    }
    
    /**
     * Test 9: Lister tous les joueurs
     */
    @Test
    public void testListerJoueurs() {
        // Créer plusieurs joueurs
        SaveManager.sauvegarderParametres(new ParametresJoueur("Joueur1", "Fr", 50, 0, 0));
        SaveManager.sauvegarderParametres(new ParametresJoueur("Joueur2", "En", 75, 1, 0));
        SaveManager.sauvegarderParametres(new ParametresJoueur("Joueur3", "Fr", 100, 2, 0));
        
        // Lister les joueurs
        List<String> joueurs = SaveManager.listerJoueurs();
        
        // Vérifier que tous les joueurs sont listés
        assertTrue("Joueur1 doit être dans la liste", joueurs.contains("Joueur1"));
        assertTrue("Joueur2 doit être dans la liste", joueurs.contains("Joueur2"));
        assertTrue("Joueur3 doit être dans la liste", joueurs.contains("Joueur3"));
        assertEquals("La liste doit contenir exactement 3 joueurs", 3, joueurs.size());
    }
    
    /**
     * Test 10: Lister les niveaux disponibles
     */
    @Test
    public void testListerNiveaux() {
        List<String> niveaux = SaveManager.listerIdsNiveaux();
        
        // Vérifier que la liste n'est pas vide (au moins quelques niveaux de demo)
        assertNotNull("La liste des niveaux ne doit pas être null", niveaux);
        assertFalse("La liste des niveaux doit contenir au moins un niveau", niveaux.isEmpty());
    }
    
    /**
     * Test 11: Charger un niveau existant
     */
    @Test
    public void testChargerNiveauExistant() {
        Niveau niveau = SaveManager.chargerNiveau("test");
        
        assertNotNull("Le niveau 'test' doit être chargeable", niveau);
        assertEquals("L'ID du niveau doit correspondre", "test", niveau.getId());
        assertTrue("La taille du niveau doit être positive", niveau.getTaille() > 0);
    }
    
    /**
     * Test 12: Charger un niveau inexistant
     */
    @Test
    public void testChargerNiveauInexistant() {
        Niveau niveau = SaveManager.chargerNiveau("levelInexistant123");
        
        assertNull("Le chargement d'un niveau inexistant doit retourner null", niveau);
    }
}
