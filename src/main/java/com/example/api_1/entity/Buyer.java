package com.example.api_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "buyers")
@Getter
@Setter
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buyer_seq")
    @SequenceGenerator(name = "buyer_seq", sequenceName = "inf_sys_el_shop.buyers_table_id_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String lastname;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;
}

