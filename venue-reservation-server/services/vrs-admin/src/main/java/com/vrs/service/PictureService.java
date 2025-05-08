package com.vrs.service;

import com.vrs.convention.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author dam
 * @create 2024/11/21 17:12
 */
public interface PictureService {
    Result<Map<String, Object>> uploadFile(MultipartFile file, String request);
}
