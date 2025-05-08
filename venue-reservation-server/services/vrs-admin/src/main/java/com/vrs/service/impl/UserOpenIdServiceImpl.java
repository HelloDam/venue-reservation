package com.vrs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.domain.entity.UserOpenIdDO;
import com.vrs.mapper.UserOpenIdMapper;
import com.vrs.service.UserOpenIdService;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【user_openid(openid-username路由表)】的数据库操作Service实现
* @createDate 2025-01-09 14:47:46
*/
@Service
public class UserOpenIdServiceImpl extends ServiceImpl<UserOpenIdMapper, UserOpenIdDO>
    implements UserOpenIdService {

}




