package com.leo.mapper;

import com.leo.model.domain.LeoUser;
import com.leo.util.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LeoUserMapper extends BaseMapper<LeoUser> {
}