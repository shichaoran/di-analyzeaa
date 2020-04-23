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


    List<String> list =Lists.newArrayList("H01001001","H01001002","H01001003","H01001004","H01001005","H01001006","H01001007","H01001008", "H01001009","H01001010","H01001011","H01001012",
            "H01002001","H01002002", "H01001003","H01002004","H01002005","H01002006","H01002007","H01002008",
            "H01003001","H01003002","H01003003","H01004001","H01004002","H01004003","H01004004","H01004005","H01004006","H01004007",
            "H01005001","H01005002","H01005003","H01005004","H02010001");

}
