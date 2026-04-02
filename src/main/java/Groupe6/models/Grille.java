package Groupe6.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Groupe6.aide.Aide;
import Groupe6.save.Niveau;
import Groupe6.save.ParametresJoueur;
import Groupe6.save.PartieSauvegardee;
import Groupe6.save.SaveManager;
import Groupe6.aide.AideManager;

/**
 * Modèle logique de la grille de jeu CalcuDoku/MathDoku.
 * 
 * Responsabilités:
 * - Gestion de l'état global du jeu (matrice de cellules, zones de calcul)
 * - Validation des règles: pas de doublons par ligne/colonne, contraintes
 * mathématiques des zones
 * - Système d'historique pour undo/redo (stocke chaque action)
 * - Pattern Observer pour notifier les observateurs de chaque changement
 * - Persistance via SaveManager (chargement/sauvegarde de parties)
 * 
 * Architecture:
 * - matriceCellules[ligne][colonne]: grille N×N de Cellule
 * - listeZones: List<ZoneCalcul> contenant les contraintes mathématiques
 * - historique: List<int[]> avec [ligne, colonne, anciennneVal, nouvelleVal,
 * actionType]
 * - indexActuel: pointeur dans l'historique (pour undo/redo)
 * - observers: notifiés à chaque modification (VueGrille, SoundManager, etc.)
 */
public class Grille {

    // ====== CONSTANTES D'ACTIONS (pour l'historique) ======
    public static final int ACTION_MOVE = 0; // Entrée d'une valeur normale
    public static final int ACTION_CANDIDAT = 1; // Ajout/suppression de candidat
    public static final int ACTION_AIDE = 2; // Utilisation d'une aide
    public static final int ACTION_AUCUNE = -1; // Aucune action

    // ====== PATTERN OBSERVER ======
    private List<GrilleObserver> observers = new ArrayList<>(); // Listeners notifiés des changements

    // ====== THREAD SAFETY ======
    private final ReadWriteLock celluleMatriceLock = new ReentrantReadWriteLock(); // Protège matriceCellules
    private final ReadWriteLock zonesLock = new ReentrantReadWriteLock(); // Protège listeZones

    // ====== DONNÉES DE LA GRILLE ======
    private final int taille; // Taille N de la grille N×N
    private Cellule[][] matriceCellules; // Grille principale (protégée par celluleMatriceLock)
    private List<ZoneCalcul> listeZones; // Zones avec contraintes (protégée par zonesLock)
    private int[][] matriceCorrection; // Grille solution pour vérification
    private int[][] matricePreRemplie; // Grille pré-remplie (pour aide/ajouter automatiquement des chiffres)

    // ====== SÉLECTION ======
    private Cellule celluleSelectionnee; // Cellule actuellement sélectionnée (null si aucune)

    // ====== HISTORIQUE & UNDO/REDO ======
    private int indexActuel = -1; // Index dans l'historique (pour undo/redo)
    private List<int[]> historique = new java.util.ArrayList<>(); // Enregistre toutes les actions

    // ====== MÉTADONNÉES ======
    private String nomJoueur; // Nom du joueur
    private String idNiveau; // ID du niveau (ex. "facile1", "moyen2")
    private long tempsEcoule; // Temps écoulé en ms depuis le début
    private AideManager aideManager; // Gestionnaire des hints/astuces

    // ====== PARAMÈTRES DE JEU ======
    private boolean afficherPossibilites; // Afficher/masquer les possibilités d'équations au survol
    private boolean afficherErreurDouble; // Afficher/masquer les cases rouges en cas de doublon

    // ====== ÉTAT ======
    private boolean estComplete; // true si grille complète et valide

    // ====== THREAD-SAFE HELPER METHODS ======

