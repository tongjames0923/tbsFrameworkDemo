package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tbs.framework.sql.annotations.AutoSetDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "login_info")
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_flag", nullable = false)
    private Byte loginFlag;

    @Column(name = "login_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @AutoSetDate
    private Date loginTime;

    @Column(name = "delete_mark", nullable = false)
    private Byte deleteMark;

    @Column(name = "home_id")
    private Long homeId;

    @Size(max = 255)
    @Column(name = "note")
    private String note;

}