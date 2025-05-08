package com.vrs.controller;

import com.vrs.convention.result.Result;
import com.vrs.service.PictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author dam
 * @create 2024/11/21 17:00
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/picture/")
@Tag(name = "图片相关")
public class PictureController {

    private final PictureService pictureService;

    /**
     * 通用上传请求（单个）
     */
    @Operation(summary = "图片上传")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(MultipartFile file, HttpServletRequest request) throws Exception {
        String gatewayHost = request.getHeader("X-Gateway-Host");
        return pictureService.uploadFile(file, gatewayHost);
    }
}
