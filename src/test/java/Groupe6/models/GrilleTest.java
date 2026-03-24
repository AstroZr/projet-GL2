package Groupe6.models;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class GrilleTest {

    @Test
    public void testGetTaille() {
        System.out.println("DEBUG: Test getTaille");
        Grille grille = new Grille("testUser", "test");
        assertEquals(4, grille.getTaille());
    }

    @Test
    public void testGetMatriceCellules() {
        System.out.println("DEBUG: Test getMatriceCellules");
        int taille = 4;
        Grille grille = new Grille("testUser", "test");
        Cellule[][] matrice = grille.getMatriceCellules();
        assertNotNull(matrice);
        assertEquals(taille, matrice.length);
    }

    @Test
    public void testVerifierMathsDivision() {
        System.out.println("DEBUG: Test verifier math division");
        Grille grille = new Grille("testUser", "test");
        ZoneCalcul zone = new ZoneCalcul(2, TypeOperation.DIVISION);
        zone.ajouterCellule(grille.getCellule(0, 0));
        zone.ajouterCellule(grille.getCellule(0, 1));
        grille.getCellule(0, 0).setValeur(4);
        grille.getCellule(0, 1).setValeur(2);
        assertEquals(true, zone.verifierMaths());
    }

    @Test
    public void testVerifierMathsDivision2() {
        System.out.println("DEBUG: Test verifier math division");
        Grille grille = new Grille("testUser", "test");
        ZoneCalcul zone = new ZoneCalcul(2, TypeOperation.DIVISION);
        zone.ajouterCellule(grille.getCellule(0, 0));
        zone.ajouterCellule(grille.getCellule(0, 1));
        zone.ajouterCellule(grille.getCellule(0, 2));
        grille.getCellule(0, 0).setValeur(4);
        grille.getCellule(0, 1).setValeur(2);
        grille.getCellule(0, 2).setValeur(1);
        assertEquals(true, zone.verifierMaths());
    }

    @Test
    public void testVerifierMathsSoustraction() {
        System.out.println("DEBUG: Test verifier math soustraction");
        Grille grille = new Grille("testUser", "test");
        ZoneCalcul zone = new ZoneCalcul(3, TypeOperation.SOUSTRACTION);
        zone.ajouterCellule(grille.getCellule(0, 0));
        zone.ajouterCellule(grille.getCellule(0, 1));
        grille.getCellule(0, 0).setValeur(4);
        grille.getCellule(0, 1).setValeur(1);
        assertEquals(true, zone.verifierMaths());
    }

    @Test
    public void testVerifierMathsSoustraction2() {
        System.out.println("DEBUG: Test verifier math soustraction");
        Grille grille = new Grille("testUser", "test");
        ZoneCalcul zone = new ZoneCalcul(1, TypeOperation.SOUSTRACTION);
        zone.ajouterCellule(grille.getCellule(0, 0));
        zone.ajouterCellule(grille.getCellule(0, 1));
        zone.ajouterCellule(grille.getCellule(0, 2));
        grille.getCellule(0, 0).setValeur(4);
        grille.getCellule(0, 1).setValeur(2);
        grille.getCellule(0, 2).setValeur(1);
        assertEquals(true, zone.verifierMaths());
    }

    @Test
    public void testGestionCandidats() {
        System.out.println("DEBUG: Test gestion candidats");
        Grille grille = new Grille("testUser", "test");
        grille.ajouterCandidat(0, 0, 1);
        grille.ajouterCandidat(0, 0, 2);

        List<Integer> candidats = grille.getCellule(0, 0).getListeCandidat();
        System.out.println("DEBUG: Candidats en [0,0]: " + candidats);
        assertEquals(2, candidats.size());

        grille.supprimerCandidat(0, 0, 1);
        System.out.println("DEBUG: Candidats après suppression: " + grille.getCellule(0, 0).getListeCandidat());
        assertFalse(grille.getCellule(0, 0).getListeCandidat().contains(1));
    }

    @Test
    public void testHistoriqueCoups() {
        System.out.println("DEBUG: Test historique coups");
        Grille grille = new Grille("testUser", "test");

        grille.ajouterChiffre(0, 0, 4);
        System.out.println("DEBUG: Coup 1 joué (4 en 0,0)");

        grille.ajouterChiffre(0, 1, 3);
        System.out.println("DEBUG: Coup 2 joué (3 en 0,1)");

        grille.retourArriere();
        System.out.println("DEBUG: Retour arrière effectué, valeur [0,1] est : " + grille.getCellule(0, 1).getValeur());
        assertEquals(0, grille.getCellule(0, 1).getValeur());

        grille.retourAvant();
        System.out.println("DEBUG: Retour avant effectué, valeur [0,1] est : " + grille.getCellule(0, 1).getValeur());
        assertEquals(3, grille.getCellule(0, 1).getValeur());

        grille.supprimerChiffre(0, 0);
        System.out.println("DEBUG: Suppression en [0,0], valeur est : " + grille.getCellule(0, 0).getValeur());

        grille.retourArriere();
        System.out.println("DEBUG: UNDO suppression en [0,0], valeur est : " + grille.getCellule(0, 0).getValeur());
        assertEquals(4, grille.getCellule(0, 0).getValeur());
    }

    @Test
    public void testTrouverCombinaisonsZone() {
        System.out.println("DEBUG: Test trouver combinaisons");
        Grille grille = new Grille("testUser", "test");
        ZoneCalcul zone = new ZoneCalcul(5, TypeOperation.ADDITION);

        zone.ajouterCellule(grille.getCellule(0, 0));
        zone.ajouterCellule(grille.getCellule(0, 1));

        List<List<Integer>> combos = zone.trouverCombinaisons(4);
        System.out.println("DEBUG: Combinaisons trouvées : " + combos);
        assertFalse(combos.isEmpty());

        grille.getCellule(0, 0).setValeur(2);
        List<List<Integer>> combosRestants = zone.trouverCombinaisons(4);
        System.out.println("DEBUG: Combinaisons avec cellule fixe : " + combosRestants);
        assertEquals(1, combosRestants.size());
        assertEquals(3, (int) combosRestants.get(0).get(0));
    }

    @Test
    public void testHistoriqueVide() {
        System.out.println("DEBUG: Test historique vide");
        Grille grille = new Grille("testUser", "test");
        try {
            grille.retourArriere();
            grille.retourAvant();
            System.out.println("DEBUG: Retour arrière/avant sur vide réussi sans erreur.");
        } catch (Exception e) {
            fail("Erreur inattendue sur historique vide");
        }
    }
}