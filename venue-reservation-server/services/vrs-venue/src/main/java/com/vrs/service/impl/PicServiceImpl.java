package com.vrs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.domain.entity.PictureDO;
import com.vrs.mapper.PictureMapper;
import com.vrs.service.PicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Admin
 * @description 针对表【picture】的数据库操作Service实现
 * @createDate 2024-12-24 19:07:18
 */
@Service
public class PicServiceImpl extends ServiceImpl<PictureMapper, PictureDO>
        implements PicService {

    @Override
    public void savePicList(Long itemId, List<String> pictureList, int itemType) {
        List<PictureDO> pictureDOList = pictureList.stream().map(pic -> {
            return PictureDO.builder()
                    .picture(pic)
                    .itemId(itemId)
                    .itemType(itemType)
                    .build();
        }).collect(Collectors.toList());
        this.saveBatch(pictureDOList);
    }

    @Override
    public void deletePicList(Long itemId, List<Long> pictureItemIdList, int itemType) {
        if (pictureItemIdList.size() == 0) {
            return;
        }
        QueryWrapper<PictureDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.in("id", pictureItemIdList);
        deleteWrapper.eq("item_id", itemId);
        deleteWrapper.eq("item_type", itemType);
        this.remove(deleteWrapper);
    }
}




