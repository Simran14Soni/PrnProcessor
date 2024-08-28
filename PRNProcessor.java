import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PRNProcessor {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar PRNProcessor.jar <PRN Number> <Path to JSON file>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String jsonFilePath = args[1];

        try {
            String jsonString = readFile(jsonFilePath);
            String destinationValue = findDestination(jsonString);

            if (destinationValue != null) {
                String randomString = generateRandomString(8);
                String toHash = prnNumber + destinationValue + randomString;
                String hash = generateMD5Hash(toHash);
                System.out.println(hash + ";" + randomString);
            } else {
                System.out.println("Key 'destination' not found in the JSON file.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private static String findDestination(String jsonString) {
        String key = "\"destination\"";
        int keyIndex = jsonString.indexOf(key);
        if (keyIndex != -1) {
            int start = jsonString.indexOf(":", keyIndex) + 1;
            int end = jsonString.indexOf(",", start);
            if (end == -1) {
                end = jsonString.indexOf("}", start);
            }
            String value = jsonString.substring(start, end).trim();
            return value.replaceAll("\"", ""); // Remove quotes if present
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
