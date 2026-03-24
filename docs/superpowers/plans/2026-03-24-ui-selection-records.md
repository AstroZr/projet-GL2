# UI Selection 2-colonnes + Records Scroll Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Afficher les niveaux en 2 colonnes zigzag dans l'écran de sélection, et rendre les panneaux de l'écran Records scrollables indépendamment à la molette.

**Architecture:** Trois modifications indépendantes — (1) infrastructure molette souris (MethodesEtats + MouseInputs + GamePanel), (2) layout 2-colonnes dans Selection, (3) scroll par clipping + translate dans Records.

**Tech Stack:** Java 17, Swing/Java2D, JUnit 4

---

## Fichiers modifiés

| Fichier | Rôle |
|---|---|
| `src/main/java/Groupe6/etats/MethodesEtats.java` | Ajout méthode default `mouseWheelMoved` |
| `src/main/java/Groupe6/inputs/MouseInputs.java` | Implémente `MouseWheelListener`, dispatche l'événement |
| `src/main/java/Groupe6/game/GamePanel.java` | Enregistre `mouseInputs` comme `MouseWheelListener` |
| `src/main/java/Groupe6/etats/Selection.java` | Layout 2-colonnes zigzag |
| `src/main/java/Groupe6/etats/Records.java` | Scroll indépendant par panneau |
| `src/test/java/Groupe6/etats/SelectionLayoutTest.java` | Tests calcul colonne/Y |
| `src/test/java/Groupe6/etats/RecordsScrollTest.java` | Tests clamping scroll |

---

## Task 1 : Infrastructure molette souris

**Files:**
- Modify: `src/main/java/Groupe6/etats/MethodesEtats.java`
- Modify: `src/main/java/Groupe6/inputs/MouseInputs.java`
- Modify: `src/main/java/Groupe6/game/GamePanel.java`

- [ ] **Step 1 : Ajouter `mouseWheelMoved` comme méthode default dans `MethodesEtats`**

Dans `MethodesEtats.java`, après la dernière méthode (`updateTexts`), ajouter l'import `java.awt.event.MouseWheelEvent` et la méthode :

```java
import java.awt.event.MouseWheelEvent;

// dans l'interface :
default void mouseWheelMoved(MouseWheelEvent e) {}
```

- [ ] **Step 2 : Implémenter `MouseWheelListener` dans `MouseInputs`**

```java
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseInputs implements MouseListener, MouseMotionListener, MouseWheelListener {
    // ... code existant ...

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseWheelMoved((MouseWheelEvent) event));
    }
}
```

Note : `handleMouseEvent` prend un `MouseEvent`, `MouseWheelEvent` en est une sous-classe donc ça fonctionne. Caster en `MouseWheelEvent` dans le lambda.

- [ ] **Step 3 : Enregistrer le listener dans `GamePanel`**

Dans `GamePanel`, après `addMouseMotionListener(mouseInputs)`, ajouter :

```java
addMouseWheelListener(mouseInputs);
```

- [ ] **Step 4 : Compiler pour vérifier**

```bash
mvn clean compile
```

Expected : BUILD SUCCESS, 0 erreurs.

- [ ] **Step 5 : Commit**

```bash
git add src/main/java/Groupe6/etats/MethodesEtats.java \
        src/main/java/Groupe6/inputs/MouseInputs.java \
        src/main/java/Groupe6/game/GamePanel.java
git commit -m "feat: add mouse wheel event dispatch infrastructure"
```

---

## Task 2 : Selection — layout 2 colonnes zigzag

**Files:**
- Modify: `src/main/java/Groupe6/etats/Selection.java`
- Create: `src/test/java/Groupe6/etats/SelectionLayoutTest.java`

### Logique du layout 2 colonnes

Pour `n` niveaux, avec l'index `i` (0-based) :
- Colonne : `i % 2 == 0` → gauche, sinon droite
- Rang dans la colonne : `i / 2`
- `bw` = largeur d'un bouton (ref 280, scalé)
- `colGap` = gap entre colonnes (ref 60, scalé)
- `leftX` = `cx - colGap/2 - bw`
- `rightX` = `cx + colGap/2`
- `Y` = `startY + (i / 2) * gap`

- [ ] **Step 1 : Écrire le test de calcul de position**

