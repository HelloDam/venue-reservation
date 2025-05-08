package com.vrs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.domain.entity.LocalMessageDO;
import com.vrs.mapper.LocalMessageMapper;
import com.vrs.service.LocalMessageService;
import org.springframework.stereotype.Service;

/**
* @author dam
* @description 针对表【local_message(本地消息表)】的数据库操作Service实现
* @createDate 2025-05-01 18:31:43
*/
@Service
public class LocalMessageServiceImpl extends ServiceImpl<LocalMessageMapper, LocalMessageDO>
    implements LocalMessageService {

}




