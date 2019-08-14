package cn.wizzer.app.hospital.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("异常记录")
@Table("record")
public class Record extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("复核重量")
    @ColDefine(type = ColType.FLOAT)
    private float reviewWeight;

    @Column
    @Comment("上传重量")
    @ColDefine(type = ColType.FLOAT)
    private float updateWeight;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private int status;

    @Column
    @Comment("操作员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String operatorId;

    @Column
    @Comment("详情")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String detail;

    @Column
    @Comment("入库时间")
    @ColDefine(type = ColType.INT)
    private int storeAt;

    @Column
    @Comment("垃圾ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String rubbishId;

    @Column
    @Comment("是否推送过")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean pushFlag;

    public void setReviewWeight(float reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public void setUpdateWeight(float updateWeight) {
        this.updateWeight = updateWeight;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setStoreAt(int storeAt) {
        this.storeAt = storeAt;
    }

    public void setRubbishId(String rubbishId) {
        this.rubbishId = rubbishId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getRubbishId() {
        return rubbishId;
    }

    public void setPushFlag(Boolean pushFlag) {
        this.pushFlag = pushFlag;
    }
}
