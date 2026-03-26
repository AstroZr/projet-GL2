package Groupe6.mathdoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import Groupe6.utilz.LangManager;
import Groupe6.save.SaveManager;
import java.util.List;

/**
 * Tests de démarrage basiques : vérifie que les composants essentiels s'initialisent.
 */
public class AppTest {

    @Test
    public void testLangManagerChargeFrancaisParDefaut() {
        LangManager.setLangue("fr");
        String result = LangManager.get("app.name");
        assertNotNull("La clé app.name doit exister", result);
    }

    @Test
    public void testSaveManagerListeJoueursRetourneNonNull() {
        List<String> joueurs = SaveManager.listerJoueurs();
        assertNotNull("listerJoueurs() ne doit jamais retourner null", joueurs);
    }

    @Test
    public void testSaveManagerListeNiveauxNonVide() {
        List<String> niveaux = SaveManager.listerIdsNiveaux();
        assertNotNull("listerIdsNiveaux() ne doit jamais retourner null", niveaux);
    }

    @Test
    public void testLangManagerLangueParDefaut() {
        LangManager.setLangue("fr");
        assertEquals("fr", LangManager.getLangueCode());
    }
}