Créer `src/test/java/Groupe6/etats/SelectionLayoutTest.java` :

```java
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
}
```

- [ ] **Step 2 : Vérifier que le test compile et passe (logique pure, pas de Swing)**

```bash
mvn test -Dtest=SelectionLayoutTest
```

Expected : PASS (la logique est correcte par construction).

- [ ] **Step 3 : Modifier les constantes dans `Selection.java`**

Remplacer :
```java
private static final int LARGEUR_BOUTON = 400;
```
Par :
```java
private static final int LARGEUR_BOUTON = 280;
private static final int GAP_COLONNES_REF = 60;
```

Note : le bouton "Retour" utilise `cx - bw / 2` pour son X (ligne 73 dans `initClasses()`). Comme `bw` est re-dérivé depuis `LARGEUR_BOUTON` au début de la méthode, il s'adapte automatiquement à la nouvelle largeur — aucune modification supplémentaire nécessaire pour ce bouton.

- [ ] **Step 4 : Modifier `initClasses()` dans `Selection.java`**

Remplacer la boucle de création des boutons :

```java
int colGap = layoutScale.scaleUniform(GAP_COLONNES_REF);
int leftX  = cx - colGap / 2 - bw;
int rightX = cx + colGap / 2;

for (int i = 0; i < ids.size(); i++) {
    String id = ids.get(i);
    int bx = (i % 2 == 0) ? leftX : rightX;
    int by = startY + (i / 2) * gap;
    boutons.add(new BoutonNiveau(bx, by, bw, bh, id, jeu));
}
```

- [ ] **Step 5 : Modifier `applyLayout()` dans `Selection.java`**

Remplacer la boucle de repositionnement :

```java
int colGap = layoutScale.scaleUniform(GAP_COLONNES_REF);
int leftX  = cx - colGap / 2 - bw;
int rightX = cx + colGap / 2;

for (int i = 0; i < nombreNiveaux; i++) {
    Bouton b = boutons.get(i);
    b.setX((i % 2 == 0) ? leftX : rightX);
    b.setY(startY + (i / 2) * gap);
    b.setLargeur(bw);
    b.setHauteur(bh);
}
```

- [ ] **Step 6 : Lancer l'application et vérifier visuellement**

```bash
mvn exec:java
```

Naviguer vers l'écran de sélection. Vérifier :
- Niveaux en 2 colonnes symétriques
- Zigzag : niveau 1 gauche, 2 droite, 3 gauche...
- Bouton Retour centré en bas

- [ ] **Step 7 : Commit**

```bash
git add src/main/java/Groupe6/etats/Selection.java \
        src/test/java/Groupe6/etats/SelectionLayoutTest.java
git commit -m "feat: selection screen 2-column zigzag layout"
```

---

## Task 3 : Records — scroll indépendant par panneau

**Files:**
- Modify: `src/main/java/Groupe6/etats/Records.java`
- Create: `src/test/java/Groupe6/etats/RecordsScrollTest.java`

### Logique du scroll

- `scrollLeft`, `scrollRight` : offset en pixels (0 = en haut)
- `contentH` : hauteur totale du contenu (calculée lors du draw)
- `visibleH` = `panelH - 60` (60px pour le header)
- Clamp : `scroll = Math.max(0, Math.min(scroll, Math.max(0, contentH - visibleH)))`
- Molette : `scrollAmount = e.getUnitsToScroll() * SCROLL_SPEED` (SCROLL_SPEED = 20px ref)
- Détection panneau actif : si `mouseX < cx` → gauche, sinon → droite

- [ ] **Step 1 : Écrire le test de clamping**

Créer `src/test/java/Groupe6/etats/RecordsScrollTest.java` :

```java
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
```

- [ ] **Step 2 : Vérifier que les tests passent**

```bash
mvn test -Dtest=RecordsScrollTest
```

Expected : PASS.

- [ ] **Step 3 : Ajouter les champs scroll dans `Records.java`**

Après les champs existants (`layoutScale`, labels...) :

```java
private static final int SCROLL_SPEED = 20;
private int scrollLeft  = 0;
private int scrollRight = 0;
private int maxScrollLeft  = 0;
private int maxScrollRight = 0;
```

- [ ] **Step 4 : Extraire le calcul de hauteur de contenu gauche**

