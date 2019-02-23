package com.leo.service.impl;

import com.leo.common.ServerResponse;
import com.leo.model.NamePwdCookie;
import com.leo.model.UserSaveAccept;
import com.leo.model.domain.LeoUser;
import com.leo.service.LeoUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("leoUserService")
public class LeoUserServiceImpl extends BaseServiceImpl<LeoUser, Long> implements LeoUserService {

    @Override
    public ServerResponse<NamePwdCookie> login(NamePwdCookie userInfo) {
        LeoUser user = new LeoUser();
        user.setName(userInfo.getName());
        user.setPwd(userInfo.getPwd());
        LeoUser selectOne = selectOne(user);
        if(selectOne!=null){
            userInfo.setCookie(UUID.randomUUID().toString().replaceAll("-",""));
            selectOne.setToken(userInfo.getCookie());
            update(selectOne);
            return ServerResponse.createBySuccess(userInfo);
        }
        return ServerResponse.createByErrorMessage("用户名不存在或密码错误");
    }

    @Override
    public LeoUser save(UserSaveAccept accept) {
        LeoUser user=null;
        if(accept.getId()==null){
            user = new LeoUser();
            user.setName(accept.getName());
            user.setPwd(accept.getPwd());
            user.setEndtime(accept.getEndtime());
            user.setUseSize(accept.getUseSize());
            insert(user);
        }else{
            Long id = accept.getId();
            user = selectByPk(id);
            if(user ==null){
                return null;
            }
            if(user.equals("admin")){
                return null;
            }
            user.setPwd(accept.getPwd());
            user.setEndtime(accept.getEndtime());
            user.setUseSize(accept.getUseSize());
            update(user);
        }

        return user;
    }
}
