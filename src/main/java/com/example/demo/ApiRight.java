package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "api_rights")
public class ApiRight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "auth_type", length = 50)
    private String authType;

    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

    @Column(name = "enable")
    private Integer enable;

    @Column(name = "force_need_flag")
    private Integer forceNeedFlag;

    @Size(max = 500)
    @Column(name = "param", length = 500)
    private String param;

    @Size(max = 255)
    @Column(name = "role")
    private String role;

    @Column(name = "role_type")
    private Integer roleType;

    @Size(max = 255)
    @Column(name = "url")
    private String url;

}