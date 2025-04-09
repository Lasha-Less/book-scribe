package com.lb.book_scribe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "book")
public class SqlBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "lingo", nullable = false, length = 2)
    private String lingo;

    @Column(nullable = false, length = 50)
    private String format;

    @Column(name = "in_stock", nullable = false)
    private boolean inStock;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "historical_date")
    private Integer historicalDate;

    @Column(name = "original_language", length = 50)
    private String originalLanguage;

    @Column(name = "publisher")
    private String publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SqlPersonRole> personRoles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "book_collection",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    private Set<SqlCollection> collections = new HashSet<>();

}
