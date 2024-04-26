package com.example.demo;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "user_right_param")
public class UserRightParam implements Serializable {
    private static final long serialVersionUID = 3590604584501194947L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_right_id", nullable = false)
    private Long userRightId;

    @Column(name = "number_value", precision = 10, scale = 2)
    private BigDecimal numberValue;

    @Lob
    @Column(name = "string_value")
    private String stringValue;

}