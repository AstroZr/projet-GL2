package Groupe6.models;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class GrilleTest {

    @Test
    public void testGetTaille() {
        Grille grille = new Grille(4, null);
        assertEquals(4, grille.getTaille());
    }

    @Test
    public void testGetMatriceCellules() {
        int taille = 4;
        Grille grille = new Grille(taille, null);
        Cellule[][] matrice = grille.getMatriceCellules();

        assertNotNull(matrice);
        assertEquals(taille, matrice.length);
        assertEquals(taille, matrice[0].length);

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                assertNotNull(matrice[i][j]);
                assertEquals(i, matrice[i][j].getLigne());
                assertEquals(j, matrice[i][j].getColonne());
            }
        }
    }

    @Test
    public void testGetListeCellules() {
        int taille = 4;
        Grille grille = new Grille(taille, null);
        List<Cellule> liste = grille.getListeCellules();

        assertNotNull(liste);
        assertEquals(taille * taille, liste.size());

        // Vérifier l'ordre
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                Cellule c = liste.get(i * taille + j);
                assertEquals(i, c.getLigne());
                assertEquals(j, c.getColonne());
            }
        }
    }

    @Test
    public void testGetCellule() {
        Grille grille = new Grille(4, null);
        Cellule c = grille.getCellule(1, 2);
        assertNotNull(c);
        assertEquals(1, c.getLigne());
        assertEquals(2, c.getColonne());

        assertNull(grille.getCellule(-1, 0));
        assertNull(grille.getCellule(0, 4));
    }
}
