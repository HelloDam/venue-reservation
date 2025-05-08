package com.vrs.bizlog.parse;

import com.mzt.logapi.service.IParseFunction;
import com.vrs.enums.PartitionTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2024/12/15 16:43
 */
@Component
public class PartitionStatusEnumParse implements IParseFunction {

    @Override
    public String functionName() {
        return "PartitionStatusEnumParse";
    }

    @Override
    public String apply(Object value) {
        return PartitionTypeEnum.findValueByType(Integer.parseInt(value.toString()));
    }
}
