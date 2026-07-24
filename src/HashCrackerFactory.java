public class HashCrackerFactory {
    public static HashCracker create(String method) {
        switch (method.toUpperCase()) {
            case "DICO":
                return new DictionaryHashCracker("dictionary.txt");
            case "BRUTE":
                return new BruteForceHashCracker();
            default:
                throw new IllegalArgumentException("Méthode inconnue: " + method);
        }
    }
}
