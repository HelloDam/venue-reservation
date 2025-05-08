package com.vrs.common.utils;

import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.ServiceException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author dam
 * @create 2024/11/21 17:18
 */
public class PictureUploadUtil {
    /**
     * 资源映射路径 前缀
     */
    public static final String PIC_PREFIX = "/pic";
    /**
     * 图片存储的根路径
     */
    public static final String UPLOAD_PATH = System.getProperty("user.dir") + File.separator + "upload";
    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    public static final String upload(MultipartFile file) {
        int fileNameLength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNameLength > DEFAULT_FILE_NAME_LENGTH) {
            // --if-- 如果图片名称过程，抛异常
            throw new ClientException(BaseErrorCode.PICTURE_NAME_EXCEED_LENGTH);
        }

        String name = file.getOriginalFilename();
        if (!name.contains(".")) {
            // --if-- 如果图片没有正常后缀
            throw new ClientException(BaseErrorCode.NO_SUFFIX_ERROR);
        } else if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif"))) {
            // --if-- 校验文件是否为图片类型
            throw new ClientException(BaseErrorCode.PICTURE_TYPE_ERROR);
        }
        String[] split = name.split("\\.");
        // 图片名称
        String fileName = split[0];
        // 图片后缀
        String fileSuffix = split[1];

        // 获取当前日期
        LocalDate date = LocalDate.now();
        // 获取年份
        int year = date.getYear();
        // 获取月份
        int month = date.getMonthValue();
        // 获取日期
        int day = date.getDayOfMonth();
        String dir = File.separator + year + File.separator + month + File.separator + day;
        File dirFile = new File(UPLOAD_PATH + dir);
        if (!dirFile.exists()) {
            // 创建相应日期文件夹
            dirFile.mkdirs();
        }
        // 生成一个唯一ID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 相对路径
        String relativePath = dir + File.separator + fileName + "-" + uuid + "." + fileSuffix;
        // 生成图片要上传到的绝对路径
        String absPath = UPLOAD_PATH + relativePath;

        // 压缩存储
        try (InputStream is = file.getInputStream()) {
            // 设置目标文件
            File targetFile = new File(absPath);
            // 使用Thumbnails库调整图片分辨率
            Thumbnails.of(is)
                    // 设置最大宽度和高度，保持原始比例
                    .size(1920, 1080)
                    .outputQuality(0.7) // 设置压缩质量(0.0-1.0)
                    .toFile(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }

        // 直接保存文件（不压缩）
//        try (InputStream is = file.getInputStream()) {
//            File targetFile = new File(absPath);
//            // 将输入流中的数据复制到目标文件中
//            java.nio.file.Files.copy(is, targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new ServiceException(e.getMessage());
//        }

        return PIC_PREFIX + "/" + year + "/" + month + "/" + day + "/" + fileName + "-" + uuid + "." + fileSuffix;
    }

}
