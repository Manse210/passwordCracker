public class Main {
    public static void main(String[] args) {
        String method = null;
        String hash = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m") && i + 1 < args.length) {
                method = args[++i];
            } else if (args[i].equals("-h") && i + 1 < args.length) {
                hash = args[++i];
            }
        }

        if (method == null || hash == null) {
            System.out.println("Usage: passwordCracker -m [BRUTE|DICO] -h <hash>");
            return;
        }

        try {
            HashCracker cracker = HashCrackerFactory.create(method);

            long start = System.currentTimeMillis();
            String result = cracker.crack(hash);
            long end = System.currentTimeMillis();

            if (result != null) {
                System.out.println("Password found: " + result);
            } else {
                System.out.println("Password not found");
            }
            System.out.println("Temps d'exécution: " + (end - start) + " ms");

            // Affiche le nombre de tentatives si disponible
            if (cracker instanceof DictionaryHashCracker) {
                System.out.println("Tentatives: " + ((DictionaryHashCracker) cracker).getAttempts());
            } else if (cracker instanceof BruteForceHashCracker) {
                System.out.println("Tentatives: " + ((BruteForceHashCracker) cracker).getAttempts());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
