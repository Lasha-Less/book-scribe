package com.lb.book_scribe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "person")
public class SqlPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "prefix", length = 10)
    private String prefix;

    @Column(name = "last_name", length = 100)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SqlPersonRole> personRoles = new HashSet<>();

}
