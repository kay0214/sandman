package com.sandman.download.service;

import com.sandman.download.domain.*;
import com.sandman.download.repository.ResourceRepository;
import com.sandman.download.security.SecurityUtils;
import com.sandman.download.service.dto.DownloadRecordDTO;
import com.sandman.download.service.dto.ResourceDTO;
import com.sandman.download.service.dto.UploadRecordDTO;
import com.sandman.download.service.dto.UserDTO;
import com.sandman.download.service.mapper.ResourceMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Resource.
 */
@Service
@Transactional
public class ResourceService {

    private final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;

    private final ResourceMapper resourceMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private UploadRecordService uploadRecordService;
    @Autowired
    private ResourceRecordService resourceRecordService;
    @Autowired
    private DownloadRecordService downloadRecordService;

    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    /**
     * Save a resource.
     *
     * @param resourceDTO the entity to save
     * @return the persisted entity
     */
    public ResourceDTO save(ResourceDTO resourceDTO) {
        log.debug("Request to save Resource : {}", resourceDTO);
        Resource resource = resourceMapper.toEntity(resourceDTO);
        resource = resourceRepository.save(resource);
        return resourceMapper.toDto(resource);
    }
    /**
     * update a resource
     * */
    public BaseDto updateResource(ResourceDTO resourceDTO){
        log.info("update a resource:{}",resourceDTO.getId());
        Resource oriResource = resourceRepository.findOne(resourceDTO.getId());

        String resName = resourceDTO.getResName();
        if(resName!=null && !"".equals(resName)){
            oriResource.setResName(resName);
        }
        String resDesc = resourceDTO.getResDesc();
        if(resDesc!=null && !"".equals(resDesc)){
            oriResource.setResDesc(resDesc);
        }
        oriResource.setResGold(resourceDTO.getResGold());

        Resource resource = resourceRepository.save(oriResource);
        return new BaseDto(200,"更新成功!",resourceMapper.toDto(resource));
    }

    /**
     * Get all the resources.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ResourceDTO> findAll() {
        log.debug("Request to get all Resources");
        return resourceRepository.findAll().stream()
            .map(resourceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one resource by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResourceDTO findOne(Long id) {
        log.debug("Request to get Resource : {}", id);
        Resource resource = resourceRepository.findOne(id);
        return resourceMapper.toDto(resource);
    }

    /**
     * Delete the resource by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Resource : {}", id);
        resourceRepository.delete(id);
    }
    /**
     * upload resource
     * */
    public BaseDto uploadRes(ResourceDTO resourceDTO, MultipartFile file){
        if(file.isEmpty()){
            return new BaseDto(401,"上传文件为空!");
        }
        //开始做用户资源记录
        User user = userService.findUserByUserName(SecurityUtils.getCurrentUserLogin().get());//根据userName查询user信息

        resourceDTO.setUserId(user.getId());//设置UserId给resource

        String fileType = FileUtils.getSuffixNameByFileName(file.getOriginalFilename());
        fileType = (fileType==null || "".equals(fileType))?"file":fileType;//如果utils给出的文件类型为null，将file赋值给fileType
        String filePath = SftpParam.getPathPrefix() + "/" + fileType + "/" + user.getId() + "/";//  /var/www/html/spkIMG + / + rar + / + userId + /

        String resName = resourceDTO.getResName();
        log.info("用户传入的resName:{}",resName);
        resName = (resName==null || "".equals(resName) || "null".equals(resName))?FileUtils.getPrefixByFileName(file.getOriginalFilename()):resName;
        log.info("如果用户没有传入resName，默认取文件的名字:{}",resName);
        resourceDTO.setResName(resName);//如果用户设置的resName为空，将原文件名赋值给resName

        String fileName = ("file".equals(fileType))?resName:(resName + "." + fileType);

        log.info("fileName={}",fileName);

        resourceDTO.setResUrl(filePath + fileName);//设置文件url为 服务器路径+文件类型+userId+fileName
        log.info("resUrl={}",resourceDTO.getResUrl());
        //resourceDTO.setResGold(0);//用户填写的 下载资源所需积分
        //resourceDTO.setResDesc("");//用户填写的资源描述
        resourceDTO.setResSize(FileUtils.getFileSize(file.getSize()));//获取文件大小，四舍五入保留两位小数

        resourceDTO.setResType(fileType);
        resourceDTO.setDownCount(0);//设置默认下载数为0，因为是刚上传
        resourceDTO.setStatus(1);//0：已删除，1:正常
        resourceDTO.setCreateTime(DateUtils.getLongTime());//设置创建时间为当前时间
        resourceDTO.setUpdateTime(DateUtils.getLongTime());//设置更新时间为当前时间，因为刚上传
        Resource resource = resourceRepository.save(resourceMapper.toEntity(resourceDTO));//DTO转entity,保存
        //开始做用户上传日志记录
        UploadRecordDTO uploadRecordDTO = new UploadRecordDTO();
        uploadRecordDTO.setResId(resource.getId());//资源id
        uploadRecordDTO.setUserId(user.getId());//用户id
        uploadRecordDTO.setRecordTime(DateUtils.getLongTime());//记录时间

        UploadRecordDTO uploadRecord = uploadRecordService.save(uploadRecordDTO);//得到保存后的数据,带id
        //开始将文件上传到远程服务器
        File tempFile = FileUtils.getFileByMultipartFile(file);//MultiPartFile转File
        boolean uploadSuccess = FileUtils.upload(filePath,fileName,tempFile);//上传服务器
        tempFile.delete();
        if(!uploadSuccess){//如果上传远程服务器失败
            resourceRepository.delete(resource.getId());//删除资源数据
            uploadRecordService.delete(uploadRecord.getId());//删除日志记录
            return new BaseDto(402,"上传远程服务器失败!");
        }
        return new BaseDto(200,"上传成功!",resourceMapper.toDto(resource));
    }

