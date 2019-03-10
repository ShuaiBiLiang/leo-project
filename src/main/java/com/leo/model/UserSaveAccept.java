package com.leo.model;

import java.util.Objects;

/**
 * Created by liang on 2017/6/8.
 */
public class UserSaveAccept {

    private Long id;

    private String name;

    private String oldPwd;

    private String pwd;

    private Long endtime;

    private Integer useSize;

    private Long buyDay;

    private Long addDay;

    public Long getBuyDay() {
        return buyDay;
    }

    public void setBuyDay(Long buyDay) {
        this.buyDay = buyDay;
    }

    public Long getAddDay() {
        return addDay;
    }

    public void setAddDay(Long addDay) {
        this.addDay = addDay;
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


    public Integer getUseSize() {
        return useSize;
    }

    public void setUseSize(Integer useSize) {
        this.useSize = useSize;
    }

    public UserSaveAccept() {
    }

    public UserSaveAccept(Long id, String name, String pwd, Long endtime, Integer useSize) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.endtime = endtime;
        this.useSize = useSize;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }
}
