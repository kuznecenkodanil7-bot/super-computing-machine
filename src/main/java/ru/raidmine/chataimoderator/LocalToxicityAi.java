package ru.raidmine.chataimoderator;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class LocalToxicityAi {
    private static final Pattern NON_LETTER_DIGIT = Pattern.compile("[^a-zа-яё0-9]+");
    private static final Pattern REPEATED_CHARS = Pattern.compile("(.)\\1{2,}");

    private LocalToxicityAi() {
    }

    public static ToxicityResult analyze(String rawMessage, ModConfig config) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return ToxicityResult.clean(0.0);
        }

        String normalized = normalize(rawMessage);
        String compact = NON_LETTER_DIGIT.matcher(normalized).replaceAll("");
        Set<String> words = buildWordSet(config);

        for (String badWord : words) {
            String normalizedBadWord = normalize(badWord);
            String compactBadWord = NON_LETTER_DIGIT.matcher(normalizedBadWord).replaceAll("");

            if (compactBadWord.length() < 3) {
                continue;
            }

            if (containsWholeWord(normalized, normalizedBadWord)) {
                return ToxicityResult.violation(0.95, "найдено запрещенное слово", badWord);
            }

            if (config.detectObfuscatedWords && compact.contains(compactBadWord)) {
                return ToxicityResult.violation(0.85, "похоже на обход фильтра символами/пробелами", badWord);
            }
        }

        double aggressiveScore = estimateAggression(rawMessage);
        if (aggressiveScore >= config.minToxicityScore) {
            return ToxicityResult.violation(aggressiveScore, "агрессивное сообщение", "");
        }

        return ToxicityResult.clean(aggressiveScore);
    }

    private static Set<String> buildWordSet(ModConfig config) {
        Set<String> words = new HashSet<>();
        if (config.insultWords != null) {
            for (String word : config.insultWords) {
                if (word != null && !word.isBlank()) {
                    words.add(word.trim().toLowerCase(Locale.ROOT));
                }
            }
        }
        return words;
    }

    private static boolean containsWholeWord(String message, String word) {
        String pattern = "(^|[^a-zа-яё0-9])" + Pattern.quote(word) + "($|[^a-zа-яё0-9])";
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(message).find();
    }

    private static double estimateAggression(String rawMessage) {
        int exclamationCount = 0;
        int uppercaseLetters = 0;
        int letters = 0;

        for (int i = 0; i < rawMessage.length(); i++) {
            char c = rawMessage.charAt(i);
            if (c == '!') {
                exclamationCount++;
            }
            if (Character.isLetter(c)) {
                letters++;
                if (Character.isUpperCase(c)) {
                    uppercaseLetters++;
                }
            }
        }

        double score = 0.0;
        if (exclamationCount >= 4) {
            score += 0.25;
        }
        if (letters >= 8 && ((double) uppercaseLetters / letters) >= 0.75) {
            score += 0.25;
        }
        if (rawMessage.contains("???") || rawMessage.contains("!!!")) {
            score += 0.15;
        }

        return Math.min(score, 0.65);
    }

    private static String normalize(String input) {
        String text = Normalizer.normalize(input, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е')
                .replace('@', 'a')
                .replace('0', 'о')
                .replace('1', 'и')
                .replace('!', 'и')
                .replace('3', 'е')
                .replace('4', 'а')
                .replace('5', 's')
                .replace('$', 's')
                .replace('7', 'т');

        // Сжимает "дураааак" -> "дурак", чтобы ловить растягивание букв.
        return REPEATED_CHARS.matcher(text).replaceAll("$1");
    }
}
