package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.mq.TimePeriodStockReduceMqDTO;
import com.vrs.domain.dto.req.ListTimePeriodByDateRepDTO;
import com.vrs.domain.dto.req.PeriodDateAndTimePeriodMapRepDTO;
import com.vrs.domain.dto.req.TimePeriodListReqDTO;
import com.vrs.domain.dto.req.TimePeriodStockRestoreReqDTO;
import com.vrs.domain.dto.resp.TimePeriodRespDTO;
import com.vrs.domain.entity.OrderDO;
import com.vrs.domain.entity.TimePeriodDO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author dam
 * @description 针对表【time_period_0】的数据库操作Service
 * @createDate 2024-11-17 16:35:42
 */
public interface TimePeriodService extends IService<TimePeriodDO> {

    OrderDO reserve1(Long timePeriodId, Integer courtIndex);

    String reserve2(Long timePeriodId, Integer courtIndex);

    void batchPublishTimePeriodOptimize(List<TimePeriodDO> batch, boolean isCache);

    void batchPublishTimePeriod(List<TimePeriodDO> batch, boolean isCache);

    void reduceStockAndBookedSlots(TimePeriodStockReduceMqDTO timePeriodStockUpdateMqDTO);

    void restoreStockAndBookedSlotsDatabase(TimePeriodStockRestoreReqDTO message);

    void restoreStockAndBookedSlotsCache(Long timePeriodId, Long userId, Long courtIndex, String stockKey, String freeIndexBitMapKey);

    OrderDO executePreserveV1(TimePeriodDO timePeriodDO, Long courtIndex, Long venueId, String stockKey, String freeIndexBitMapKey);

    byte[] getBitArrayFromCahe(String freeIndexBitmapKey, Long timePeriodId, int initStock);

    TimePeriodDO getTimePeriodDOById(Long timePeriodId);

    Integer getStockByTimePeriodId(Long timePeriodId);

    Integer getStockByTimePeriodId(String keyName, Long timePeriodId);

    List<Integer> getBookedListByTimePeriodId(Long timePeriodId, Long partitionId);

    void restoreStockAndBookedSlots(TimePeriodStockRestoreReqDTO timePeriodStockRestoreReqDTO);

    List<TimePeriodDO> listTimePeriodWithIdList(List<Long> timePeriodIdList, List<Long> partitionIdList);

    PageResponse<TimePeriodDO> pageTimePeriodDO(TimePeriodListReqDTO request);

    LinkedHashMap<String, List<TimePeriodRespDTO>> getPeriodDateAndTimePeriodMap(PeriodDateAndTimePeriodMapRepDTO request);

    TimePeriodRespDTO infoById(Long id);

    void checkBitMapCache(String freeIndexBitmapKey, Long timePeriodId, int initStock);

    void initializeFreeIndexBitmap(String freeIndexBitmapKey, int initStock, long longValue, long expireSecond);

    List<TimePeriodRespDTO> listTimePeriodByDate(ListTimePeriodByDateRepDTO listTimePeriodByDateRepDTO);
}
