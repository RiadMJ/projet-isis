package PasswordManager;

import org.mindrot.jbcrypt.BCrypt;
import java.io.*;

public class PasswordManager {
    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_HASH = hashPassword("admin123"); // Hash par défaut

    public static boolean verifyPassword(String input) {
        String storedHash = readHashFromFile();
        return BCrypt.checkpw(input, storedHash);
    }

    public static void changePassword(String newPassword) {
        writeHashToFile(hashPassword(newPassword));
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static String readHashFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            return DEFAULT_HASH; // Retourne le hash par défaut si fichier inexistant
        }
    }

    private static void writeHashToFile(String hash) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(hash);
        } catch (IOException e) {
            throw new RuntimeException("Erreur d'écriture du fichier de configuration", e);
        }
    }
}
