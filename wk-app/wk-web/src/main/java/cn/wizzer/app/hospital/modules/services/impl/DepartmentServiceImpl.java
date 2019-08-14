package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Department;
import cn.wizzer.app.hospital.modules.services.DepartmentService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class DepartmentServiceImpl extends BaseServiceImpl<Department> implements DepartmentService {
    public DepartmentServiceImpl(Dao dao) {
        super(dao);
    }
}
