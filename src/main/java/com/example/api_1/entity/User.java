package com.example.api_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", schema = "inf_sys_el_shop")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "inf_sys_el_shop.users_table_id_seq", allocationSize = 1)
    private Long id;

    private String password;
    private String phone;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", referencedColumnName = "id")
    private Role role;
    private String nickname;
    private String email;

    public Object getIdRole() {
        return role != null ? role.getId() : null;
    }

    public void setId_role(int idRole) {
        role.setId(idRole);
    }



}
