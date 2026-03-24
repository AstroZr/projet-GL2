package Groupe6.etats;

import org.junit.Test;
import static org.junit.Assert.*;

public class RecordsScrollTest {

    private int clampScroll(int scroll, int contentH, int visibleH) {
        return Math.max(0, Math.min(scroll, Math.max(0, contentH - visibleH)));
    }

    @Test
    public void testScrollNeDepassePasLeContenu() {
        // contenu 600px, visible 520px → max scroll = 80
        assertEquals(80, clampScroll(200, 600, 520));
    }

    @Test
    public void testScrollNeDescendPasEnDessousDe0() {
        assertEquals(0, clampScroll(-50, 600, 520));
    }

    @Test
    public void testScrollSansDebordement() {
        // contenu < visible → pas de scroll possible
        assertEquals(0, clampScroll(100, 300, 520));
    }

    @Test
    public void testScrollValeurNormale() {
        assertEquals(40, clampScroll(40, 600, 520));
    }
}