    /**
     * download resource
     * */
    public void downloadRes(Long resId,HttpServletResponse response)throws IOException{
        ResourceDTO resourceDTO = resourceMapper.toDto(resourceRepository.findOne(resId));//根据ID查询出来整条resource
        String resName = FileUtils.getFileNameByUrl(resourceDTO.getResUrl());//根据url获取到文件名前缀，不带扩展名
        String fileName = ("file".equals(resourceDTO.getResType()))?resName:(resName + "." + resourceDTO.getResType());
        log.info("resource:{}",resourceDTO.toString());
        User resOwner = userService.findOne(resourceDTO.getUserId());//根据userId查询出资源拥有者

        User curUser = userService.findUserByUserName(SecurityUtils.getCurrentUserLogin().get());//根据用户名查询出当前登录的用户

        if(!curUser.getId().equals(resOwner.getId())){//当前登录用户与资源拥有者不是同一人
            log.info("上传下载不同人");
            //判断用户积分是否足够
            int curUserGold = curUser.getGold();//当前用户积分
            int resGold = resourceDTO.getResGold();//资源积分
            if(curUserGold<resGold)
                response.sendError(403,"积分不足!");


            //下载者写入积分详情
            ResourceRecord curUserRecord = resourceRecordService.reduceGold(curUser,resourceDTO,0,"下载资源，积分扣除");
            //资源拥有者写入积分详情
            ResourceRecord ownerRecord = resourceRecordService.addGold(resOwner,resourceDTO,0,"其他用户下载该资源，积分增加");

            //下载者写入下载记录
            DownloadRecordDTO downloadRecordDTO = downloadRecordService.save(curUser.getId(),resourceDTO.getId());

            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=\"" + FileUtils.getRightFileNameUseCode(fileName) + "\"");// 设置文件名
            boolean success = FileUtils.download(FileUtils.getFilePathByUrl(resourceDTO.getResUrl()),fileName,response);
            if(success){//如果下载成功
                log.info("上传下载不同人，下载成功");
                //用户积分操作: 下载者扣除积分，上传者加上积分
                log.info("curUserGold={},resGold={},ownerGold={}",curUserGold,resGold,resOwner.getGold());
                curUser.setGold(curUserGold - resGold);//如果积分足够，扣除相应积分
                int ownerGold = resOwner.getGold();
                resOwner.setGold(ownerGold + resGold);//资源拥有者加上相应积分
                userService.save(curUser);
                userService.save(resOwner);

            }else{//下载失败，两个用户信息还没有保存，所以只需要删除日志记录和积分记录即可
                log.info("上传下载不同人,下载失败");
                resourceRecordService.delete(curUserRecord.getId());//删除下载者积分记录
                resourceRecordService.delete(ownerRecord.getId());//删除资源拥有者积分记录
                downloadRecordService.delete(downloadRecordDTO.getId());//删除下载记录
                response.sendError(404,"下载出错!");
            }
        }else{//下载与登录为同一个人
            log.info("上传下载同一人");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=\"" + FileUtils.getRightFileNameUseCode(fileName) + "\"");// 设置文件名
            boolean success = FileUtils.download(FileUtils.getFilePathByUrl(resourceDTO.getResUrl()),fileName,response);
            if(!success){
                response.sendError(404,"下载出错!");
            }
        }

    }
}
