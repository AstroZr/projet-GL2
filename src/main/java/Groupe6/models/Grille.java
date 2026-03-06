package Groupe6.models;

import java.util.ArrayList;
import java.util.List;

public class Grille {
    private final int taille;
    private final Cellule[][] matriceCellules;
    private final List<ZoneCalcul> listeZones;
    private Cellule celluleSelectionnee;

    private boolean estComplete;

    public Grille(int taille, String nomFichier) {
        this.taille = taille;
        this.matriceCellules = new Cellule[taille][taille];
        this.listeZones = new ArrayList<>();
        this.estComplete = false;

        initialiserCellules(nomFichier);
    }

    private void initialiserCellules(String nomFichier) {
        if (nomFichier == null) {
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    matriceCellules[i][j] = new Cellule(i, j);
                }
            }
        } else {
            // TODO: Initialiser la grille à partir d'un fichier
        }
    }

    public void ajouterZone(ZoneCalcul zone) {
        listeZones.add(zone);
    }

    public void selectionnerCellule(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne)) return;
        if (celluleSelectionnee != null) {
            celluleSelectionnee.setEstSelectionnee(false);
        }
        celluleSelectionnee = matriceCellules[ligne][colonne];
        celluleSelectionnee.setEstSelectionnee(true);
    }

    public void ajouterChiffre(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne)) return;
        Cellule cellule = matriceCellules[ligne][colonne];
        if (!cellule.estModifiable()) return;
        cellule.setValeur(valeur);
        validerGrille();
    }

    public void supprimerChiffre(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne)) return;
        Cellule cellule = matriceCellules[ligne][colonne];
        if (!cellule.estModifiable()) return;
        cellule.setValeur(0);
        validerGrille();
    }

    public void validerGrille() {
        estComplete = true;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                matriceCellules[i][j].setEstErreurDuplique(false);
                matriceCellules[i][j].setEstValide(true);
                if (matriceCellules[i][j].estVide()) {
                    estComplete = false;
                }
            }
        }
        for (int i = 0; i < taille; i++) {
            verifierDoublonsLigne(i);
            verifierDoublonsColonne(i);
        }
        for (ZoneCalcul zone : listeZones) {
            boolean estCalculValide = zone.verifierMaths();
            if (!estCalculValide) {
                for (Cellule c : zone.getListeCellules()) {
                    if (!estZoneIncomplete(zone)) {
                        c.setEstValide(false);
                        estComplete = false;
                    }
                }
            }
        }
        if (aDesErreurs()) {
            estComplete = false;
        }
    }

    private boolean estZoneIncomplete(ZoneCalcul zone) {
        for (Cellule c : zone.getListeCellules()) {
            if (c.estVide()) return true;
        }
        return false;
    }

    private boolean aDesErreurs() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (matriceCellules[i][j].estErreurDuplique() || !matriceCellules[i][j].estValide())
                    return true;
            }
        }
        return false;
    }

    private void verifierDoublonsLigne(int ligne) {
        int[] comptes = new int[taille + 1];
        for (int col = 0; col < taille; col++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0) comptes[val]++;
        }
        for (int col = 0; col < taille; col++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0 && comptes[val] > 1) {
                matriceCellules[ligne][col].setEstErreurDuplique(true);
            }
        }
    }

    private void verifierDoublonsColonne(int col) {
        int[] comptes = new int[taille + 1];
        for (int ligne = 0; ligne < taille; ligne++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0) comptes[val]++;
        }
        for (int ligne = 0; ligne < taille; ligne++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0 && comptes[val] > 1) {
                matriceCellules[ligne][col].setEstErreurDuplique(true);
            }
        }
    }

    private boolean estHorsLimites(int ligne, int colonne) {
        return ligne < 0 || ligne >= taille || colonne < 0 || colonne >= taille;
    }

    // === NOUVEAU : IMPORT / EXPORT POUR LA SAUVEGARDE ===

    public int[][] exporterVersTableau() {
        int[][] tableau = new int[taille][taille];
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                tableau[i][j] = matriceCellules[i][j].getValeur();
            }
        }
        return tableau;
    }

    public void importerDepuisTableau(int[][] tableau) {
        if (tableau == null || tableau.length != taille) return;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                ajouterChiffre(i, j, tableau[i][j]);
            }
        }
    }

    // === GETTERS ===
    public int getTaille() { return taille; }
    public Cellule getCellule(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne)) return null;
        return matriceCellules[ligne][colonne];
    }
    public Cellule getCelluleSelectionnee() { return celluleSelectionnee; }
    public List<ZoneCalcul> getListeZones() { return listeZones; }
    public boolean estComplete() { return estComplete; }
}
