package org.example.entity;

import javax.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "name_of_object")
public class Name {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
}
