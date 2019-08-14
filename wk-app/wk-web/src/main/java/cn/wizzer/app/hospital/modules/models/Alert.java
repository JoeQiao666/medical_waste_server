package cn.wizzer.app.hospital.modules.models;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("警戒值")
@Table("alert")
public class Alert implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("重量")
    @ColDefine(type = ColType.FLOAT)
    private float weight;
    @Column
    @Comment("百分比")
    @ColDefine(type = ColType.INT)
    private int percent;
    @Column
    @Comment("时间")
    @ColDefine(type = ColType.INT)
    private int time;

    public int getTime() {
        return time;
    }

    public int getPercent() {
        return percent;
    }
}
