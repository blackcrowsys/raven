package com.blackcrowsys.raven.model;

import com.blackcrowsys.raven.annotation.MapTo;
import com.blackcrowsys.raven.annotation.Mapping;

@Mapping(value = Target.class)
public class Schema {

    @MapTo(fieldName = "value")
    private Integer v1;

    @MapTo(fieldName = "name")
    private String v2;

    public Integer getV1() {
        return v1;
    }

    public void setV1(Integer v1) {
        this.v1 = v1;
    }

    public String getV2() {
        return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }
}
