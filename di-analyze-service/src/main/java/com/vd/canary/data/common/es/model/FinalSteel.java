package com.vd.canary.data.common.es.model;

import com.google.common.collect.Lists;
import jdk.dynalink.linker.LinkerServices;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FinalSteel implements Serializable {

//   private final String H01001001="H01001001";//    H01001001       钢板/卷
//   private final String H01001002="H01001002";// H01001002       螺纹钢
//   private final String H01001003="H01001003";// H01001003       圆钢
//   private final String H01001004="H01001004";//  H01001004     钢管
//   private final String H01001005="H01001005";//H01001005       角钢
//   private final String H01001006="H01001006";//H01001006       槽钢
//   private final String H01001007="H01001007";//H01001007       工字钢
//   private final String H01001008="H01001008";//H01001008       扁钢
//   private final String H01001009="H01001009";//H01001009       H型钢
//   private final String H01001010="H01001010";//H01001010       其它型钢
//   private final String H01001011="H01001011";//H01001011       线材及冷拔钢筋
//   private final String H01001012="H01001012";//H01001012       钢绞线/钢丝束
//   private final String H01002001="H01002001";//H01002001       彩涂板/卷
//   private final String H01002002="H01002002";//H01002002       镀锌板/卷
//   private final String H01002003="H01002003";//H01002003       钢格板/配件
//   private final String H01002004="H01002004";//H01002004       采光板
//   private final String H01002005="H01002005";//H01002005       楼承板
//   private final String H01002006="H01002006";//H01002006       钢质复合板
//   private final String H01002007="H01002007";//H01002007       檩条
//   private final String H01002008="H01002008";//H01002008       钢龙骨
//   private final String H01003001="H01003001";//H01003001       焊材
//   private final String H01003002="H01003002";//H01003002       焊接辅材
//   private final String H01003003="H01003003";//H01003003       钻/磨/切割辅耗材
//   private final String H01004001="H01004001";//H01004001       螺栓/螺母及垫片
//   private final String H01004002="H01004002";//H01004002       化学锚栓
//   private final String H01004003="H01004003";// H01004003       栓钉
//   private final String H01004004="H01004004";// H01004004       自攻钉
//   private final String H01004005="H01004005";//  H01004005       牛角钉
//   private final String H01004006="H01004006";// H01004006       射钉
//   private final String H01004007="H01004007";// H01004007       套筒
//   private final String H01005001="H01005001";//  H01005001       屈曲支撑及配件
//   private final String H01005002="H01005002";// H01005002       阻尼器
//   private final String H01005003="H01005003";// H01005003       钢拉索
//   private final String H01005004="H01005004";//  H01005004       支座
//   private final String H02010001="H02010001";//  H02010001       防腐防霉涂料
    List<String> list =Lists.newArrayList("H01001001","H01001002","H01001003","H01001004","H01001005","H01001006","H01001007","H01001008", "H01001009","H01001010","H01001011","H01001012",
            "H01002001","H01002002", "H01001003","H01002004","H01002005","H01002006","H01002007","H01002008",
            "H01003001","H01003002","H01003003","H01004001","H01004002","H01004003","H01004004","H01004005","H01004006","H01004007",
            "H01005001","H01005002","H01005003","H01005004","H02010001");



}
