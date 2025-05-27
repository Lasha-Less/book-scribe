package com.lb.book_scribe.util;

import com.lb.book_scribe.dto.PersonRoleInputDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonNameParserTest {

    @Test
    void testParse_singleName_returnsAsLastNameOnly() {
        PersonRoleInputDTO result = PersonNameParser.parse("Plato", "author");

        assertNotNull(result);
        assertNull(result.getFirstName());
        assertNull(result.getPrefix());
        assertEquals("Plato", result.getLastName());
        assertEquals("author", result.getRole());
    }

    @Test
    void testParse_twoPartName() {
        PersonRoleInputDTO result = PersonNameParser.parse("John Smith", "editor");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertNull(result.getPrefix());
        assertEquals("Smith", result.getLastName());
        assertEquals("editor", result.getRole());
    }

    @Test
    void testParse_withPrefix() {
        PersonRoleInputDTO result = PersonNameParser.parse("Maria van der Zee", "translator");

        assertNotNull(result);
        assertEquals("Maria", result.getFirstName());
        assertEquals("van der", result.getPrefix());
        assertEquals("Zee", result.getLastName());
        assertEquals("translator", result.getRole());
    }

    @Test
    void testParse_complexPrefix() {
        PersonRoleInputDTO result = PersonNameParser.parse("Hans de la Fontaine", "author");

        assertNotNull(result);
        assertEquals("Hans", result.getFirstName());
        assertEquals("de la", result.getPrefix());
        assertEquals("Fontaine", result.getLastName());
    }

    @Test
    void testParse_nullOrBlank_returnsNull() {
        assertNull(PersonNameParser.parse(null, "author"));
        assertNull(PersonNameParser.parse("   ", "editor"));
    }

    @Test
    void testParse_stripsCommasAndExtraSpaces() {
        PersonRoleInputDTO result = PersonNameParser.parse("  Jean,   Dupont  ", "author");

        assertNotNull(result);
        assertEquals("Jean", result.getFirstName());
        assertEquals("Dupont", result.getLastName());
    }

    @Test
    void testParse_twoPartName_CharlesDarwin() {
        PersonRoleInputDTO result = PersonNameParser.parse("Charles Darwin", "author");

        assertNotNull(result);
        assertEquals("Charles", result.getFirstName());
        assertEquals("Darwin", result.getLastName());
        assertEquals("author", result.getRole());
    }

}