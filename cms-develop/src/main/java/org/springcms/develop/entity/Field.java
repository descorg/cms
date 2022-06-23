package org.springcms.develop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@javax.persistence.Table(name = "dev_field")
public class Field {
    @Id //主键
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String type;
    private Long length;
    private Integer decimal;
    private Boolean isNull;
    private Boolean isPk;
    private String value;
    private String description;
}
