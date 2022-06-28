package org.springcms.develop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@javax.persistence.Table(name = "dev_summary")
public class Summary {
    @Id //主键
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer sourceId;
    private String table;
    private String field;

    private String joinTable;
    private String joinField;
    private String nameField;
}
