package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Company;
import cn.wizzer.app.hospital.modules.services.CompanyService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class CompanyServiceImpl extends BaseServiceImpl<Company> implements CompanyService {
    public CompanyServiceImpl(Dao dao) {
        super(dao);
    }
}
