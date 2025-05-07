package com.lb.book_scribe.util;

import com.lb.book_scribe.dto.PersonRoleInputDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PersonNameParser {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static PersonRoleInputDTO parse(String fullName, String role) {
        if (fullName == null || fullName.isBlank()) return null;

        // 1. Normalize: remove commas, normalize spaces
        String cleaned = fullName.replace(",", "").trim();
        List<String> tokens = new ArrayList<>(List.of(WHITESPACE.split(cleaned)));

        if (tokens.size() == 1) {
            return new PersonRoleInputDTO(null, null, tokens.get(0), role);
        }

        // 2. Find longest prefix starting from position 1 onward
        int prefixStart = -1;
        int prefixEnd = -1;
        for (int i = 1; i < tokens.size() - 1; i++) {
            for (int j = tokens.size() - 1; j > i; j--) {
                String candidate = String.join(" ", tokens.subList(i, j));
                if (NamePrefixDetector.isPrefix(candidate)) {
                    prefixStart = i;
                    prefixEnd = j;
                    break;
                }
            }
            if (prefixStart != -1) break;
        }

        String firstName;
        String prefix = null;
        String lastName;

        if (prefixStart != -1) {
            firstName = String.join(" ", tokens.subList(0, prefixStart));
            prefix = String.join(" ", tokens.subList(prefixStart, prefixEnd));
            lastName = String.join(" ", tokens.subList(prefixEnd, tokens.size()));
        } else {
            // No prefix found, fallback logic
            if (tokens.size() == 2) {
                firstName = tokens.get(0);
                lastName = tokens.get(1);
            } else {
                firstName = String.join(" ", tokens.subList(0, tokens.size() - 1));
                lastName = tokens.get(tokens.size() - 1);
            }
        }

        return new PersonRoleInputDTO(firstName, prefix, lastName, role);
    }

}
