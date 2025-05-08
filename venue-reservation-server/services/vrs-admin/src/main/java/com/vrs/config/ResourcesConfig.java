package com.vrs.config;

import com.vrs.common.utils.PictureUploadUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author dam
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将图片请求路径映射到对应的本地图片路径
        registry.addResourceHandler(PictureUploadUtil.PIC_PREFIX + "/**")
                .addResourceLocations("file:" + PictureUploadUtil.UPLOAD_PATH + "/");
    }

}