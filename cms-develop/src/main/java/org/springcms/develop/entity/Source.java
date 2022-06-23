package org.springcms.develop.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@javax.persistence.Table(name = "dev_source")
public class Source {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String drive;
    private String username;
    private String password;
    private String database;
    private String url;
    private String description;
    private Date createTime;
}
