package cn.wizzer.app.sys.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_api;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.service.BaseService;

import java.io.IOException;
import java.util.Date;

/**
 * Created by wizzer on 2016/12/23.
 */
public interface SysApiService extends BaseService<Sys_api> {
    String generateToken(Date date, String appId)throws IOException, ClassNotFoundException;
    boolean verifyToken(String appId, String token);
    String createJWT(long ttlMillis, Sys_user user);
    Boolean isVerify(String token, Sys_user user);
}
