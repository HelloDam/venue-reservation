package com.vrs.controller;

import com.vrs.common.context.UserContext;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.UserLoginReqDTO;
import com.vrs.domain.dto.req.UserRegisterReqDTO;
import com.vrs.domain.dto.req.UserUpdateReqDTO;
import com.vrs.domain.dto.resp.UserLoginRespDTO;
import com.vrs.domain.dto.resp.UserRespDTO;
import com.vrs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理控制层
 */
@RestController
@RequestMapping("/admin/user/")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    /**
     * 查询用户名是否存在
     */
    @Operation(summary = "查询用户名是否存在")
    @GetMapping("/v1/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册用户
     */
    @Operation(summary = "注册")
    @PostMapping("/v1/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @Operation(summary = "登录")
    @PostMapping("/v1/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 微信登录
     */
    @Operation(summary = "微信登录")
    @GetMapping("/v1/wechatLogin")
    public Result<UserLoginRespDTO> wechatLogin(@RequestParam("code") String code) {
        return Results.success(userService.wechatLogin(code));
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @Operation(summary = "登出")
    @DeleteMapping("/v1/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        userService.logout(token);
        return Results.success();
    }

    /**
     * 根据用户名查询用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/v1/getUserInfo")
    public Result<UserRespDTO> getUserInfo() {
        String username = UserContext.getUsername();
        return Results.success(userService.getUserByUserName(username));
    }

    /**
     * 根据用户名查询用户信息
     */
    @Operation(summary = "修改用户信息")
    @PostMapping("/v1/update")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 上传excel表，并解析导入机构用户数据
     */
    @Operation(summary = "机构数据excel导入")
    @PostMapping("/importUserExcel")
    public Result importUserExcel(MultipartFile file) throws Exception {
        userService.importUserExcel(file);
        return Results.success();
    }
}
