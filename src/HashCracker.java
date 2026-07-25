/**
 * Interface définissant le contrat commun à toutes les stratégies
 * de cassage de hash. Chaque stratégie concrète doit implémenter
 * la méthode {@link #crack(String)}.
 */
public interface HashCracker {

    /**
     * Tente de retrouver le mot de passe en clair à partir d'un hash donné.
     *
     * @param hash le hash MD5 à inverser (sous forme hexadécimale)
     * @return le mot de passe trouvé, ou {@code null} si aucun résultat
     */
    String crack(String hash);
}
