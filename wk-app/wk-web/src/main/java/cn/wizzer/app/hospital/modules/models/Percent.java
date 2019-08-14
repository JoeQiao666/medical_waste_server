package cn.wizzer.app.hospital.modules.models;

import org.nutz.dao.entity.annotation.*;

@Comment("近30天个类型占比：饼状图（感染性、损伤性、病理性等所占比例）")
@Table("last_month_percent")
public class Percent {
    @Column
    @Comment("百分比")
    @ColDefine(type = ColType.FLOAT)
    private float percent;
    @Column
    @Comment("类别名称")
    @ColDefine(type = ColType.VARCHAR)
    private String name;
    @Column
    @Comment("是否是盐水瓶")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isBottle;

}
