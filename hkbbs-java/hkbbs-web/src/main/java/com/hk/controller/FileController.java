package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.WebConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController extends ABaseController {

    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private WebConfig webConfig;

    @RequestMapping("/upLoadImage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO upLoadImage(MultipartFile file) throws BusinessException {
        if (file == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String fileName = file.getOriginalFilename();
        String fileExtName = StringTools.getFileSuffix(fileName);
        if (!ArrayUtils.contains(Constants.IMAGE_SUFFIX,fileExtName)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String path = copyFile(file);
        Map<String,String> fileMap = new HashMap<>();
        fileMap.put("fileName",path);
        return getSuccessResponseVO(fileMap);
    }

    private String copyFile(MultipartFile file) throws BusinessException {
        try {
            String fileName = file.getOriginalFilename();
            String fileExtName = StringTools.getFileSuffix(fileName);
            String fileRealName = StringTools.getRandomString(Constants.LENGTH_30) + fileExtName;
            String folderPath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_TEMP + "/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File upLoadFile = new File(folderPath + fileRealName);
            file.transferTo(upLoadFile);
            return Constants.FILE_FOLDER_TEMP + "/" + fileRealName;
        } catch (Exception e) {
            logger.error("上传文件失败",e);
            throw new BusinessException("上传文件失败");
        }
    }

    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageFolder") String imageFolder,
                         @PathVariable("imageName") String imageName) {
        readImage(response,imageFolder,imageName);
    }

    @RequestMapping("/getAvatar/{userId}")
    public void getAvatar(HttpServletResponse response,
                         @PathVariable("userId") String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        String avatarPath = webConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;

        File avatarFolder = new File(webConfig.getProjectFolder() + avatarFolderName);
        if (!avatarFolder.exists()) {
            avatarFolder.mkdirs();
        }
        String imageName = userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        // 文件不存在使用默认头像
        if (!file.exists()) {
            imageName = Constants.AVATAR_DEFAULT;
        }
        readImage(response,Constants.FILE_FOLDER_AVATAR_NAME,imageName);
    }

    private void readImage(HttpServletResponse response, String imageFolder, String imageName) {
        ServletOutputStream sos = null;
        FileInputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            if (StringTools.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
                return;
            }
            String imageSuffix = StringTools.getFileSuffix(imageName);
            String filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_IMAGE + imageFolder + "/" + imageName;
            if (Constants.FILE_FOLDER_TEMP.equals(imageFolder) || imageFolder.contains(Constants.FILE_FOLDER_AVATAR_NAME)) {
                filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            imageSuffix = imageSuffix.replace(".","");
            if (!Constants.FILE_FOLDER_AVATAR_NAME.equals(imageFolder)) {

                // 设置缓存时间
                response.setHeader("Cache-Control","max-age-2592000");
            }
            response.setContentType("image/" + imageSuffix);
            in = new FileInputStream(file);
            sos = response.getOutputStream();
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while (-1 != (ch = in.read())) {
                baos.write(ch);
            }
            sos.write(baos.toByteArray());
            sos.flush();
            baos.flush();
        }catch (Exception e) {
            logger.error("读取图片异常",e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
