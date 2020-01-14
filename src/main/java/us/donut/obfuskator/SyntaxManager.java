package us.donut.obfuskator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

public class SyntaxManager {

    private static Random random = new Random();
    private static List<Supplier<String>> triggers = new ArrayList<>();
    private static List<Supplier<String>> statements = new ArrayList<>();
    private static List<Supplier<String>> expressions = new ArrayList<>();
    private static List<String> words;

    public static String getRandomTrigger() {
        StringBuilder trigger = new StringBuilder(triggers.get(random.nextInt(triggers.size())).get());
        Obfuscator.randomRepeat(10, () -> trigger.append("\n").append(getRandomStatement(4)));
        return trigger.toString();
    }

    public static String getRandomStatement(int indentation) {
        String indent =  StringUtils.repeat(' ', indentation);
        return indent + statements.get(random.nextInt(statements.size())).get().replace("\n", "\n" + indent);
    }

    private static void newTrigger(Supplier<String> supplier) {
        triggers.add(supplier);
    }

    private static void newStatement(Supplier<String> supplier) {
        statements.add(supplier);
    }

    private static void newExpression(Supplier<String> supplier) {
        expressions.add(supplier);
    }

    private static String expression() {
        return expressions.get(random.nextInt(expressions.size())).get();
    }

    private static String variable() {
        return "{_" + word() + "}";
    }

    public static String number() {
        return String.valueOf(random.nextInt(999));
    }

    public static String word() {
        return words.get(random.nextInt(words.size()));
    }

    static {
        try (InputStream inputStream = SyntaxManager.class.getResourceAsStream("/words.txt")) {
            words = Arrays.asList(IOUtils.toString(inputStream, StandardCharsets.UTF_8).split("\n"));
        } catch (IOException e) {
            ObfuskatorApp.displayException("Failed to load words.txt", e);
        }

        newTrigger(() -> "on break:");
        newTrigger(() -> "on chat:");
        newTrigger(() -> "on click:");
        newTrigger(() -> "on damage:");
        newTrigger(() -> "on death:");
        newTrigger(() -> "on inventory click:");
        newTrigger(() -> "on join:");
        newTrigger(() -> "on quit:");

        newStatement(() -> "add " + expression() + " to " + variable());
        newStatement(() -> "increase " + variable() + " by " + expression());
        newStatement(() -> "give " + variable() + " to " + variable());
        newStatement(() -> "set " + variable() + " to " + expression());
        newStatement(() -> "remove " + expression() + " from " + variable());
        newStatement(() -> "subtract " + expression() + " from " + variable());
        newStatement(() -> "reduce " + variable() + " by " + expression());
        newStatement(() -> "delete " + variable());
        newStatement(() -> "clear " + variable());
        newStatement(() -> "reset " + variable());
        newStatement(() -> "damage " + variable() + " by " + number() + " hearts");
        newStatement(() -> "heal " + variable() + " by " + number() + " hearts");
        newStatement(() -> "repair " + variable() + " by " + number());
        newStatement(() -> "drop " + variable() + " at " + variable());
        newStatement(() -> "equip " + variable() + " with " + variable());
        newStatement(() -> "feed " + variable() + " by " + number());
        newStatement(() -> "force " + variable() + " to respawn");
        newStatement(() -> "ignite " + variable() + " for " + variable());
        newStatement(() -> "extinguish " + variable());
        newStatement(() -> "kill " + variable());
        newStatement(() -> "send " + variable() + " to " + variable());
        newStatement(() -> "teleport " + variable() + " to " + variable());

        newStatement(() -> {
            StringBuilder ifStatement = new StringBuilder("if " + variable() + " is " + expression() + ":");
            Obfuscator.randomRepeat(10, () -> ifStatement.append("\n").append(getRandomStatement(4)));
            return ifStatement.toString();
        });

        newStatement(() -> {
            StringBuilder ifStatement = new StringBuilder("if " + variable() + " is not " + expression() + ":");
            Obfuscator.randomRepeat(10, () -> ifStatement.append("\n").append(getRandomStatement(4)));
            return ifStatement.toString();
        });

        newStatement(() -> {
            StringBuilder loopStatement = new StringBuilder("loop " + variable() + " times:");
            Obfuscator.randomRepeat(10, () -> loopStatement.append("\n").append(getRandomStatement(4)));
            return loopStatement.toString();
        });

        newExpression(SyntaxManager::variable);
        newExpression(SyntaxManager::number);
        newExpression(() -> "\"" + word() + "\"");
        newExpression(() -> "true");
        newExpression(() -> "false");
        newExpression(() -> "console");
        newExpression(() -> "difference between " + variable() + " and " + variable());
        newExpression(() -> "distance between " + variable() + " and " + variable());
        newExpression(() -> "hunger of " + variable());
        newExpression(() -> "health of " + variable());
        newExpression(() -> "location of " + variable());
        newExpression(() -> "name of " + variable());
        newExpression(() -> "target of " + variable());
        newExpression(() -> "tool of " + variable());
        newExpression(() -> "uuid of " + variable());
        newExpression(() -> number() + " of " + variable());
        newExpression(() -> "yaw of " + variable());
        newExpression(() -> "pitch of " + variable());
    }
}
