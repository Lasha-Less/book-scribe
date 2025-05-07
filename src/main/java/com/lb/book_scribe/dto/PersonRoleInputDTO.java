package com.lb.book_scribe.dto;

import lombok.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonRoleInputDTO {

    private String firstName;
    private String prefix;
    private String lastName;
    private String role;

    public String getFullName() {
        return Stream.of(firstName, prefix, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

}
