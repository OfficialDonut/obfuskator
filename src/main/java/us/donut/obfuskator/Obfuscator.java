package us.donut.obfuskator;

import java.util.Arrays;
import java.util.Random;

public class Obfuscator {

    private static Random random = new Random();

    public static String obfuscate(String script) {
        StringBuilder obfuscatedScript = new StringBuilder();
        String[] lines = Arrays.stream(script.replace("\t", "    ").split("\n"))
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                .toArray(String[]::new);

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            String trimmedLine = line.trim();
            int indentation = line.indexOf(trimmedLine);
            if (trimmedLine.endsWith(":") && indentation == 0) {
                randomRepeat(3, () -> obfuscatedScript.append(SyntaxManager.getRandomTrigger()).append("\n"));
            } else if (!trimmedLine.startsWith("else") && (!trimmedLine.matches(".+:.*") || lineIndex - 1 >= 0 && !lines[lineIndex - 1].matches(".+:.*"))) {
                randomRepeat(5, () -> obfuscatedScript.append(SyntaxManager.getRandomStatement(indentation)).append("\n"));
            }
            obfuscatedScript.append(line).append("\n");
        }

        return obfuscatedScript.toString();
    }

    public static void randomRepeat(int n, Runnable action) {
        for (int i = 0; i < random.nextInt(n) + 1; i++) {
            action.run();
        }
    }
}
