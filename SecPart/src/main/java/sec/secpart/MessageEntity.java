package sec.secpart;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "message", schema = "easy_home_money")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = -5172861598325509711L;

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "workId")
    private String workId;

    @Size(max = 255)
    @Column(name = "messageId")
    private String messageId;

    @Size(max = 255)
    @Column(name = "tag")
    private String tag;

}