package Groupe6.models;

import java.util.ArrayList;
import java.util.List;

import Groupe6.save.Niveau;
import Groupe6.save.PartieSauvegardee;
import Groupe6.save.SaveManager;

/**
 * Modèle logique de la grille de jeu.
 * Gère l'état global, les règles et la validation.
 */
public class Grille {
    private List<GrilleObserver> observers = new ArrayList<>();

    private final int taille;
    private final Cellule[][] matriceCellules;
    private final List<ZoneCalcul> listeZones;
    private Cellule celluleSelectionnee;
    private int indexActuel = -1;
    private List<int[]> historique = new java.util.ArrayList<>();
    private String nomJoueur;
    private String idNiveau;

    // Etat du jeu
    private boolean estComplete;

    /**
     * Constructeur de la grille a partir d'un niveau ou d'une sauvegarde de partie.
     * 
     * @param nomJoueur Le nom du joueur 
     * @param idNiveau  L'identifiant du niveau à charger
     */
    public Grille(String nomJoueur, String idNiveau) {
        this.nomJoueur = nomJoueur;
        this.idNiveau = idNiveau;
        // chargement de la base du niveau  pour les zones de calcul
        Niveau niveauBase = SaveManager.chargerNiveau(idNiveau);
        
        // defaut si le niveau existe pas
        if (niveauBase == null) {
            this.taille = 4;
            this.matriceCellules = new Cellule[4][4];
            this.listeZones = new ArrayList<>();
            this.estComplete = false;
            return;
        }

        PartieSauvegardee sauvegarde = SaveManager.chargerPartie(nomJoueur, idNiveau);
        
        this.taille = niveauBase.getTaille();
        if (sauvegarde != null) {
            // charge la sauvegarde
            
            this.matriceCellules = sauvegarde.getMatriceCellules();
            this.historique = sauvegarde.getHistorique();
            this.listeZones = niveauBase.getListeZones();
            
            // relie les zones de niveauBase aux cellules de la sauvegarde
            for (ZoneCalcul zone : listeZones) {
                for (Cellule c : zone.getListeCellules()) {
                    Cellule celluleSauvegardee = matriceCellules[c.getLigne()][c.getColonne()];
                    celluleSauvegardee.setZoneCalcul(zone);
                }
            }
        } else {
            // Pas de sauvegarde 
            this.matriceCellules = niveauBase.getMatriceCellules();
            this.listeZones = niveauBase.getListeZones();
            this.historique = new ArrayList<>();
        }
        
        this.estComplete = false;
    }

    /**
     * Ajoute un observateur à la liste des observateurs.
     * 
     * @param observer L'observateur à ajouter
     */
    public void ajouterObservateur(GrilleObserver observer) {
        observers.add(observer);
    }

