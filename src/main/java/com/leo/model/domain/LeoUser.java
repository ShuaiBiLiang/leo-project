package com.leo.model.domain;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import lombok.*;


public class LeoUser implements Serializable {
    private Long id;

    private String name;

    private String pwd;

    private Long endtime;

    private static final long serialVersionUID = 1L;

    public LeoUser() {
    }

    public LeoUser(Long id, String name, String pwd, Long endtime) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("pwd", pwd)
                .add("endtime", endtime)
                .toString();
    }
}
