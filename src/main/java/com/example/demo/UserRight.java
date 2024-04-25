package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_rights")
public class UserRight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "delete_mark")
    private Integer deleteMark;

    @Column(name = "rights_id")
    private Long rightsId;

    @Column(name = "update_time")
    private Instant updateTime;

    @Column(name = "user_id")
    private Long userId;

}