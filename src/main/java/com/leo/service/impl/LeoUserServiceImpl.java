package com.leo.service.impl;

import com.leo.model.domain.LeoUser;
import com.leo.service.LeoUserService;
import org.springframework.stereotype.Service;

@Service("leoUserService")
public class LeoUserServiceImpl extends BaseServiceImpl<LeoUser, Long> implements LeoUserService {

}
