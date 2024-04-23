package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "home", schema = "easy_home_money")
public class HomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "delete_flag")
    private Byte deleteFlag;

    @Column(name = "home_name")
    private String homeName;

    @Column(name = "modify_time")
    private Instant modifyTime;

}