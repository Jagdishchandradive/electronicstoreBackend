package com.ecommerce.electronicstore.service.impl;

import com.ecommerce.electronicstore.exception.BadApiRequest;
import com.ecommerce.electronicstore.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String UploadFile(MultipartFile file, String path) throws IOException {

        String originalFilename = file.getOriginalFilename();
       logger.info("Filename={}",originalFilename);
        String fileName= UUID.randomUUID().toString();
        String Ext=originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNameWithExt=fileName+Ext;
        String fullPathwithFileName=path+fileNameWithExt;

        logger.info("Full image path:{}",fullPathwithFileName);
        if(Ext.equalsIgnoreCase(".png")||Ext.equalsIgnoreCase(".jpg")||
        Ext.equalsIgnoreCase("jpeg")){
            logger.info("file extension is {}",Ext);
            File folder=new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(fullPathwithFileName));
            return fileNameWithExt;

        }else{
            throw new BadApiRequest("File with this "+Ext+"Not Allowed");

        }


    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullpath=path+File.separator+name;
        InputStream inputStream =new FileInputStream(fullpath);
        return inputStream;
    }
}
