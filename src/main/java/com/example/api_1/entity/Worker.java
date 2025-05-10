package com.example.api_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "workers")
@Getter
@Setter
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worker_seq")
    @SequenceGenerator(name = "worker_seq", sequenceName = "inf_sys_el_shop.workers_table_id_seq", allocationSize = 1)
    private Long id;

    private Long id_position;
    private String name;
    private String lastname;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;
}
