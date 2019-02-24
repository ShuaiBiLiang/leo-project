package com.leo.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Id;
import java.io.Serializable;


public class LeoUserVo implements Serializable {
    @Id
    private Long id;

    private String name;

    private String pwd;

    private Long endtime;

    private String token;

    private Integer useSize;

    private Long days;

    private static final long serialVersionUID = 1L;

    public LeoUserVo() {
    }

    public LeoUserVo(Long id, String name, String pwd, Long endtime) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
    }

    public LeoUserVo(Long id, String name, String pwd, Long endtime, String token, Integer useSize) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
        this.token = token;
        this.useSize = useSize;
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

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
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
