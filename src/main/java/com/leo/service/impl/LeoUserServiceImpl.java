package com.leo.service.impl;

import com.leo.common.ServerResponse;
import com.leo.model.LeoUserVo;
import com.leo.model.NamePwdCookie;
import com.leo.model.UserSaveAccept;
import com.leo.model.domain.LeoUser;
import com.leo.service.LeoUserService;
import com.leo.util.UserLeoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("leoUserService")
public class LeoUserServiceImpl extends BaseServiceImpl<LeoUser, Long> implements LeoUserService {

    @Override
    public ServerResponse<LeoUserVo> login(NamePwdCookie userInfo) {
        LeoUser user = new LeoUser();
        user.setName(userInfo.getName());
        user.setPwd(userInfo.getPwd());
        LeoUser selectOne = selectOne(user);
        if(selectOne!=null){
            userInfo.setCookie(UUID.randomUUID().toString().replaceAll("-",""));
            selectOne.setToken(userInfo.getCookie());
            update(selectOne);
            LeoUserVo vo = new LeoUserVo();
            BeanUtils.copyProperties(selectOne,vo);
            long time1 = System.currentTimeMillis();
            long time2 = vo.getEndtime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            long days = diff / (1000 * 60 * 60 * 24);
            vo.setDays(days);
            return ServerResponse.createBySuccess(vo);
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
            if(user.getName().equals("admin")){
                return null;
            }
            user.setPwd(accept.getPwd());
            user.setEndtime(accept.getEndtime());
            user.setUseSize(accept.getUseSize());
            update(user);

            List<NamePwdCookie> namePwdCookieList= UserLeoUtil.getInstance().get(user.getName());
            if(namePwdCookieList!=null){
                namePwdCookieList.clear();
            }
        }

        return user;
    }

    @Override
    public ServerResponse<LeoUser> resetPwd(String name, UserSaveAccept accept) {
        LeoUser user=null;
        if(accept.getId()==null){
            return null;
        }else{
            Long id = accept.getId();
            user = selectByPk(id);
            if(user ==null){
                return null;
            }
            if(!user.getName().equals(name)){
                return null;
            }
            if(!user.getPwd().equals(accept.getOldPwd())){
                return ServerResponse.createByErrorCodeMessage(77,"原密码错误！");
            }
            user.setPwd(accept.getPwd());

            update(user);

        }
        if(user==null){
            return ServerResponse.createByError();
        }
        ServerResponse<LeoUser> response = ServerResponse.createBySuccess(user);
        return response;
    }
}
