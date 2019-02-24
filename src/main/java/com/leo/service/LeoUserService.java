package com.leo.service;

import com.leo.common.ServerResponse;
import com.leo.model.LeoUserVo;
import com.leo.model.NamePwdCookie;
import com.leo.model.UserSaveAccept;
import com.leo.model.domain.LeoUser;

public interface LeoUserService extends BaseService<LeoUser, Long> {
    ServerResponse<LeoUserVo> login(NamePwdCookie userInfo);

    LeoUser save(UserSaveAccept accept);

    ServerResponse<LeoUser> resetPwd(String name, UserSaveAccept accept);
}
