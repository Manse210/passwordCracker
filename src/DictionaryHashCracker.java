import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Stratégie de cassage par dictionnaire.
 * Parcourt un fichier texte ligne par ligne, calcule le hash MD5
 * de chaque mot et le compare au hash cible.
 */
public class DictionaryHashCracker implements HashCracker {
    private final String dictionaryPath;
    private int attempts;

    /**
     * @param dictionaryPath chemin vers le fichier dictionnaire
     */
    public DictionaryHashCracker(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
        this.attempts = 0;
    }

    @Override
    public String crack(String hash) {
        attempts = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            String word;
            while ((word = reader.readLine()) != null) {
                word = word.trim();
                if (word.isEmpty()) continue;
                attempts++;
                if (HashUtils.md5(word).equals(hash)) {
                    return word;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture dictionnaire: " + e.getMessage());
        }
        return null;
    }

    /**
     * @return le nombre de tentatives effectuées lors du dernier appel à {@link #crack(String)}
     */
    public int getAttempts() {
        return attempts;
    }
}
