package com.vrs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.idev.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.common.context.UserContext;
import com.vrs.common.entity.ExcelUserData;
import com.vrs.common.properties.WeChatProperties;
import com.vrs.common.utils.UsernameGenerator;
import com.vrs.constant.UserTypeConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.ServiceException;
import com.vrs.domain.dto.mq.ImportUserExcelMqDTO;
import com.vrs.domain.dto.req.UserLoginReqDTO;
import com.vrs.domain.dto.req.UserRegisterReqDTO;
import com.vrs.domain.dto.req.UserUpdateReqDTO;
import com.vrs.domain.dto.resp.UserLoginRespDTO;
import com.vrs.domain.dto.resp.UserRespDTO;
import com.vrs.domain.entity.OrganizationDO;
import com.vrs.domain.entity.UserDO;
import com.vrs.domain.entity.UserOpenIdDO;
import com.vrs.mapper.UserMapper;
import com.vrs.rocketMq.producer.ImportUserExcelProducer;
import com.vrs.service.OrganizationService;
import com.vrs.service.UserOpenIdService;
import com.vrs.service.UserService;
import com.vrs.service.excel.UserDataRowListener;
import com.vrs.utils.JwtUtil;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.vrs.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.vrs.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * @author dam
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-11-15 16:52:24
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
        implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final DataSource dataSource;
    private final WeChatProperties weChatProperties;
    private final UserOpenIdService userOpenIdService;
    private final OrganizationService organizationService;
    private final ImportUserExcelProducer importUserExcelProducer;

    private static final long EXPIRE_TIME = 300L;
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.HOURS;

    /**
     * excel 存储的根路径
     */
    public static final String EXCEL_TEMP_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "excel";

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 开始注册之前，判断用户名有没有被注册
        if (hasUsername(requestParam.getUserName())) {
            // --if-- 用户名已经存在了，抛异常
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
        // 使用Redisson的分布式锁，有看门狗机制，底层使用Netty来实现，网络通讯更加高效
        // LOCK_USER_REGISTER_KEY + requestParam.getUsername()：只锁注册的用户名
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUserName());
        try {
            if (lock.tryLock()) {
                try {
                    // 将用户数据保存到数据库
                    UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
                    userDO.setAvatar("/pic/2025/5/8/场快订吉祥物-53f626cc0bb0475ab13eab7feade376e.png");
                    userDO.setNickName(userDO.getUserName());
                    int inserted = baseMapper.insert(userDO);
                    if (inserted < 1) {
                        throw new ClientException(BaseErrorCode.USER_SAVE_ERROR);
                    }
                    // 保存成功，将注册成功的用户名保存到布隆过滤器
                    userRegisterCachePenetrationBloomFilter.add(requestParam.getUserName());
                } catch (DuplicateKeyException ex) {
                    // 数据库唯一索引异常（按理说这个是不会执行）
                    throw new ClientException(BaseErrorCode.USER_EXIST_ERROR);
                }
            } else {
                // --if-- 没有获取到锁，说明有其他用户正在注册，大概率注册都会成功的，返回用户名已经存在
                throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 直接用布隆过滤器判断用户名是否存在
     * - 布隆过滤器不存在，说明肯定不存在
     * - 布隆过滤器存在，可能产生误判，但是问题不大，部分用户名用不了也没啥关系
     *
     * @param username 用户名
     * @return
     */
    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
//        if (this.hasUsername(requestParam.getUserName()) == false) {
//            // --if-- 布隆过滤器中不存在，一定不存在
//            throw new ClientException(BaseErrorCode.USER_NULL_ERROR);
//        }

        //// 根据用户名密码查询，看看有没有匹配的用户
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUserName, requestParam.getUserName())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getIsDeleted, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在或者密码错误");
        }
        if (userDO.getStatus() != 0) {
            throw new ClientException("该账号已经停用");
        }

        return handleLogin(userDO);
    }

    /**
     * 处理登录，返回token
     *
     * @param userDO
     * @return
     */
    @Override
    public UserLoginRespDTO handleLogin(UserDO userDO) {
        //// 判断用户之前有没有登录，如果登录了直接返回token即可，防止有人一直刷接口
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + userDO.getUserName());
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            // 用户又登录了，刷新过期时间
            stringRedisTemplate.expire(USER_LOGIN_KEY + userDO.getUserName(), EXPIRE_TIME, EXPIRE_TIME_UNIT);
            // 如果已经登录，返回缓存的token
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() ->
                            // token为空
                            new ClientException("用户登录错误"));
            try {
                JwtUtil.getUserId(token);
                return new UserLoginRespDTO(token);
            } catch (Exception e) {
                // --if-- 如果抛异常，说明token有问题，可能过期了，需要继续执行下面的流程来重新生成token
            }
        }

        //// 存储用户信息
        // 使用jwt创建token
        String token = JwtUtil.createToken(userDO.getId(), userDO.getUserName(), userDO.getUserType(), userDO.getOrganizationId());
        // 将生成的token和用户信息存储到redis里面
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + userDO.getUserName(), token, JSON.toJSONString(userDO));
        // 设置过期时间
        stringRedisTemplate.expire(USER_LOGIN_KEY + userDO.getUserName(), EXPIRE_TIME, EXPIRE_TIME_UNIT);
        return new UserLoginRespDTO(token);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserLoginRespDTO wechatLogin(String code) {
        String openId = getOpenid(code);

        // 判断openid是否为空，如果为空则表示登录失败，抛出业务异常
        if (openId == null) {
            throw new ServiceException(BaseErrorCode.WECHAT_LOGIN_FAIL);
        }

        UserOpenIdDO userOpenIdDO = userOpenIdService.getOne(Wrappers.lambdaQuery(UserOpenIdDO.class).eq(UserOpenIdDO::getOpenId, openId));
        String userName;
        if (userOpenIdDO == null) {
            // --if-- 用户第一次使用微信登录
            userName = UsernameGenerator.generateUsername();
            while (hasUsername(userName)) {
                userName = UsernameGenerator.generateUsername();
            }
            // 创建用户
            this.register(UserRegisterReqDTO.builder()
                    .userName(userName)
                    .password(UUID.randomUUID().toString())
                    .build());
            // 存储路由
            userOpenIdService.save(
                    UserOpenIdDO.builder()
                            .openId(openId)
                            .userName(userName)
                            .build());
        } else {
            userName = userOpenIdDO.getUserName();
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUserName, userName)
                .eq(UserDO::getIsDeleted, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        return handleLogin(userDO);
    }

    /**
     * 用户退出登录
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        // 拦截器已经帮我验证了token的有效性，直接删除缓存即可
        String username = JwtUtil.getUsername(token);
        stringRedisTemplate.delete(USER_LOGIN_KEY + username);
    }

    @Override
    public UserRespDTO getUserByUserName(String userName) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .select(UserDO::getId, UserDO::getUserName, UserDO::getNickName,
                        UserDO::getPhoneNumber, UserDO::getEmail, UserDO::getGender,
                        UserDO::getAvatar, UserDO::getAvatarType,UserDO::getPoint,UserDO::getProfile)
                .eq(UserDO::getUserName, userName);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ServiceException(BaseErrorCode.USER_NULL_ERROR);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        String username = UserContext.getUsername();
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUserName, username);
        UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
        if (StringUtils.isNotBlank(userDO.getAvatar())) {
            if (userDO.getAvatar().startsWith("http")) {
                // --if-- 如果头像是远程链接，设置类型为 1
                userDO.setAvatarType(1);
            }
        }
        baseMapper.update(userDO, updateWrapper);
    }

    @Override
    @SneakyThrows
    public void userNameBloomFilterInit() {
        // 获取 dataSource Bean 的连接
        @Cleanup Connection conn = dataSource.getConnection();
        @Cleanup Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        // 查询sql，只查询关键的字段
        String sql = "SELECT user_name FROM user where is_deleted = 0";

        @Cleanup ResultSet rs = stmt.executeQuery(sql);

        // 每次获取一行数据进行处理，rs.next()如果有数据返回true，否则返回false
        while (rs.next()) {
            // 获取数据中的属性
            String userName = rs.getString("user_name");
            if (!userRegisterCachePenetrationBloomFilter.contains(userName)) {
                userRegisterCachePenetrationBloomFilter.add(userName);
            }
        }
    }

    @Override
    public void importUserExcel(MultipartFile file) {
        /// 数据校验
        // 校验用户是否有绑定机构
        Long organizationId = UserContext.getOrganizationId();
        if (organizationId == null) {
            throw new ClientException(BaseErrorCode.USER_NOT_SET_ORGANIZATION_ERROR);
        }
        // 校验用户权限，是否为机构管理员
        Integer userType = UserContext.getUserType();
        boolean flag = UserTypeConstant.validateInstituteManager(userType);
        if (!flag) {
            throw new ClientException(BaseErrorCode.USER_TYPE_IS_NOT_INSTITUTE_MANAGER_ERROR);
        }
        // 文件类型校验
        String absPath;
        String name = file.getOriginalFilename();
        if (!name.contains(".")) {
            // --if-- 如果图片没有正常后缀
            throw new ClientException(BaseErrorCode.NO_SUFFIX_ERROR);
        } else if (name.endsWith(".xlsx")) {
            absPath = EXCEL_TEMP_PATH + File.separator + UUID.randomUUID() + ".xlsx";
        } else if (name.endsWith(".xls")) {
            absPath = EXCEL_TEMP_PATH + File.separator + UUID.randomUUID() + ".xls";
        } else {
            throw new ClientException(BaseErrorCode.EXCEL_TYPE_ERROR);
        }

        /// 暂存文件到服务器本地
        // 如果不存在目录，创建目录
        if (!FileUtil.exist(EXCEL_TEMP_PATH)) {
            FileUtil.mkdir(EXCEL_TEMP_PATH);
        }
        // 将输入流中的数据复制到目标文件中
        try (InputStream is = file.getInputStream()) {
            File targetFile = new File(absPath);
            // 将输入流中的数据复制到目标文件中
            java.nio.file.Files.copy(is, targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }

        /// 发送消息，执行excel数据解析并将用户数据导入数据库
        SendResult sendResult = importUserExcelProducer.sendMessage(ImportUserExcelMqDTO.builder()
                .organizationId(UserContext.getOrganizationId())
                .excelPath(absPath)
                .build());
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            log.error("消息发送失败: " + sendResult.getSendStatus());
            throw new ServiceException(BaseErrorCode.MQ_SEND_ERROR);
        }

        // todo 记录导入任务到数据库中
    }

    /**
     * 正式执行excel数据解析与导入
     *
     * @param importUserExcelMqDTO
     */
    @Override
    public void handleImportUserExcel(ImportUserExcelMqDTO importUserExcelMqDTO) {
        OrganizationDO organizationDO = organizationService.getById(importUserExcelMqDTO.getOrganizationId());
        UserDataRowListener listener = new UserDataRowListener(this, importUserExcelMqDTO.getOrganizationId(), organizationDO.getMark());
        long start = System.currentTimeMillis();
        EasyExcel.read(importUserExcelMqDTO.getExcelPath(), ExcelUserData.class, listener).sheet().doRead();
        long end = System.currentTimeMillis();
        System.out.println("导入时间：" + (end - start) + "ms");
        // 删除暂存的 excel 文件
        FileUtil.del(importUserExcelMqDTO.getExcelPath());
    }

    @Override
    public void saveBatchIgnore(List<UserDO> userDOBatch) {
        if (userDOBatch.size() == 0) {
            return;
        }
        baseMapper.saveBatchIgnore(userDOBatch);
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        // 调用微信接口服务，获得当前微信用户的openid
        Map<String, Object> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppId());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

}




