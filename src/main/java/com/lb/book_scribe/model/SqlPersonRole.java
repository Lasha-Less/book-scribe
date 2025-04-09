package com.lb.book_scribe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book_people_role",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "person_id", "role"}))
public class SqlPersonRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private SqlBook book;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private SqlPerson person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public SqlPersonRole() {}

    public SqlPersonRole(SqlBook book, SqlPerson person, Role role) {
        this.book = book;
        this.person = person;
        this.role = role;
    }

}