Dans la méthode `draw()`, avant de dessiner le contenu du panneau gauche, calculer la hauteur totale :

```java
int visibleH = panelH - 60;

// Hauteur totale contenu gauche
int cLeftH = niveaux.size() * (rowH + 6);
maxScrollLeft = Math.max(0, cLeftH - visibleH);
scrollLeft    = Math.max(0, Math.min(scrollLeft, maxScrollLeft));
```

- [ ] **Step 5 : Clipper et décaler le rendu du panneau gauche**

**Important :** `dessinerPanel(g2d, leftX, panelY, panelW, panelH)` (fond + bordure du rectangle) doit rester appelé **avant** le `setClip`. Seul le contenu (les lignes) est clippé, pas le fond du panneau.

Remplacer le rendu du contenu gauche (la boucle `for (String niv : niveaux)` du panneau gauche) :

```java
// Sauvegarder le clip original
java.awt.Shape clipOriginal = g2d.getClip();

// Appliquer le clip au contenu du panneau gauche
g2d.setClip(leftX, panelY + 50, panelW, visibleH);
g2d.translate(0, -scrollLeft);

int rowY = panelY + 60;
for (String niv : niveaux) {
    String temps = mesTemps.containsKey(niv) ? formaterTemps(mesTemps.get(niv)) : "\u2014";
    dessinerLigne(g2d, leftX + 12, rowY, panelW - 24, rowH, niv, temps, false);
    rowY += rowH + 6;
}
if (niveaux.isEmpty()) {
    g2d.setFont(FONT_TEXTE);
    g2d.setColor(getFond().getCouleurTexte());
    g2d.drawString(labelAucun, leftX + 16, panelY + 70);
}

// Restaurer la translation et le clip
g2d.translate(0, scrollLeft);
g2d.setClip(clipOriginal);

// Indicateur scroll bas (gradient) si contenu tronqué
if (scrollLeft < maxScrollLeft) {
    dessinerIndicateurScroll(g2d, leftX, panelY + 50 + visibleH - 24, panelW, 24, false);
}
// Indicateur scroll haut
if (scrollLeft > 0) {
    dessinerIndicateurScroll(g2d, leftX, panelY + 50, panelW, 24, true);
}
```

- [ ] **Step 6 : Calculer la hauteur de contenu du panneau droit et clipper**

Le contenu droit est plus complexe (une ligne de titre + entrées par niveau). Pour éviter d'appeler `chargerClassementGlobal` deux fois par frame (une fois pour le calcul, une fois pour le rendu), charger les classements une seule fois et les mettre en cache dans une `Map` :

```java
// Charger tous les classements une seule fois
Map<String, List<Map.Entry<String, Long>>> classements = new HashMap<>();
for (String niv : niveaux) {
    classements.put(niv, SaveManager.chargerClassementGlobal(niv));
}

int cRightH = 0;
for (String niv : niveaux) {
    cRightH += 28; // titre du niveau
    List<Map.Entry<String, Long>> cl = classements.get(niv);
    int entries = cl.isEmpty() ? 1 : Math.min(cl.size(), 5);
    cRightH += entries * (rowH + 4) + 10;
}
maxScrollRight = Math.max(0, cRightH - visibleH);
scrollRight    = Math.max(0, Math.min(scrollRight, maxScrollRight));
```

**Important :** `dessinerPanel(g2d, rightX, panelY, panelW, panelH)` (fond + bordure) doit rester **avant** le `setClip`.

Puis clipper et décaler (remplacer la boucle droite existante, en utilisant `classements.get(niv)` au lieu de `SaveManager.chargerClassementGlobal(niv)`) :

