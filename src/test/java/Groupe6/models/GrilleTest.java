package Groupe6.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import org.junit.Test;

public class GrilleTest {

  @Test
  public void testGetTaille() {
    Grille grille = new Grille("testUser", "Facile 1");
    assertEquals(4, grille.getTaille());
  }

  @Test
  public void testGetMatriceCellules() {
    int taille = 4;
    Grille grille = new Grille("testUser", "Facile 1");
    Cellule[][] matrice = grille.getMatriceCellules();
    assertNotNull(matrice);
    assertEquals(taille, matrice.length);
  }

  @Test
  public void testVerifierMathsDivision() {
    Grille grille = new Grille("testUser", "Facile 1");
    ZoneCalcul zone = new ZoneCalcul(2, TypeOperation.DIVISION);
    zone.ajouterCellule(grille.getCellule(0, 0));
    zone.ajouterCellule(grille.getCellule(0, 1));
    grille.getCellule(0, 0).setValeur(4);
    grille.getCellule(0, 1).setValeur(2);
    assertEquals(true, zone.verifierMaths());
  }

  @Test
  public void testVerifierMathsDivision2() {
    Grille grille = new Grille("testUser", "Facile 1");
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
    Grille grille = new Grille("testUser", "Facile 1");
    ZoneCalcul zone = new ZoneCalcul(3, TypeOperation.SOUSTRACTION);
    zone.ajouterCellule(grille.getCellule(0, 0));
    zone.ajouterCellule(grille.getCellule(0, 1));
    grille.getCellule(0, 0).setValeur(4);
    grille.getCellule(0, 1).setValeur(1);
    assertEquals(true, zone.verifierMaths());
  }

  @Test
  public void testVerifierMathsSoustraction2() {
    Grille grille = new Grille("testUser", "Facile 1");
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
    Grille grille = new Grille("testUser", "Facile 1");
    grille.ajouterCandidat(0, 0, 1);
    grille.ajouterCandidat(0, 0, 2);

    List<Integer> candidats = grille.getCellule(0, 0).getListeCandidat();
    assertEquals(2, candidats.size());

    grille.supprimerCandidat(0, 0, 1);
    assertFalse(grille.getCellule(0, 0).getListeCandidat().contains(1));
  }

  @Test
  public void testHistoriqueCoups() {
    Grille grille = new Grille("testUser", "Facile 1");

    grille.ajouterChiffre(0, 0, 4);
    grille.ajouterChiffre(0, 1, 3);

    grille.retourArriere();
    assertEquals(0, grille.getCellule(0, 1).getValeur());

    grille.retourAvant();
    assertEquals(3, grille.getCellule(0, 1).getValeur());

    grille.supprimerChiffre(0, 0);
    grille.retourArriere();
    assertEquals(4, grille.getCellule(0, 0).getValeur());
  }

  @Test
  public void testTrouverCombinaisonsZone() {
    Grille grille = new Grille("testUser", "Facile 1");
    ZoneCalcul zone = new ZoneCalcul(5, TypeOperation.ADDITION);

    zone.ajouterCellule(grille.getCellule(0, 0));
    zone.ajouterCellule(grille.getCellule(0, 1));

    List<List<Integer>> combos = zone.trouverCombinaisons(4);
    assertFalse(combos.isEmpty());

    grille.getCellule(0, 0).setValeur(2);
    List<List<Integer>> combosRestants = zone.trouverCombinaisons(4);
    assertEquals(1, combosRestants.size());
    assertEquals(3, (int) combosRestants.get(0).get(0));
  }

  @Test
  public void testHistoriqueVide() {
    Grille grille = new Grille("testUser", "Facile 1");
    try {
      grille.retourArriere();
      grille.retourAvant();
    } catch (Exception e) {
      fail("Erreur inattendue sur historique vide");
    }
  }
}
