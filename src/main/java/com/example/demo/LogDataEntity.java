package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Abstergo
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log_data", schema = "easy_home_money")
public class LogDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "log_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date logDate;
    
    @Column(name = "log_content")
    private String logContent;

    @Size(max = 255)
    @Column(name = "applicationName")
    private String applicationName;
    @Size(max = 32)
    @Column(name = "level")
    private String level;
}