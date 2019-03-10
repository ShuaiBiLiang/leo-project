package com.leo.model.domain;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import lombok.*;

import javax.persistence.Id;


public class LeoUser implements Serializable {
    @Id
    private Long id;

    private String name;

    private String pwd;

    private Long endtime;

    private String token;

    private Integer useSize;

    private Integer state; // 0正常  1停用 2删除

    private static final long serialVersionUID = 1L;

    public LeoUser() {
    }

    public LeoUser(Long id, String name, String pwd, Long endtime) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
    }

    public LeoUser(Long id, String name, String pwd, Long endtime, String token, Integer useSize, Integer state) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
        this.token = token;
        this.useSize = useSize;
        this.state = state;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUseSize() {
        return useSize;
    }

    public void setUseSize(Integer useSize) {
        this.useSize = useSize;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
