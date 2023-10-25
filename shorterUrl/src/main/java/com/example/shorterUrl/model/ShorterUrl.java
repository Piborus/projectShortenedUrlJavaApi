package com.example.shorterUrl.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "url")
@Entity(name = "url")
public class ShorterUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shorturl")
    @NotNull(message = "{field.notnull")
    @NotEmpty(message = "{field.notempty}")
    private String shortUrl;

    @Column(name = "longurl")
    @NotNull(message = "{field.notnull}")
    @NotEmpty(message = "{field.notempty}")
    private String longUrl;

    @Column(name = "accessnumber" )
    @NotNull
    private Long accessNumber;

}