    /**
     * Supprime un observateur de la liste.
     * 
     * @param observer L'observateur à supprimer
     */
    public void supprimerObservateur(GrilleObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs que la grille a changé.
     */
    public void notifierObservateurs() {
        for (GrilleObserver obs : observers) {
            obs.onGrilleChanged();
        }
    }

    /**
     * Initialise la matrice de cellules vides.
     */
    private void initialiserCellulesVides() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                matriceCellules[i][j] = new Cellule(i, j);
            }
        }
    }

    /**
     * Ajoute une zone de calcul à la grille.
     * 
     * @param zone La zone à ajouter
     */
    public void ajouterZone(ZoneCalcul zone) {
        listeZones.add(zone);
    }

    // === GESTION DU JEU ===

    /**
     * Sélectionne une cellule aux coordonnées données.
     * 
     * @param ligne   Ligne de la cellule
     * @param colonne Colonne de la cellule
     */
    public void selectionnerCellule(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne))
            return;

        // Désélectionner l'ancienne
        if (celluleSelectionnee != null) {
            celluleSelectionnee.setEstSelectionnee(false);
        }

        // Sélectionner la nouvelle
        celluleSelectionnee = matriceCellules[ligne][colonne];
        celluleSelectionnee.setEstSelectionnee(true);
        notifierObservateurs();
    }

    /**
     * enregistre un coup
     * @param ligne ligne du coup
     * @param colonne colonne du coup 
     * @param nouvelleValeur valeur modifier
     */
    private void enregistrerCoup(int ligne, int colonne, int nouvelleValeur) {
        int ancienneValeur = matriceCellules[ligne][colonne].getValeur();
        
        // Si on est au milieu de l'historique, on supprime le futur
        if (indexActuel < historique.size() - 1) {
            historique.subList(indexActuel + 1, historique.size()).clear();
        }
        
        historique.add(new int[]{ligne, colonne, ancienneValeur, nouvelleValeur});
        indexActuel++;
    }

    /**
     * Ajoute un chiffre dans la cellule sélectionnée (ou une cellule spécifique).
     * Vérifie immédiatement les contraintes de base (doublons).
     * 
     * @param ligne   Ligne cible
     * @param colonne Colonne cible
     * @param valeur  Valeur à insérer (1 à taille)
     */
    public void ajouterChiffre(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne))
            return;

        Cellule cellule = matriceCellules[ligne][colonne];
        if (!cellule.estModifiable())
            return; // Si on a des cases pré-remplies (exemple le tuto ?)

        enregistrerCoup(ligne,colonne,valeur); //enregistre la modification
        cellule.setValeur(valeur);
        validerGrille();
        notifierObservateurs();
    }

    /**
     * Supprime le chiffre de la cellule spécifiée.
     */
    public void supprimerChiffre(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne))
            return;

        Cellule cellule = matriceCellules[ligne][colonne];
        if (!cellule.estModifiable())
            return; 

        enregistrerCoup(ligne,colonne,0); //enregistre la modification
        cellule.setValeur(0);
        validerGrille();
        notifierObservateurs();
    }

    /**
     * Valide l'état de la grille (détecte les doublons et valide les zones).
     */
    public void validerGrille() {
        estComplete = true;

        // Réinitialiser les erreurs
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                matriceCellules[i][j].setEstErreurDuplique(false);
                matriceCellules[i][j].setEstValide(true);
                if (matriceCellules[i][j].estVide()) {
                    estComplete = false;
                }
            }
        }

        // Vérifier les doublons (lignes et colonnes)
        for (int i = 0; i < taille; i++) {
            verifierDoublonsLigne(i);
            verifierDoublonsColonne(i);
        }

        // Vérifier les zones via la méthode verifierMaths()
        for (ZoneCalcul zone : listeZones) {
            boolean estCalculValide = zone.verifierMaths();

            // Si le calcul est faux, on marque les cellules comme invalides
            if (!estCalculValide) {
                for (Cellule c : zone.getListeCellules()) {
                    // On ne marque invalide que si la zone est remplie
                    if (!estZoneIncomplete(zone)) {
                        c.setEstValide(false);
                        estComplete = false;
                    }
                }
            }
        }

        // Dernier contrôle : si on a des erreurs, le jeu n'est pas fini
        if (aDesErreurs()) {
            estComplete = false;
        }
    }

    /**
     * Vérifie si une zone est incomplète.
     * 
     * @param zone La zone à vérifier
     * @return true si la zone est incomplète, false sinon
     */
    private boolean estZoneIncomplete(ZoneCalcul zone) {
        for (Cellule c : zone.getListeCellules()) {
            if (c.estVide())
                return true;
        }
        return false;
    }

    /**
     * Vérifie si la grille a des erreurs.
     * 
     * @return true si la grille a des erreurs, false sinon
     */
    private boolean aDesErreurs() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (matriceCellules[i][j].estErreurDuplique() || !matriceCellules[i][j].estValide())
                    return true;
            }
        }
        return false;
    }

    /**
     * Vérifie les doublons dans une ligne.
     * 
     * @param ligne La ligne à vérifier
     */
    private void verifierDoublonsLigne(int ligne) {
        int[] comptes = new int[taille + 1];

        // Compter les occurrences
        for (int col = 0; col < taille; col++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0)
                comptes[val]++;
        }

        // Marquer les erreurs
        for (int col = 0; col < taille; col++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0 && comptes[val] > 1) {
                matriceCellules[ligne][col].setEstErreurDuplique(true);
            }
        }
    }

    /**
     * Vérifie les doublons dans une colonne.
     * 
     * @param col La colonne à vérifier
     */
    private void verifierDoublonsColonne(int col) {
        int[] comptes = new int[taille + 1];

        // Compter les occurrences
        for (int ligne = 0; ligne < taille; ligne++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0)
                comptes[val]++;
        }

        // Marquer les erreurs
        for (int ligne = 0; ligne < taille; ligne++) {
            int val = matriceCellules[ligne][col].getValeur();
            if (val != 0 && comptes[val] > 1) {
                matriceCellules[ligne][col].setEstErreurDuplique(true);
            }
        }
    }

    /**
     * Vérifie si les coordonnées sont hors limites.
     * 
     * @param ligne   Ligne à vérifier
     * @param colonne Colonne à vérifier
     * @return true si les coordonnées sont hors limites, false sinon
     */
    private boolean estHorsLimites(int ligne, int colonne) {
        return ligne < 0 || ligne >= taille || colonne < 0 || colonne >= taille;
    }

    // === GETTERS ===
    /**
     * Retourne la taille de la grille
     * 
     * @return la taille de la grille
     */
    public int getTaille() {
        return taille;
    }

    /**
     * Retourne la cellule aux coordonnées données
     * 
     * @param ligne   Ligne de la cellule
     * @param colonne Colonne de la cellule
     * @return la cellule aux coordonnées données
     */
    public Cellule getCellule(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne))
            return null;
        return matriceCellules[ligne][colonne];
    }

    /**
     * Retourne la matrice complète des cellules.
     * 
     * @return la matrice des cellules
     */
    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    /**
     * Retourne une liste plate de toutes les cellules de la grille.
     * 
     * @return la liste de toutes les cellules
     */
    public List<Cellule> getListeCellules() {
        List<Cellule> liste = new ArrayList<>(taille * taille);
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                liste.add(matriceCellules[i][j]);
            }
        }
        return liste;
    }

    /**
     * Retourne la cellule sélectionnée
     * 
     * @return la cellule sélectionnée
     */
    public Cellule getCelluleSelectionnee() {
        return celluleSelectionnee;
    }

    /**
     * Retourne la liste des zones de calcul
     * 
     * @return la liste des zones de calcul
     */
    public List<ZoneCalcul> getListeZones() {
        return listeZones;
    }

    /**
     * Retourne le nom du joueur
     * 
     * @return le nom du joueur
     */
    public String getNomJoueur() {
        return nomJoueur;
    }

    /**
     * Retourne l'identifiant du niveau
     * 
     * @return l'identifiant du niveau
     */
    public String getIdNiveau() {
        return idNiveau;
    }



    /**
     * Vérifie si la grille est complète
     * 
     * @return true si la grille est complète, false sinon
     */
    public boolean estComplete() {
        return estComplete;
    }

    /**
     * recul dans la pile de coup 
     */
    public void retourArriere() {
        if (indexActuel < 0) return;
        
        int[] coup = historique.get(indexActuel);
        matriceCellules[coup[0]][coup[1]].setValeur(coup[2]); // Restaure ancienneValeur
        indexActuel--;
        
        validerGrille();
        notifierObservateurs();
    }

    /**
     * avance dans la pile de coup 
     */
    public void retourAvant() {
        if (indexActuel >= historique.size() - 1) return;
        
        indexActuel++;
        int[] coup = historique.get(indexActuel);
        matriceCellules[coup[0]][coup[1]].setValeur(coup[3]); // Applique nouvelleValeur
        
        validerGrille();
        notifierObservateurs();
    }


    /**
     * ajoute un candidat dans une cellule donne
     * @param ligne   ligne de la cellule
     * @param colonne colonne de la cellule
     * @param valeur  valeur du candidat à ajouter
     */
    public void ajouterCandidat(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne)) return;

        Cellule cellule = matriceCellules[ligne][colonne];
        // On n'ajoute que si la valeur n'est pas déjà présente
        if (!cellule.getListeCandidat().contains(valeur)) {
            cellule.getListeCandidat().add(valeur);
            notifierObservateurs();
        }
    }

    /**
     * supprime un candidat d une cellule donnee
     * @param ligne   ligne de la cellule
     * @param colonne colonne de la cellule
     * @param valeur  valeur du candidat à supprimer
     */
    public void supprimerCandidat(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne)) return;

        Cellule cellule = matriceCellules[ligne][colonne];
        if (cellule.getListeCandidat().contains(valeur)) {
            cellule.getListeCandidat().remove(Integer.valueOf(valeur));
            notifierObservateurs();
        }
    }

    public void saveGrille(){
        PartieSauvegardee ps = new PartieSauvegardee(
                    this.matriceCellules,
                    this.historique,
                    0 // temps ecouler a faire
                );
        
        SaveManager.sauvegarderPartie(nomJoueur, idNiveau, ps);
    }
}