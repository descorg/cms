package org.springcms.core.jwt.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CmsUser implements Serializable {

    private Integer id;

    private String name;
}
