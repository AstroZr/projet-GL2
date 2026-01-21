package Groupe6.ui;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Classe abstraite représentant un bouton de l'interface utilisateur.
 * 
 * FONCTIONNALITÉS :
 * - Gère la position, la taille et la zone de collision du bouton
 * - Supporte plusieurs images pour différents états (normal, survol, enfoncé)
 * - Détecte les interactions de la souris (survol, clic)
 * - Permet la personnalisation via l'index d'image
 * 
 * ARCHITECTURE :
 * - Classe abstraite à étendre pour créer des boutons spécifiques
 * - Utilise un Rectangle pour la détection de collision avec la souris
 * - Pattern Template Method : les sous-classes implémentent le rendu et l'action
 * 
 * UTILISATION :
 * Les sous-classes doivent implémenter les méthodes abstraites pour :
 * - Le rendu du bouton (draw)
 * - L'action à effectuer lors du clic (onClick)
 */
public abstract class Bouton {
    // === PROPRIÉTÉS GÉOMÉTRIQUES ===
    /** Position X du bouton sur l'écran (coin supérieur gauche) */
    protected int x;
    /** Position Y du bouton sur l'écran (coin supérieur gauche) */
    protected int y;
    /** Largeur du bouton en pixels */
    protected int largeur;
    /** Hauteur du bouton en pixels */
    protected int hauteur;
    
    // === ZONE DE DÉTECTION ===
    /** Rectangle de délimitation utilisé pour la détection de collision avec la souris */
    protected Rectangle delimitation;
    
    // === GESTION DES ÉTATS ===
    /** Index de l'image actuelle à afficher dans le tableau d'images */
    protected int index;
    /** Tableau d'images représentant les différents états du bouton */
    protected BufferedImage[] img;
    
    // === ÉTATS D'INTERACTION ===
    /** Indique si la souris survole actuellement le bouton */
    protected boolean sourisSurvol;
    /** Indique si un bouton de la souris est actuellement enfoncé sur le bouton */
    protected boolean sourisEnfonce;

    /**
     * Constructeur du bouton.
     * Initialise toutes les propriétés et crée la zone de délimitation.
     * 
     * @param x Position X du bouton (coin supérieur gauche)
     * @param y Position Y du bouton (coin supérieur gauche)
     * @param largeur Largeur du bouton en pixels
     * @param hauteur Hauteur du bouton en pixels
     * @param index Index initial de l'image à afficher dans le tableau
     * @param img Tableau d'images pour les différents états du bouton (ne doit pas être null)
     */
    public Bouton(int x, int y, int largeur, int hauteur, int index, BufferedImage[] img) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.index = index;
        this.img = img;
        creationDelimitation();
    }

    /**
     * Crée ou met à jour la zone de délimitation du bouton.
     * Cette méthode est appelée lors de l'initialisation et automatiquement
     * lors des modifications de position ou de taille.
     * 
     * OPTIMISATION :
     * Méthode privée appelée uniquement lors des changements géométriques
     * pour maintenir la cohérence entre les propriétés et la zone de détection.
     */
    private void creationDelimitation() {
        delimitation = new Rectangle(x, y, largeur, hauteur);
    }

    /**
     * Réinitialise les états d'interaction de la souris.
     * Remet à false les flags de survol et d'enfoncement.
     * 
     * UTILISATION :
     * Appelée lors de la perte de focus, de la sortie de la zone du bouton,
     * ou lors d'une réinitialisation de l'état du bouton.
     */
    public void resetBooleens() {
        sourisSurvol = false;
        sourisEnfonce = false;
    }

    // ========================================
    // === GETTERS ET SETTERS ===
    // ========================================

    /**
     * Récupère la zone de délimitation du bouton.
     * 
     * @return Le Rectangle représentant la zone de détection de collision
     */
    public Rectangle getDelimitation() {
        return delimitation;
    }

    /**
     * Définit la zone de délimitation du bouton.
     * 
     * ATTENTION :
     * Cette méthode remplace complètement la délimitation. Si vous modifiez
     * x, y, largeur ou hauteur via leurs setters, la délimitation sera
     * automatiquement mise à jour. Utilisez cette méthode uniquement si
     * vous voulez définir une zone personnalisée différente des propriétés.
     * 
     * @param delimitation Le nouveau Rectangle de délimitation (ne doit pas être null)
     */
    public void setDelimitation(Rectangle delimitation) {
        this.delimitation = delimitation;
    }

    /**
     * Récupère la position Y du bouton.
     * 
     * @return La position Y (coin supérieur gauche)
     */
    public int getY() {
        return y;
    }

    /**
     * Définit la position Y du bouton et met à jour la délimitation.
     * 
     * @param y La nouvelle position Y (coin supérieur gauche)
     */
    public void setY(int y) {
        this.y = y;
        creationDelimitation();
    }

    /**
     * Récupère la position X du bouton.
     * 
     * @return La position X (coin supérieur gauche)
     */
    public int getX() {
        return x;
    }

    /**
     * Définit la position X du bouton et met à jour la délimitation.
     * 
     * @param x La nouvelle position X (coin supérieur gauche)
     */
    public void setX(int x) {
        this.x = x;
        creationDelimitation();
    }

    /**
     * Récupère la largeur du bouton.
     * 
     * @return La largeur en pixels
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Définit la largeur du bouton et met à jour la délimitation.
     * 
     * @param largeur La nouvelle largeur en pixels (doit être positive)
     */
    public void setLargeur(int largeur) {
        this.largeur = largeur;
        creationDelimitation();
    }

    /**
     * Récupère la hauteur du bouton.
     * 
     * @return La hauteur en pixels
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Définit la hauteur du bouton et met à jour la délimitation.
     * 
     * @param hauteur La nouvelle hauteur en pixels (doit être positive)
     */
    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
        creationDelimitation();
    }

    /**
     * Vérifie si un bouton de la souris est actuellement enfoncé sur ce bouton.
     * 
     * @return true si un bouton de la souris est enfoncé, false sinon
     */
    public boolean isSourisEnfonce() {
        return sourisEnfonce;
    }

    /**
     * Définit l'état d'enfoncement de la souris sur le bouton.
     * 
     * @param sourisEnfonce true si un bouton de la souris est enfoncé, false sinon
     */
    public void setSourisEnfonce(boolean sourisEnfonce) {
        this.sourisEnfonce = sourisEnfonce;
    }

    /**
     * Vérifie si la souris survole actuellement le bouton.
     * 
     * @return true si la souris survole le bouton, false sinon
     */
    public boolean isSourisSurvol() {
        return sourisSurvol;
    }

    /**
     * Définit l'état de survol de la souris sur le bouton.
     * 
     * @param sourisSurvol true si la souris survole le bouton, false sinon
     */
    public void setSourisSurvol(boolean sourisSurvol) {
        this.sourisSurvol = sourisSurvol;
    }
}
