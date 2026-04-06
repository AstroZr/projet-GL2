package Groupe6.aide;

/**
 * Représente les différents types d'effets visuels pouvant être appliqués.
 * <p>
 * Chaque type est associé à une interprétation :
 * <ul>
 *     <li><b>Négatif</b> : indique une erreur ou un élément invalide
 *     (ex : une case incorrecte, un numéro ne respectant pas les règles,
 *     un candidat impossible).</li>
 *     <li><b>Positif</b> : indique un élément correct ou valide
 *     (ex : une case correcte, un numéro conforme aux règles,
 *     un candidat plausible).</li>
 * </ul>
 * </p>
 */
public enum TypeEffect {

    /** Indique qu'une case est incorrecte ou invalide */
    CASE_NEGATIVE,

    /** Indique qu'une case est correcte */
    CASE_POSITIVE,

    /** Indique qu'un numéro ne respecte pas les règles */
    NUMERO_NEGATIF,

    /** Indique qu'un numéro respecte les règles */
    NUMERO_POSITIF,

    /** Indique qu'un candidat est impossible ou invalide */
    CANDIDAT_NEGATIF,

    /** Indique qu'un candidat est valide ou plausible */
    CANDIDAT_POSITIF
}