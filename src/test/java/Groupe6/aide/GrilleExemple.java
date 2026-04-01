package Groupe6.aide;

import org.junit.Test;

public class GrilleExemple {
    @Test
    public void afficherGrilleExempleReste() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Affichage de la Grille d'Exemple 'Reste' ===\n");
        Groupe6.models.Grille grille = new Groupe6.aide.techniques.Reste().getGrilleExemple();

        for (int i = 0; i < grille.getTaille(); i++) {
            for (int j = 0; j < grille.getTaille(); j++) {
                if (grille.getCellule(i, j).estVide()) {
                    sb.append("[ ] ");
                } else {
                    sb.append("[").append(grille.getCellule(i, j).getValeur()).append("] ");
                }
            }
            sb.append("\n");
        }
        sb.append("================================================\n");

        System.out.println(sb.toString());
    }
}
