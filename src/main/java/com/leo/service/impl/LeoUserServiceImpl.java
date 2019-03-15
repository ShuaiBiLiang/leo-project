package com.leo.service.impl;

import com.leo.common.ServerResponse;
import com.leo.model.LeoUserVo;
import com.leo.model.NamePwdCookie;
import com.leo.model.UserSaveAccept;
import com.leo.model.domain.LeoUser;
import com.leo.service.LeoUserService;
import com.leo.util.DateUtil;
import com.leo.util.UserLeoUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service("leoUserService")
public class LeoUserServiceImpl extends BaseServiceImpl<LeoUser, Long> implements LeoUserService {
    private static Log logger = LogFactory.getLog(LeoServiceImpl.class);

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
            vo.setName(selectOne.getName());
            vo.setUseSize(selectOne.getUseSize());
            vo.setToken(selectOne.getToken());
            vo.setId(selectOne.getId());
            vo.setEndtime(selectOne.getEndtime());

            try {
                MyWebSocket.closeWebsocket(selectOne.getName());
            } catch (Exception e) {
                logger.error("登录时关闭webSocket错误",e);
            }
            return ServerResponse.createBySuccess(vo);
        }
        return ServerResponse.createByErrorMessage("用户名不存在或密码错误");
    }

    @Override
    public LeoUser save(UserSaveAccept accept) {
        LeoUser user=null;
        Long endtime = 0L;
        if(accept.getId()==null){
            user = new LeoUser();
            user.setName(accept.getName());
            user.setPwd(accept.getPwd());

            if(accept.getEndtime()!=null && accept.getEndtime()>0){
                endtime = accept.getEndtime();
            }else if(accept.getBuyDay()!=null && accept.getBuyDay()>0){
                endtime = DateUtil.getTimeByDay(accept.getBuyDay().intValue());
            }else {
                return null;
            }

            user.setEndtime(endtime);
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


            if(accept.getEndtime()!=null && accept.getEndtime()>0){
                endtime = accept.getEndtime();
            }else if(accept.getAddDay()!=null && accept.getAddDay()>0){
                endtime = user.getEndtime()+(24*60*60*1000)*accept.getAddDay();
            }else {
                return null;
            }

            user.setEndtime(endtime);
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
