package com.tangchao.shop.service;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Autowired
    private UploadProperties prop;

    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();

            if (!prop.getAllowTypes().contains(contentType)) {
                throw new CustomerException(ExceptionEnum.INVALID_FILE_TYPE);
            }


            //检验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new CustomerException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            //返回路径
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            //上传失败
            log.error("[文件]上传失败！", e);
            throw new CustomerException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }


    }
}
