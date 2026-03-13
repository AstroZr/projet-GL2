package Groupe6.aide;

/* TypeEffect représente le type d'un EffetVisuel.
   Le negatif correspond àu fait qu'un numero ne respecte pas une des regles du jeu, qu'une case ne correspond pas à la solution, qu'un candidat n'est pas plausible etc.
   Le positif correspond au fait qu'un numero correspond à une aide propose, qu'une case correspond à la solution, qu'un cadidat est plausible etc.
*/

public enum TypeEffect{
    CASE_NEGATIVE, CASE_POSITIVE,
    NUMERO_NEGATIF, NUMERO_POSITIF,
    CANDIDAT_NEGATIF, CANDIDAT_POSITIF
}
