package com.leo.service;

import com.leo.common.ServerResponse;
import com.leo.model.NamePwdCookie;
import com.leo.model.domain.LeoUser;

public interface LeoUserService extends BaseService<LeoUser, Long> {
    ServerResponse<NamePwdCookie> login(NamePwdCookie userInfo);
}