    /**
     * Accède à une cellule de façon thread-safe (lecture).
     * 
     * @param ligne   La ligne de la cellule
     * @param colonne La colonne de la cellule
     * @return La cellule à la position donnée
     */
    private Cellule getCelluleThreadSafe(int ligne, int colonne) {
        celluleMatriceLock.readLock().lock();
        try {
            return matriceCellules[ligne][colonne];
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
    }

    /**
     * Modifie une cellule de façon thread-safe (écriture).
     * 
     * @param ligne   La ligne de la cellule
     * @param colonne La colonne de la cellule
     * @param cellule La cellule à modifier
     */
    private void setCelluleThreadSafe(int ligne, int colonne, Cellule cellule) {
        celluleMatriceLock.writeLock().lock();
        try {
            matriceCellules[ligne][colonne] = cellule;
        } finally {
            celluleMatriceLock.writeLock().unlock();
        }
    }

    /**
     * Ajoute une zone de façon thread-safe.
     * 
     * @param zone La zone à ajouter
     */
    private void addZoneThreadSafe(ZoneCalcul zone) {
        zonesLock.writeLock().lock();
        try {
            listeZones.add(zone);
        } finally {
            zonesLock.writeLock().unlock();
        }
    }

    /**
     * Récupère la liste des zones de façon thread-safe (copie).
     * 
     * @return La liste des zones
     */
    private List<ZoneCalcul> getListeZonesThreadSafe() {
        zonesLock.readLock().lock();
        try {
            return new ArrayList<>(listeZones);
        } finally {
            zonesLock.readLock().unlock();
        }
    }

    /**
     * Constructeur pour instancier la grille directement à partir d'un
     * objet Niveau.
     * Ne gère ni l'utilisateur, ni les sauvegardes (pour le catalogue par exemple).
     * 
     * @param niveauBase L'objet niveau de base qui structure la grille.
     */
    public Grille(Niveau niveauBase) {
        this.nomJoueur = null;
        this.idNiveau = niveauBase != null ? niveauBase.getId() : null;

        // Valeurs par défaut au lieu de charger les paramètres de l'utilisateur
        this.afficherPossibilites = false;
        this.afficherErreurDouble = false;

        if (niveauBase == null) {
            this.taille = 4;
            this.matriceCellules = new Cellule[4][4];
            this.listeZones = new ArrayList<>();
            this.matriceCorrection = new int[4][4];
            this.estComplete = false;
            return;
        }

        this.aideManager = AideManager.getInstance();
        this.aideManager.setNBUtilisationsZero();

        this.taille = niveauBase.getTaille();
        initialiserDepuisNiveau(niveauBase);

        this.matricePreRemplie = niveauBase.getMatricePreRemplie();

        nettoyerSelection();
        validerGrille();
    }

    /**
     * Constructeur de la grille a partir d'un niveau ou d'une sauvegarde de partie.
     * 
     * @param nomJoueur Le nom du joueur
     * @param idNiveau  L'identifiant du niveau à charger
     */
    public Grille(String nomJoueur, String idNiveau) {
        this(nomJoueur, idNiveau, false);
    }

    /**
     * Constructeur de la grille a partir d'un niveau ou d'une sauvegarde de partie.
     * 
     * @param nomJoueur         Le nom du joueur
     * @param idNiveau          L'identifiant du niveau à charger
     * @param ignorerSauvegarde Si true, ignore toute sauvegarde existante
     */
    public Grille(String nomJoueur, String idNiveau, boolean ignorerSauvegarde) {
        this.nomJoueur = nomJoueur;
        this.idNiveau = idNiveau;

        // Charger les paramètres du joueur
        chargerParametresJoueur();

        // chargement de la base du niveau pour les zones de calcul
        Niveau niveauBase = SaveManager.chargerNiveau(idNiveau);

        // defaut si le niveau existe pas
        if (niveauBase == null) {
            this.taille = 4;
            this.matriceCellules = new Cellule[4][4];
            this.listeZones = new ArrayList<>();
            this.matriceCorrection = new int[4][4];
            this.estComplete = false;
            return;
        }

        PartieSauvegardee sauvegarde = chargerSauvegarde(nomJoueur, idNiveau, ignorerSauvegarde);

        this.aideManager = AideManager.getInstance();
        this.aideManager.setNBUtilisationsZero();

        this.taille = niveauBase.getTaille();
        if (sauvegarde != null) {
            initialiserDepuisSauvegarde(niveauBase, sauvegarde);
        } else {
            initialiserDepuisNiveau(niveauBase);
        }

        nettoyerSelection();
        validerGrille();
    }

    /**
     * Charge une sauvegarde de partie de façon thread-safe.
     * 
     * @param nomJoueur         Le nom du joueur
     * @param idNiveau          L'identifiant du niveau
     * @param ignorerSauvegarde Si true, ignore toute sauvegarde existante
     * @return La sauvegarde chargée ou null si aucune sauvegarde trouvée ou si
     *         ignorerSauvegarde est true
     */
    private PartieSauvegardee chargerSauvegarde(String nomJoueur, String idNiveau, boolean ignorerSauvegarde) {
        if (ignorerSauvegarde) {
            return null;
        }
        return SaveManager.chargerPartie(nomJoueur, idNiveau);
    }

    /**
     * Initialise la grille à partir d'une sauvegarde de partie.
     * 
     * @param niveauBase Le niveau de base contenant la structure des zones
     * @param sauvegarde La sauvegarde de partie à charger
     */
    private void initialiserDepuisSauvegarde(Niveau niveauBase, PartieSauvegardee sauvegarde) {
        this.matriceCellules = sauvegarde.getMatriceCellules();
        this.historique = sauvegarde.getHistorique();
        if (this.historique == null) {
            this.historique = new ArrayList<>();
        }
        this.listeZones = niveauBase.getListeZones();
        this.matriceCorrection = niveauBase.getMatriceCorrection();
        this.aideManager.setNBUtilisations(sauvegarde.getNbAidesUtilisees());
        this.tempsEcoule = sauvegarde.getTempsEcoule();
        this.indexActuel = this.historique.size() - 1;
        relierZonesAuxCellulesSauvegardees();
    }

    /**
     * Initialise la grille à partir d'un niveau de base.
     * 
     * @param niveauBase Le niveau de base contenant la structure de la grille
     */
    private void initialiserDepuisNiveau(Niveau niveauBase) {
        this.matriceCellules = niveauBase.getMatriceCellules();
        this.listeZones = niveauBase.getListeZones();
        this.matriceCorrection = niveauBase.getMatriceCorrection();
        this.historique = new ArrayList<>();
        this.tempsEcoule = 0L;
    }

    /**
     * Relie les zones aux cellules sauvegardées.
     */
    private void relierZonesAuxCellulesSauvegardees() {
        celluleMatriceLock.readLock().lock();
        zonesLock.readLock().lock();
        try {
            for (ZoneCalcul zone : listeZones) {
                for (Cellule celluleZone : zone.getListeCellules()) {
                    Cellule celluleSauvegardee = matriceCellules[celluleZone.getLigne()][celluleZone.getColonne()];
                    celluleSauvegardee.setZoneCalcul(zone);
                }
            }
        } finally {
            zonesLock.readLock().unlock();
            celluleMatriceLock.readLock().unlock();
        }
    }

    /**
     * Charge les paramètres du joueur depuis SaveManager.
     * Initialise les valeurs par défaut si les paramètres n'existent pas.
     */
    private void chargerParametresJoueur() {
        ParametresJoueur params = SaveManager.chargerParametres(nomJoueur);
        if (params != null) {
            this.afficherPossibilites = params.isAfficherPossibilites();
            this.afficherErreurDouble = params.isAfficherErreurDouble();
        } else {
            // Valeurs par défaut
            this.afficherPossibilites = false;
            this.afficherErreurDouble = false;
        }
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
        addZoneThreadSafe(zone);
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

        // Deselect only the previously selected cell to avoid an O(N^2) full-grid pass.
        if (celluleSelectionnee != null) {
            celluleSelectionnee.setEstSelectionnee(false);
        }

        // Sélectionner la nouvelle
        celluleSelectionnee = matriceCellules[ligne][colonne];
        celluleSelectionnee.setEstSelectionnee(true);
        notifierObservateurs();
    }

    private void nettoyerSelection() {
        celluleSelectionnee = null;
        if (matriceCellules == null) {
            return;
        }

        for (int ligne = 0; ligne < taille; ligne++) {
            for (int col = 0; col < taille; col++) {
                Cellule cellule = matriceCellules[ligne][col];
                if (cellule != null) {
                    cellule.setEstSelectionnee(false);
                }
            }
        }
    }

    /**
     * enregistre un coup
     * 
     * @param ligne          ligne du coup
     * @param colonne        colonne du coup
     * @param nouvelleValeur valeur modifier
     * @param estCandidat    si c'est un candidat
     */
    private void enregistrerCoup(int ligne, int colonne, int nouvelleValeur, boolean estCandidat) {
        int ancienneValeur = matriceCellules[ligne][colonne].getValeur();

        // Si on est au milieu de l'historique, on supprime le futur
        if (indexActuel < historique.size() - 1) {
            historique.subList(indexActuel + 1, historique.size()).clear();
        }

        historique.add(new int[] { ligne, colonne, ancienneValeur, nouvelleValeur,
                estCandidat ? ACTION_CANDIDAT : ACTION_MOVE });
        indexActuel++;
    }

    private void enregistrerCoupCandidat(int ligne, int colonne, int valeur, boolean ajout) {
        if (indexActuel < historique.size() - 1) {
            historique.subList(indexActuel + 1, historique.size()).clear();
        }
        historique.add(new int[] { ligne, colonne, valeur, ajout ? 1 : 0, ACTION_CANDIDAT });
        indexActuel++;
    }

    public void enregistrerUsageAide() {
        if (indexActuel < historique.size() - 1) {
            historique.subList(indexActuel + 1, historique.size()).clear();
        }
        historique.add(new int[] { -1, -1, 0, 0, ACTION_AIDE });
        indexActuel++;
        notifierObservateurs();
    }

    /**
     * Ajoute un chiffre dans la cellule sélectionnée (ou une cellule spécifique) -
     * thread-safe.
     * Vérifie immédiatement les contraintes de base (doublons).
     * 
     * @param ligne   Ligne cible
     * @param colonne Colonne cible
     * @param valeur  Valeur à insérer (1 à taille)
     */
    public void ajouterChiffre(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne))
            return;

