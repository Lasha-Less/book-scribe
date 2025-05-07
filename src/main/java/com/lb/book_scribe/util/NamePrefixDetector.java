package com.lb.book_scribe.util;

import java.util.Set;

public class NamePrefixDetector {

    // Expanded list of known surname prefixes
    private static final Set<String> PREFIXES = Set.of(
            "'s",
            "'t",
            "a",
            "ab",
            "abu",
            "af",
            "ait",
            "al",
            "alam",
            "ap",
            "at",
            "ath",
            "bar",
            "bat",
            "bath",
            "ben",
            "bet",
            "bin",
            "bint",
            "chaudhary",
            "da",
            "das",
            "de",
            "de la",
            "degli",
            "del",
            "dele",
            "della",
            "der",
            "des",
            "di",
            "dos",
            "du",
            "d’",
            "el",
            "erch",
            "ferch",
            "fitz",
            "gil",
            "i",
            "ibn",
            "ka",
            "kil",
            "la",
            "le",
            "mac",
            "mal",
            "mala",
            "mc",
            "mck",
            "mhic",
            "mic",
            "mul",
            "m’",
            "na",
            "nga",
            "ni",
            "nic",
            "nin",
            "o",
            "olam",
            "oz",
            "pour",
            "te",
            "ter",
            "tre",
            "ua",
            "ui",
            "van",
            "van de",
            "van den",
            "van der",
            "van ‘t",
            "verch",
            "von",
            "war",
            "zu"
    );

    /**
     * Checks if the given sequence of name parts matches a known prefix.
     *
     * @param parts Array of name parts to check.
     * @return True if the sequence matches a known prefix; false otherwise.
     */
    public static boolean isPrefix(String... parts) {
        if (parts == null || parts.length == 0) return false;

        String joined = String.join(" ", parts).toLowerCase().trim();
        return PREFIXES.contains(joined);
    }

}
