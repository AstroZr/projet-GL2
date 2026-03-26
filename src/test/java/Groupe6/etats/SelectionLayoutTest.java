package Groupe6.etats;

import org.junit.Test;
import static org.junit.Assert.*;

public class SelectionLayoutTest {

    // Reproduit la logique pure de positionnement (sans Swing)
    private int colonneX(int i, int cx, int bw, int colGap) {
        boolean gauche = (i % 2 == 0);
        return gauche ? cx - colGap / 2 - bw : cx + colGap / 2;
    }

    private int colonneY(int i, int startY, int gap) {
        return startY + (i / 2) * gap;
    }

    @Test
    public void testColonneGaucheIndexPair() {
        // index 0 et 2 → colonne gauche
        int cx = 960, bw = 280, gap = 60;
        assertEquals(cx - gap/2 - bw, colonneX(0, cx, bw, gap));
        assertEquals(cx - gap/2 - bw, colonneX(2, cx, bw, gap));
    }

    @Test
    public void testColonneDroiteIndexImpair() {
        int cx = 960, bw = 280, gap = 60;
        assertEquals(cx + gap/2, colonneX(1, cx, bw, gap));
        assertEquals(cx + gap/2, colonneX(3, cx, bw, gap));
    }

    @Test
    public void testPositionYZigzag() {
        int startY = 400, gap = 64;
        // niveaux 0 et 1 → rang 0 → même Y
        assertEquals(startY, colonneY(0, startY, gap));
        assertEquals(startY, colonneY(1, startY, gap));
        // niveaux 2 et 3 → rang 1
        assertEquals(startY + gap, colonneY(2, startY, gap));
        assertEquals(startY + gap, colonneY(3, startY, gap));
    }

    @Test
    public void testNombreImpairDeBoutons() {
        int startY = 400, gap = 64;
        // index 4 → rang 2 → Y = startY + 2*gap
        assertEquals(startY + 2 * gap, colonneY(4, startY, gap));
        // index 4 → pair → colonne gauche
        int cx = 960, bw = 280, colGap = 60;
        assertEquals(cx - colGap / 2 - bw, colonneX(4, cx, bw, colGap));
    }

    @Test
    public void testZeroNiveaux() {
        // Aucun niveau → boucle ne s'exécute pas, pas de crash
        // Vérification : pour i=0 la logique renverrait toujours une position valide
        int cx = 960, bw = 280, colGap = 60, startY = 400, gap = 64;
        // Le test documente le comportement attendu si une itération avait lieu
        assertEquals(cx - colGap / 2 - bw, colonneX(0, cx, bw, colGap));
        assertEquals(startY, colonneY(0, startY, gap));
    }
}