```java
g2d.setClip(rightX, panelY + 50, panelW, visibleH);
g2d.translate(0, -scrollRight);

int rRowY = panelY + 60;
for (String niv : niveaux) {
    g2d.setFont(FONT_SOUS);
    g2d.setColor(getFond().getCouleurTexte());
    g2d.drawString(labelNiveau + " : " + niv, rightX + 12, rRowY + 16);
    rRowY += 28;

    List<Map.Entry<String, Long>> classement = classements.get(niv);
    g2d.setFont(FONT_TEXTE);
    if (classement.isEmpty()) {
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(labelAucun, rightX + 16, rRowY + 16);
        rRowY += rowH;
    } else {
        int rang = 1;
        for (Map.Entry<String, Long> entry : classement) {
            boolean moi = entry.getKey().equals(joueur);
            String rangStr = "#" + rang + "  " + entry.getKey();
            String tempsStr = formaterTemps(entry.getValue());
            dessinerLigne(g2d, rightX + 12, rRowY, panelW - 24, rowH, rangStr, tempsStr, moi);
            rRowY += rowH + 4;
            rang++;
            if (rang > 5) break;
        }
    }
    rRowY += 10;
}

g2d.translate(0, scrollRight);
g2d.setClip(clipOriginal);

if (scrollRight < maxScrollRight) {
    dessinerIndicateurScroll(g2d, rightX, panelY + 50 + visibleH - 24, panelW, 24, false);
}
if (scrollRight > 0) {
    dessinerIndicateurScroll(g2d, rightX, panelY + 50, panelW, 24, true);
}
```

- [ ] **Step 7 : Ajouter `dessinerIndicateurScroll()`**

```java
private void dessinerIndicateurScroll(Graphics2D g2d, int x, int y, int w, int h, boolean versHaut) {
    java.awt.GradientPaint gp;
    Color transparent = new Color(0, 0, 0, 0);
    Color opaque      = new Color(0, 0, 0, 90);
    if (versHaut) {
        gp = new java.awt.GradientPaint(x, y, opaque, x, y + h, transparent);
    } else {
        gp = new java.awt.GradientPaint(x, y, transparent, x, y + h, opaque);
    }
    g2d.setPaint(gp);
    g2d.fillRect(x, y, w, h);
    g2d.setPaint(null);
}
```

- [ ] **Step 8 : Implémenter `mouseWheelMoved` dans `Records`**

Ajouter l'import `java.awt.event.MouseWheelEvent` et la méthode :

```java
@Override
public void mouseWheelMoved(MouseWheelEvent e) {
    int delta = e.getUnitsToScroll() * layoutScale.scaleY(SCROLL_SPEED);
    int cx = layoutScale.centerX();
    if (e.getX() < cx) {
        scrollLeft  = Math.max(0, Math.min(scrollLeft  + delta, maxScrollLeft));
    } else {
        scrollRight = Math.max(0, Math.min(scrollRight + delta, maxScrollRight));
    }
}
```

- [ ] **Step 9 : Réinitialiser le scroll à l'entrée dans l'état**

L'objet `Records` est construit une seule fois et réutilisé. Il faut un hook d'entrée.

**a)** Ajouter `default void onEnter() {}` dans `MethodesEtats.java` :

```java
/** Appelé une fois à chaque activation de cet état. */
default void onEnter() {}
```

**b)** Dans `Game.java`, dans la méthode `update()`, appeler `onEnter()` sur le nouvel état lors d'un changement (ligne ~150) :

```java
if (dernierEtat != null && etatCourant != dernierEtat) {
    transitionManager.notifierChangementEtat(frameBuffer);
    SoundManager.getInstance().playTransition();
    getCurrentState().onEnter();   // ← ajouter cette ligne
}
```

**c)** Overrider `onEnter()` dans `Records.java` :

```java
@Override
public void onEnter() {
    scrollLeft  = 0;
    scrollRight = 0;
}
```

- [ ] **Step 10 : Lancer l'application et vérifier visuellement**

```bash
mvn exec:java
```

Aller dans l'écran Records. Vérifier :
- Molette sur panneau gauche → scrolle uniquement le contenu gauche
- Molette sur panneau droit → scrolle uniquement le contenu droit
- Le titre de chaque panneau ("Mes records" / "Classement") reste visible (hors clip)
- Les indicateurs de gradient apparaissent si contenu tronqué
- Pas de débordement hors du panneau

- [ ] **Step 11 : Lancer tous les tests**

```bash
mvn test
```

Expected : tous les tests passent.

- [ ] **Step 12 : Commit**

```bash
git add src/main/java/Groupe6/etats/Records.java \
        src/main/java/Groupe6/etats/MethodesEtats.java \
        src/main/java/Groupe6/game/Game.java \
        src/test/java/Groupe6/etats/RecordsScrollTest.java
git commit -m "feat: records panels independently scrollable with mouse wheel"
```
