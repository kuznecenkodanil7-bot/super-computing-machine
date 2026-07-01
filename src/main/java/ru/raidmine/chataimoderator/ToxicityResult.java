package ru.raidmine.chataimoderator;

public record ToxicityResult(boolean violation, double score, String reason, String matchedWord) {
    public static ToxicityResult clean(double score) {
        return new ToxicityResult(false, score, "Нарушений не найдено", "");
    }

    public static ToxicityResult violation(double score, String reason, String matchedWord) {
        return new ToxicityResult(true, score, reason, matchedWord == null ? "" : matchedWord);
    }
}
