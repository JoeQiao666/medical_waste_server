package cn.wizzer.app.hospital.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("垃圾")
@Table("rubbish")
public class Rubbish extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @Comment("科室ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String departmentId;

    @Column
    @Comment("重量")
    @ColDefine(type = ColType.FLOAT)
    private float weight;

    @Column
    @Comment("类型ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String typeId;

    @Column
    @Comment("记录状态")
    @ColDefine(type = ColType.INT)
    private int status;

    @Column
    @Comment("操作员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String operatorId;

    @Column
    @Comment("医务人员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String staffId;

    @Column
    @Comment("仓库管理员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String administratorId;

    @Column
    @Comment("回收员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String recyclerId;

    @Column
    @Comment("回收公司ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String companyId;

    @Column
    @Comment("是否是盐水瓶")
    @ColDefine(type = ColType.BOOLEAN)
    private String isBottle;

    @Column
    @Comment("入库时间")
    @ColDefine(type = ColType.INT)
    private Integer storeAt;

    @Column
    @Comment("出库时间")
    @ColDefine(type = ColType.INT)
    private Integer recycleAt;

    public String getId() {
        return id;
    }

    public float getWeight() {
        return weight;
    }

    public int getStatus() {
        return status;
    }

    public Integer getStoreAt() {
        return storeAt;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setRecyclerId(String recyclerId) {
        this.recyclerId = recyclerId;
    }

    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
    }

    public void setRecycleAt(Integer recycleAt) {
        this.recycleAt = recycleAt;
    }

    public void setStoreAt(Integer storeAt) {
        this.storeAt = storeAt;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getRecyclerId() {
        return recyclerId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
