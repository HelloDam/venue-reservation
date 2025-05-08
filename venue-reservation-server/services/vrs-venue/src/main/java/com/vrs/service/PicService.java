package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.domain.entity.PictureDO;

import java.util.List;

/**
* @author Admin
* @description 针对表【picture】的数据库操作Service
* @createDate 2024-12-24 19:07:18
*/
public interface PicService extends IService<PictureDO> {
    void savePicList(Long itemId, List<String> pictureList, int itemType);

    void deletePicList(Long itemId, List<Long> pictureItemIdList, int itemType);
}
