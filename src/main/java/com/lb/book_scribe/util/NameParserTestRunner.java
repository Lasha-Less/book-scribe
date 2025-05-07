package com.lb.book_scribe.util;

import com.lb.book_scribe.dto.PersonRoleInputDTO;
import lombok.ToString;

import java.util.List;

public class NameParserTestRunner {

    public static void main(String[] args) {
        List<String> tokens = List.of(
                "Plato",
                "Mark Twain",
                "Edwin van der Saar",
                "Jakob van Bommel",
                "Victor Hugo van der Plaats",
                "E. D. van Rijks",
                "J.D. van der Lobster",
                "Michael K., van der Pistolet",
                "Solbert, V. van der Kotelet",
                "Victor, D. Frankl",
                "Albertus M., Hibiscus",
                "Jay D. Vance",
                "P.J. Harvey",
                "L. M. Notaris");
        for (String token : tokens) {
            PersonRoleInputDTO parsed = PersonNameParser.parse(token, "Author");
            System.out.println(parsed.toString());
        }
    }

}
