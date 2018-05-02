package com.taotao.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.manage.bean.PicUploadResult;
import com.taotao.manage.service.PropertieService;

@Controller
@RequestMapping("/pic")
public class PicUploadController {
    
    private Logger logger = LoggerFactory.getLogger(PicUploadController.class);
    
    @Autowired
    private PropertieService propertieService;

    private static final ObjectMapper mapper = new ObjectMapper();

    // 校验文件类型
    private static final String[] IMAGE_TYPE = new String[] { ".bmp", ".jpg", ".jpeg", ".gif", ".png"};

    /**
     * 这里返回的String不是视图名，而是返回一个‘文本类型的json数据’
     * 
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("uploadFile") MultipartFile multipartFile, HttpServletResponse response)
            throws Exception {
        //校验图片格式
        boolean isLegal = false;
        for(String type : IMAGE_TYPE){
            if(StringUtils.endsWithIgnoreCase(multipartFile.getOriginalFilename(), type)){
                isLegal = true;
                break;
            }
        }
        //封装Result对象，并将图片的byte数组放到result对象中。
        PicUploadResult fileUploadResult = new PicUploadResult();
        fileUploadResult.setError(isLegal ? 0 : 1);
        //文件新地址
        String filePath = getFilePath(multipartFile.getOriginalFilename());
        
        if(logger.isDebugEnabled()){
            logger.debug("Pic file upload .[{}] to [{}] .", multipartFile.getOriginalFilename(), filePath);
        }
        
        //生成图片的绝对引用地址
        String picUrl = StringUtils.replace(StringUtils.substringAfter(filePath, propertieService.REPOSITORY_PATH), "\\", "/");
        fileUploadResult.setUrl(propertieService.IMAGE_BASE_URL + picUrl); 
        File newFile = new File(filePath);
        
        //将文件写到磁盘
        multipartFile.transferTo(newFile);
        
        //校验图片是否合法
        isLegal = false;
        try {
            BufferedImage image = ImageIO.read(newFile);
            if(image != null){
                fileUploadResult.setHeight(image.getHeight() + "");
                fileUploadResult.setWidth(image.getWidth() + "");
                isLegal = true;
            }
        } catch (Exception e) {
        }
        
        fileUploadResult.setError(isLegal ? 0 : 1);
        
        if(!isLegal){
            //不合法，将磁盘上的文件删掉
            newFile.delete();
        }
        
        response.setContentType(MediaType.TEXT_PLAIN.toString());
        return mapper.writeValueAsString(fileUploadResult);
    }

    private String getFilePath(String originalFilename) {
        String baseFolder = propertieService.REPOSITORY_PATH + File.separator + "image";
        // yyyyMMdd
        Date nowDate = new Date();
        String fileFolder = baseFolder + File.separator + new DateTime(nowDate).toString("yyyy") + 
                File.separator + new DateTime(nowDate).toString("MM") + 
                File.separator + new DateTime(nowDate).toString("dd");
        File file = new File(fileFolder);
        if(!file.exists()){
            file.mkdirs();
        }
        
        //生成新的文件名    20180428  010543  9590  3608.jpg
        String fileName = new DateTime(nowDate).toString("yyyyMMddhhmmssSSSS") + 
                RandomUtils.nextInt(100, 1100) + "." + StringUtils.substringAfterLast(originalFilename, ".");
        return fileFolder + File.separator + fileName;
    }

}
