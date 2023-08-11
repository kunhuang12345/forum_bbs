package com.hk.utils;

import com.hk.entity.config.AppConfig;
import com.hk.entity.config.WebConfig;
import com.hk.entity.dto.FileUploadDto;
import com.hk.entity.enums.DateTimePatternEnum;
import com.hk.entity.enums.FileUploadTypeEnum;
import com.hk.exception.BusinessException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.hk.entity.constants.Constants;
import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * 上传文件类
 */
@Component
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    @Resource
    private AppConfig appConfig;

    @Resource
    private ImageUtils imageUtils;

    public FileUploadDto uploadFile2Local(MultipartFile file, String folder, FileUploadTypeEnum uploadTypeEnum) throws BusinessException {
        try {
            FileUploadDto fileUploadDto = new FileUploadDto();
            // 文件原名
            String originalFileName = file.getOriginalFilename();
            // 文件后缀
            String fileSuffix = StringTools.getFileSuffix(originalFileName);
            if (originalFileName.length() > Constants.LENGTH_200) {
                originalFileName = StringTools.getFileName(originalFileName).substring(0,Constants.LENGTH_190) + fileSuffix;
            }

            if (!ArrayUtils.contains(uploadTypeEnum.getSuffixArray(),fileSuffix)) {
                throw new BusinessException("文件类型不正确");
            }

            // 月份目录
            String month = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());

            // 基本目录
            String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;

            // 图片存放最终目录
            File targetFileFolder = new File(baseFolder + folder + month + "/");

            // 生成的文件名
            String fileName = StringTools.getRandomString(Constants.LENGTH_15) + fileSuffix;

            // 生成的文件路径
            File targetFile = new File(targetFileFolder.getPath() + "/" + fileName);

            String localPath = month + "/" + fileName;

            if (uploadTypeEnum == FileUploadTypeEnum.AVATAR) {
                //头像上传
                targetFileFolder = new File(baseFolder + folder);
                targetFile = new File(targetFileFolder.getPath() + "/" + fileName);
                localPath = folder + Constants.AVATAR_SUFFIX;
            }

            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }

            file.transferTo(targetFile);

            // 压缩图片
            if (uploadTypeEnum == FileUploadTypeEnum.COMMENT_IMAGE) {
                String thumbnailName = targetFile.getName().replace(".","_.");
                File thumbnail = new File(targetFile.getParent() + "/" + thumbnailName);
                Boolean createThumbnail = imageUtils.createThumbnail(targetFile, Constants.LENGTH_200, Constants.LENGTH_200, thumbnail);
                if (!createThumbnail) {
                    org.apache.commons.io.FileUtils.copyFile(targetFile,thumbnail);
                }
            } else if (uploadTypeEnum == FileUploadTypeEnum.AVATAR || uploadTypeEnum == FileUploadTypeEnum.ARTICLE_COVER) {
                imageUtils.createThumbnail(targetFile,Constants.LENGTH_200,Constants.LENGTH_200,targetFile);
            }

            fileUploadDto.setLocalPath(localPath);
            fileUploadDto.setOriginalFileName(originalFileName);
            return fileUploadDto;
        } catch (Exception e) {
            logger.error("上传文件失败");
            throw new BusinessException("上传文件失败");
        }
    }

}
