package com.vrs.service.impl;

import com.vrs.common.utils.PictureUploadUtil;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author dam
 * @create 2024/11/21 17:12
 */
@Service
public class PictureServiceImpl implements PictureService {

    @Override
    public Result<Map<String, Object>> uploadFile(MultipartFile file, String gatewayDomain) {
        // 上传并返回新文件名称
        String fileName = PictureUploadUtil.upload(file);
        String url = gatewayDomain + fileName;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("url", url);
        resultMap.put("fileName", fileName);
        resultMap.put("originalFileName", file.getOriginalFilename());
        return Results.success(resultMap);
    }
}
