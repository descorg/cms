package org.springcms.develop.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@javax.persistence.Table(name = "dev_code")
public class Code {

    @Id
    private Integer id;

    private String module;
    private String entity;
    private String wrap;
    private String back;
    private String front;

    private Date createTime;
}
