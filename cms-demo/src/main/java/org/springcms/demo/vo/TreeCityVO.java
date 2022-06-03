package org.springcms.demo.vo;

import lombok.Data;
import org.springcms.demo.entity.City;

import java.util.List;

@Data
public class TreeCityVO extends City {
    private Integer childrens;

    List<TreeCityVO> children;
}
