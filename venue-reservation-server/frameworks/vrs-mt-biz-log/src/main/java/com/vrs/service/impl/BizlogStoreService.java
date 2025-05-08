package com.vrs.service.impl;

import com.mzt.logapi.beans.CodeVariableType;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import com.vrs.entity.MtBizLog;
import com.vrs.service.MtBizLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author dam
 * @create 2024/12/15 21:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizlogStoreService implements ILogRecordService {

    private final MtBizLogService mtBizLogService;

    @Override
    public void record(LogRecord logRecord) {
        mtBizLogService.save(MtBizLog.builder()
                .tenant(logRecord.getTenant())
                .type(logRecord.getType())
                .subType(logRecord.getSubType())
                .className(logRecord.getCodeVariable().get(CodeVariableType.ClassName).toString())
                .methodName(logRecord.getCodeVariable().get(CodeVariableType.MethodName).toString())
                .operator(logRecord.getOperator())
                .action(logRecord.getAction())
                .extra(logRecord.getExtra())
                .status(logRecord.isFail() ? 1 : 0)
                .build());
    }

    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        return null;
    }

    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        return null;
    }
}