        celluleMatriceLock.readLock().lock();
        try {
            Cellule cellule = matriceCellules[ligne][colonne];
            if (!cellule.estModifiable())
                return; // Si on a des cases pré-remplies (exemple le tuto ?)

            enregistrerCoup(ligne, colonne, valeur, false); // enregistre la modification
            cellule.setValeur(valeur);
            cellule.getListeCandidat().clear();
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
        validerGrille();
        this.aideManager.setNBUtilisationsZero();
        notifierObservateurs();
    }

    /**
     * Supprime le chiffre de la cellule spécifiée - thread-safe.
     */
    public void supprimerChiffre(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne))
            return;

        celluleMatriceLock.readLock().lock();
        try {
            Cellule cellule = matriceCellules[ligne][colonne];
            if (!cellule.estModifiable())
                return;

            enregistrerCoup(ligne, colonne, 0, false); // enregistre la modification
            cellule.setValeur(0);
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
        validerGrille();
        this.aideManager.setNBUtilisationsZero();
        notifierObservateurs();
    }

    /**
     * Valide l'état de la grille (détecte les doublons et valide les zones) -
     * thread-safe.
     */
    public void validerGrille() {
        celluleMatriceLock.writeLock().lock();
        zonesLock.readLock().lock();
        try {
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
                boolean zoneIncomplete = estZoneIncomplete(zone);

                // Si le calcul est faux, on marque les cellules comme invalides
                if (!estCalculValide) {
                    for (Cellule c : zone.getListeCellules()) {
                        // On ne marque invalide que si la zone est remplie
                        if (!zoneIncomplete) {
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
        } finally {
            zonesLock.readLock().unlock();
            celluleMatriceLock.writeLock().unlock();
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
        celluleMatriceLock.readLock().lock();
        try {
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    if (matriceCellules[i][j].estErreurDuplique() || !matriceCellules[i][j].estValide())
                        return true;
                }
            }
            return false;
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
    }

    /**
     * Vérifie les doublons dans une ligne.
     * 
     * @param ligne La ligne à vérifier
     */
    private void verifierDoublonsLigne(int ligne) {
        /*
        if (!afficherErreurDouble) {
            return; // Ne pas marquer les doublons s'ils sont désactivés
        }
        */

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
        
        /*
        if (!afficherErreurDouble) {
            return; // Ne pas marquer les doublons s'ils sont désactivés
        }
        */

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
     * Retourne la cellule aux coordonnées données (thread-safe)
     * 
     * @param ligne   Ligne de la cellule
     * @param colonne Colonne de la cellule
     * @return la cellule aux coordonnées données
     */
    public Cellule getCellule(int ligne, int colonne) {
        if (estHorsLimites(ligne, colonne))
            return null;
        return getCelluleThreadSafe(ligne, colonne);
    }

    /**
     * Retourne la matrice complète des cellules (copie thread-safe).
     * 
     * @return une copie de la matrice des cellules
     */
    public Cellule[][] getMatriceCellules() {
        celluleMatriceLock.readLock().lock();
        try {
            Cellule[][] copie = new Cellule[taille][taille];
            for (int i = 0; i < taille; i++) {
                System.arraycopy(matriceCellules[i], 0, copie[i], 0, taille);
            }
            return copie;
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
    }

    /**
     * Retourne une liste plate de toutes les cellules de la grille (thread-safe).
     * 
     * @return la liste de toutes les cellules
     */
    public List<Cellule> getListeCellules() {
        celluleMatriceLock.readLock().lock();
        try {
            List<Cellule> liste = new ArrayList<>(taille * taille);
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    liste.add(matriceCellules[i][j]);
                }
            }
            return liste;
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
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
     * Retourne la liste des zones de calcul (copie thread-safe)
     * 
     * @return une copie de la liste des zones de calcul
     */
    public List<ZoneCalcul> getListeZones() {
        return getListeZonesThreadSafe();
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
     * Retourne la matrice pré-remplie
     * 
     * @return la matrice pré-remplie
     */
    public int[][] getMatricePreRemplie() {
        return matricePreRemplie;
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
     * Remplit automatiquement les cases vides avec les valeurs de la matrice
     * pré-remplie
     */
    public void autoRemplissage() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (matriceCellules[i][j].getValeur() == 0) {
                    matriceCellules[i][j].setValeur(matricePreRemplie[i][j]);
                }
            }
        }
    }

    /**
     * Vérifie si la grille est complète
     * 
     * @return true si la grille est complète, false sinon
     */
    public boolean estComplete() {
        celluleMatriceLock.readLock().lock();
        try {
            if (matriceCorrection == null)
                return estComplete;
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    if (matriceCellules[i][j].getValeur() != matriceCorrection[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        } finally {
            celluleMatriceLock.readLock().unlock();
        }
    }

    /**
     * Retourne si les possibilités d'équations doivent être affichées
     * 
     * @return true si les possibilités doivent être affichées, false sinon
     */
    public boolean isAfficherPossibilites() {
        return afficherPossibilites;
    }

    /**
     * Retourne si les erreurs de doublon doivent être affichées
     * 
     * @return true si les erreurs de doublon doivent être affichées, false sinon
     */
    public boolean isAfficherErreurDouble() {
        return afficherErreurDouble;
    }

    /**
     * Met à jour l'affichage des possibilités d'équations et revalide la grille
     * 
     * @param afficher true pour afficher, false pour masquer
     */
    public void setAfficherPossibilites(boolean afficher) {
        this.afficherPossibilites = afficher;
    }

    /**
     * Met à jour l'affichage des erreurs de doublon et revalide la grille
     * 
     * @param afficher true pour afficher, false pour masquer
     */
    public void setAfficherErreurDouble(boolean afficher) {
        this.afficherErreurDouble = afficher;
        // Revalider la grille pour recalculer les doublons
        validerGrille();
        notifierObservateurs();
    }

    /**
     * recul dans la pile de coup
     */
    public int retourArriere() {
        if (indexActuel < 0)
            return ACTION_AUCUNE;

        int[] coup = historique.get(indexActuel);
        int typeAction = coup[4];
        if (typeAction == ACTION_MOVE) {
            matriceCellules[coup[0]][coup[1]].setValeur(coup[2]); // Restaure ancienneValeur
        } else if (typeAction == ACTION_CANDIDAT) {
            Cellule cellule = matriceCellules[coup[0]][coup[1]];
            int valeur = coup[2];
            boolean ajout = coup[3] == 1;
            if (ajout) {
                cellule.getListeCandidat().remove(Integer.valueOf(valeur));
            } else if (!cellule.getListeCandidat().contains(valeur)) {
                cellule.getListeCandidat().add(valeur);
            }
        }
        indexActuel--;

        validerGrille();
        notifierObservateurs();
        return typeAction;
    }

    /**
     * avance dans la pile de coup
     */
    public int retourAvant() {
        if (indexActuel >= historique.size() - 1)
            return ACTION_AUCUNE;

        indexActuel++;
        int[] coup = historique.get(indexActuel);
        int typeAction = coup[4];
        if (typeAction == ACTION_MOVE) {
            matriceCellules[coup[0]][coup[1]].setValeur(coup[3]); // Applique nouvelleValeur
        } else if (typeAction == ACTION_CANDIDAT) {
            Cellule cellule = matriceCellules[coup[0]][coup[1]];
            int valeur = coup[2];
            boolean ajout = coup[3] == 1;
            if (ajout) {
                if (!cellule.getListeCandidat().contains(valeur)) {
                    cellule.getListeCandidat().add(valeur);
                }
            } else {
                cellule.getListeCandidat().remove(Integer.valueOf(valeur));
            }
        }

        validerGrille();
        notifierObservateurs();
        return typeAction;
    }

    /**
     * ajoute un candidat dans une cellule donne
     * 
     * @param ligne   ligne de la cellule
     * @param colonne colonne de la cellule
     * @param valeur  valeur du candidat à ajouter
     */
    public void ajouterCandidat(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne))
            return;

        Cellule cellule = matriceCellules[ligne][colonne];
        // On n'ajoute que si la valeur n'est pas déjà présente
        if (!cellule.getListeCandidat().contains(valeur)) {
            cellule.getListeCandidat().add(valeur);

            enregistrerCoupCandidat(ligne, colonne, valeur, true);
            notifierObservateurs();
        }
    }

    /**
     * supprime un candidat d une cellule donnee
     * 
     * @param ligne   ligne de la cellule
     * @param colonne colonne de la cellule
     * @param valeur  valeur du candidat à supprimer
     */
    public void supprimerCandidat(int ligne, int colonne, int valeur) {
        if (estHorsLimites(ligne, colonne))
            return;

        Cellule cellule = matriceCellules[ligne][colonne];
        if (cellule.getListeCandidat().contains(valeur)) {
            cellule.getListeCandidat().remove(Integer.valueOf(valeur));

            enregistrerCoupCandidat(ligne, colonne, valeur, false);
            notifierObservateurs();
        }
    }

    /**
     * Retourne le temps écoulé en millisecondes.
     * 
     * @return Le temps écoulé en millisecondes
     */
    public long getTempsEcoule() {
        return tempsEcoule;
    }

    /**
     * Définit le temps écoulé en millisecondes.
     * 
     * @param tempsEcoule Le temps écoulé en millisecondes
     */
    public void setTempsEcoule(long tempsEcoule) {
        this.tempsEcoule = Math.max(0L, tempsEcoule);
    }

    /**
     * Sauvegarde la grille actuelle.
     */
    public void saveGrille() {
        PartieSauvegardee ps = new PartieSauvegardee(
                this.matriceCellules,
                this.historique,
                this.tempsEcoule);

        SaveManager.sauvegarderPartie(nomJoueur, idNiveau, ps);
    }
}