package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.enums.ExceptionEnums;
import com.leyou.exception.LyException;
import com.leyou.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    private static final List<String> CONTENT_TYPE = Arrays.asList("image/jpeg", "image/gif","image/png","image/bmp");

    @Override
    public String uploadImage(MultipartFile file) {

        String originalFilename = file.getOriginalFilename(); // 获取原始文件名
        if (!CONTENT_TYPE.contains(file.getContentType())){ // 检验文件后缀名
            log.error("[image] 上传图片类型不符合");
            throw new LyException(ExceptionEnums.IMAGE_TYPE_NOT_CONFORM);
        }

        try { // 校验文件内容

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                log.error("[image] 上传图片内容不符合，不是图片");
                throw new LyException(ExceptionEnums.IMAGE_TYPE_NOT_CONFORM);
            }

            // file.transferTo(new File("G:\\icon\\img\\"+ originalFilename)); // 保存到本地
            String ext = StringUtils.substringAfterLast(originalFilename, "."); // 得到文件扩展名
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            return "http://image.leyou.com/" + storePath.getFullPath(); // 返回生产的url

        } catch (IOException e) {
            log.error("服务器内部错误");
            e.printStackTrace();
        }
        return null;
    }
}
