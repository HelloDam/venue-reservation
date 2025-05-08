package com.vrs.service.excel;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.vrs.common.entity.ExcelUserData;
import com.vrs.domain.entity.UserDO;
import com.vrs.service.UserService;
import com.vrs.utils.SnowflakeIdUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author dam
 * @create 2025/1/12 17:05
 */
@Slf4j
public class UserDataRowListener extends AnalysisEventListener<ExcelUserData> {

    private UserService userService;
    private Long organizationId;
    private String organizationMark;

    public UserDataRowListener(UserService userService, Long organizationId, String organizationMark) {
        this.userService = userService;
        this.organizationId = organizationId;
        this.organizationMark = organizationMark;
    }

    /**
     * excel表中的数据行数
     */
    @Getter
    private int rowCount = 0;

    /**
     * 一批的数据量
     */
    private final int BATCH_SIZE = 1000;

    /**
     * 最小批次大小
     */
    private final int MIN_BATCH_SIZE = 50;

    public static final String EXCEL_TEMP_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "excel";

    /**
     * 数据插入缓冲区，满了就存储到数据库中
     */
    private List<UserDO> userDOBuffer = new ArrayList<>();
    /**
     * 记录插入失败的数据
     */
    private List<UserDO> failedData = new ArrayList<>();


    /**
     * 处理每一行数据
     *
     * @param excelUserData
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelUserData excelUserData, AnalysisContext analysisContext) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(excelUserData, userDO);
        userDO.setOrganizationId(organizationId);
        userDO.setUserName(organizationMark + "_" + userDO.getUserName());
        userDO.setId(SnowflakeIdUtil.nextId());
        userDO.setCreateTime(new Date());
        userDOBuffer.add(userDO);
        if (userDOBuffer.size() >= BATCH_SIZE) {
            processBatch(userDOBuffer, BATCH_SIZE);
            userDOBuffer.clear();
        }
    }

    /**
     * 所有数据解析完成之后的操作
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (userDOBuffer.size() > 0) {
            processBatch(userDOBuffer, BATCH_SIZE);
            userDOBuffer.clear();
        }
        log.info("-------------------------- 机构用户全部导入完毕 ----------------------------");
        if (failedData.size() > 0) {
            String fileName = EXCEL_TEMP_PATH + File.separator + "fail " + UUID.randomUUID() + ".xlsx";
            // todo 如果错误的数据很多的话，之类需要优化，否则占用内存较高
            EasyExcel.write(fileName, ExcelUserData.class)
                    .sheet("导入失败用户")
                    .doWrite(failedData);
            // todo 导出excel之后，将excel请求路径存在到数据库中，供后续被管理员下载
        }
        // todo 修改数据库中的导入任务状态为成功或部分失败
    }

    /**
     * 处理批次数据
     *
     * @param batch     当前批次数据
     * @param batchSize 当前批次大小
     */
    private void processBatch(List<UserDO> batch, int batchSize) {
        try {
            // 尝试批量插入
            userService.saveBatchIgnore(batch);
        } catch (Exception e) {
            log.error("批量插入失败，当前批次大小：{}，尝试拆分批次", batchSize, e);
            if (batchSize > MIN_BATCH_SIZE) {
                // 拆分批次为更小的批次
                int newBatchSize = batchSize / 4;
                List<List<UserDO>> smallerBatches = splitBatch(batch, newBatchSize);
                for (List<UserDO> smallerBatch : smallerBatches) {
                    processBatch(smallerBatch, newBatchSize); // 递归处理更小的批次
                }
            } else {
                // 如果批次已经缩小到最小批次，逐条插入
                processSingleBatch(batch);
            }
        }
    }

    /**
     * 将批次拆分为更小的批次
     *
     * @param batch        原始批次
     * @param newBatchSize 新批次大小
     * @return 拆分后的批次列表
     */
    private List<List<UserDO>> splitBatch(List<UserDO> batch, int newBatchSize) {
        List<List<UserDO>> smallerBatches = new ArrayList<>();
        for (int i = 0; i < batch.size(); i += newBatchSize) {
            int end = Math.min(i + newBatchSize, batch.size());
            smallerBatches.add(batch.subList(i, end));
        }
        return smallerBatches;
    }

    /**
     * 逐条插入数据
     *
     * @param batch 当前批次数据
     */
    private void processSingleBatch(List<UserDO> batch) {
        for (UserDO userDO : batch) {
            try {
                // 逐条插入
                userService.save(userDO);
            } catch (Exception e) {
                log.error("单条插入失败，失败数据：{}", userDO, e);
                // 记录失败数据
                failedData.add(userDO);
            }
        }
    }

}
