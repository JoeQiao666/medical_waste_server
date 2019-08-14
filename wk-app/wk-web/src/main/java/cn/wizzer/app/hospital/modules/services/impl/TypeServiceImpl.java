package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Type;
import cn.wizzer.app.hospital.modules.services.TypeService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class TypeServiceImpl extends BaseServiceImpl<Type> implements TypeService {
    public TypeServiceImpl(Dao dao) {
        super(dao);
    }
}
