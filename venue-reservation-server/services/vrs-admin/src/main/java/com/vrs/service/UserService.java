package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.domain.dto.mq.ImportUserExcelMqDTO;
import com.vrs.domain.dto.req.UserLoginReqDTO;
import com.vrs.domain.dto.req.UserRegisterReqDTO;
import com.vrs.domain.dto.req.UserUpdateReqDTO;
import com.vrs.domain.dto.resp.UserLoginRespDTO;
import com.vrs.domain.dto.resp.UserRespDTO;
import com.vrs.domain.entity.UserDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author dam
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-11-15 16:52:24
 */
public interface UserService extends IService<UserDO> {

    /**
     * 注册用户
     *
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 用户登录请求参数
     * @return 用户登录返回参数 Token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    UserLoginRespDTO handleLogin(UserDO userDO);

    UserLoginRespDTO wechatLogin(String code);

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @return 用户名存在返回 True，不存在返回 False
     */
    Boolean hasUsername(String username);

    /**
     * 注销用户登录
     *
     * @param token
     */
    void logout(String token);

    UserRespDTO getUserByUserName(String username);

    void update(UserUpdateReqDTO requestParam);

    void userNameBloomFilterInit();

    void importUserExcel(MultipartFile file);

    void handleImportUserExcel(ImportUserExcelMqDTO message);

    void saveBatchIgnore(List<UserDO> userDOBatch);
}
