package com.leo.model.domain;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class LeoUser implements Serializable {
    private Long id;

    private String name;

    private String pwd;

    private Long endtime;

    private static final long serialVersionUID = 1L;

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
