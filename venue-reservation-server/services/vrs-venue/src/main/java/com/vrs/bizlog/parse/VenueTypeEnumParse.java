package com.vrs.bizlog.parse;

import com.mzt.logapi.service.IParseFunction;
import com.vrs.enums.VenueStatusEnum;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2024/12/15 16:43
 */
@Component
public class VenueTypeEnumParse implements IParseFunction {

    @Override
    public String functionName() {
        return "VenueTypeEnumParse";
    }

    @Override
    public String apply(Object value) {
        return VenueStatusEnum.findValueByType(Integer.parseInt(value.toString()));
    }
}
