package cn.wizzer.app.hospital.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("公司")
@Table("company")
public class Company extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;
    @Column
    @Comment("电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String telephone;
    @Column
    @Comment("负责人姓名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String chargeName;
    @Column
    @Comment("负责人电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String chargePhone;
    @Column
    @Comment("地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String address;
    @Column
    @Comment("性别")
    @ColDefine(type = ColType.INT)
    private int gender;
    @Column
    @Comment("年龄")
    @ColDefine(type = ColType.INT)
    private int age;
    @Column
    @Comment("描述")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String description;
    @Column
    @Comment("卡号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String cardNumber;
    @Column
    @Comment("市")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String city;
    @Column
    @Comment("县")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String county;

    public String getId() {
        return id;
    }
}
