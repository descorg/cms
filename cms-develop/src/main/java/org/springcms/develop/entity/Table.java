package org.springcms.develop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@javax.persistence.Table(name = "dev_table")
public class Table {
    @Id //主键
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer sourceId;

    private String name;
    private String prefix;

    private String description;

    @Column(columnDefinition = "text")
    private String fields;

    @Column(columnDefinition = "text")
    private String summary;

    private Date createTime;

    @TableField(exist = false)
    private Boolean execute;
}
