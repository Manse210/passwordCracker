public class BruteForceHashCracker implements HashCracker {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int MAX_LENGTH = 4;
    private int attempts;

    public BruteForceHashCracker() {
        this.attempts = 0;
    }

    @Override
    public String crack(String hash) {
        attempts = 0;
        for (int length = 1; length <= MAX_LENGTH; length++) {
            String result = tryLength(hash, "", length);
            if (result != null) return result;
        }
        return null;
    }

    private String tryLength(String hash, String prefix, int length) {
        if (prefix.length() == length) {
            attempts++;
            return HashUtils.md5(prefix).equals(hash) ? prefix : null;
        }
        for (char c : ALPHABET.toCharArray()) {
            String result = tryLength(hash, prefix + c, length);
            if (result != null) return result;
        }
        return null;
    }

    public int getAttempts() {
        return attempts;
    }
}
