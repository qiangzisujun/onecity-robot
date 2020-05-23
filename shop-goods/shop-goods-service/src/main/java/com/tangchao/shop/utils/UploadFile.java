package com.tangchao.shop.utils;

import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * @Class UploadFile
 * @Description TODO
 * @Author Aquan
 * @Date 2020/4/7 11:45
 * @Version 1.0
 **/
public class UploadFile {


    /**
     * 文件上传方法
     */

    public static boolean fileUpLoad(MultipartFile[] files, HttpServletRequest request, String path)
            throws IOException {

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // 保存文件
                return saveFile(request, file, path);
            }
        }
        return false;
    }

    /**
     * 保存上传文件
     *
     * @param request
     * @param file
     * @return
     */

    public static boolean saveFile(HttpServletRequest request, MultipartFile file, String path) {

        if (!file.isEmpty()) {
            try {
                File saveDir = new File(path);
                if (!saveDir.getParentFile().exists())
                    saveDir.getParentFile().mkdirs();
                // 转存文件
                file.transferTo(saveDir);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
