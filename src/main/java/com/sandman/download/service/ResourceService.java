package com.sandman.download.service;

import com.sandman.download.common.repository.PageableTools;
import com.sandman.download.common.repository.SortDto;
import com.sandman.download.domain.*;
import com.sandman.download.repository.ResourceRepository;
import com.sandman.download.repository.myRepository.ResourceRepo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private ResourceRepo resourceRepo;

    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    /**
     * upload resource
     * */
    public BaseDto uploadRes(ResourceDTO resourceDTO, MultipartFile file){//上传不用修改USER表，所以直接拿过来登录信息就可以了
        if(file.isEmpty()){
            return new BaseDto(401,"上传文件为空!");
        }
        //开始做用户资源记录
        //User user = userService.findUserByUserName(SecurityUtils.getCurrentUserLogin().get());//根据userName查询user信息

        Long userId = SecurityUtils.getCurrentUserId();//登录的时候保存的信息，不用再次查询数据库

        resourceDTO.setUserId(userId);//设置UserId给resource
        resourceDTO.setOwnerName(SecurityUtils.getCurrentUserName());//设置资源所属用户

        String fileType = FileUtils.getSuffixNameByFileName(file.getOriginalFilename());
        fileType = (fileType==null || "".equals(fileType))?"file":fileType;//如果utils给出的文件类型为null，将file赋值给fileType
        String filePath = SftpParam.getPathPrefix() + "/" + fileType + "/" + userId + "/";//  /var/www/html/spkIMG + / + rar + / + userId + /

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
        resourceDTO.setResSize(String.valueOf(file.getSize()));//获取文件大小，存的时候不做操作，取得时候四舍五入带单位

        resourceDTO.setResType(fileType);
        resourceDTO.setDownCount(0);//设置默认下载数为0，因为是刚上传
        resourceDTO.setStatus(1);//0：已删除，1:正常
        resourceDTO.setCreateTime(DateUtils.getLongTime());//设置创建时间为当前时间
        resourceDTO.setUpdateTime(DateUtils.getLongTime());//设置更新时间为当前时间，因为刚上传
        Resource resource = resourceRepository.save(resourceMapper.toEntity(resourceDTO));//DTO转entity,保存
        //开始做用户上传日志记录
        UploadRecordDTO uploadRecordDTO = new UploadRecordDTO();
        uploadRecordDTO.setResId(resource.getId());//资源id
        uploadRecordDTO.setUserId(userId);//用户id
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
    public BaseDto downloadRes(Long resId,HttpServletResponse response)throws Exception{//下载需要修改USER表，需要再次去数据库查询
        Resource resource = resourceRepository.findOne(resId);
        if(resource==null){
            return new BaseDto(408,"资源不存在!");
        }
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);//根据ID查询出来整条resource

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
                return new BaseDto(403,"积分不足!");


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
                return new BaseDto(404,"下载出错!");
            }
        }else{//下载与登录为同一个人
            log.info("上传下载同一人");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=\"" + FileUtils.getRightFileNameUseCode(fileName) + "\"");// 设置文件名
            boolean success = FileUtils.download(FileUtils.getFilePathByUrl(resourceDTO.getResUrl()),fileName,response);
            if(!success){
                return new BaseDto(404,"下载出错!");
            }
        }
        return new BaseDto(200,"下载已完成!");
    }
    /**
     * update a resource
     * */
    public BaseDto updateResource(ResourceDTO resourceDTO){//更新一个resource
        log.info("update a resource:{}",resourceDTO.getId());
        Resource oriResource = resourceRepository.findOne(resourceDTO.getId());
        Long resOwnerId = oriResource.getUserId();
        if(!resOwnerId.equals(SecurityUtils.getCurrentUserId())){
            return new BaseDto(405,"无权修改!");
        }

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

        //ResourceDTO resource = getOneResource(resourceDTO.getId());

        return new BaseDto(200,"更新成功!",resource);
    }

    /**
     * Get all my resources.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Map getAllMyResources(Integer pageNumber,Integer size,Long userId,String sortType,String order) {
        log.debug("getAllMyResources page:{},size:{},order:{}",pageNumber,size,order);
        userId = (userId==null)?SecurityUtils.getCurrentUserId():userId;
        pageNumber = (pageNumber==null || pageNumber<1)?1:pageNumber;
        size = (size==null || size<0)?10:size;
        if(sortType==null || "".equals(sortType) || (!"ASC".equals(sortType.toUpperCase()) && !"DESC".equals(sortType.toUpperCase()))){
            sortType = "DESC";
        }
        order = (order==null || "".equals(order) || "null".equals(order))?"createTime":order;
        Pageable pageable = PageableTools.basicPage(pageNumber,size,new SortDto(sortType,order));//第一页就传page=1
        Page allResources = resourceRepo.findByUserId(userId,pageable);

        Map data = new HashMap();//最终返回的map

        data.put("totalRow",allResources.getTotalElements());
        data.put("totalPage",allResources.getTotalPages());
        data.put("currentPage",allResources.getNumber()+1);//默认0就是第一页
        data.put("resourceList",getFileSizeHaveUnit(allResources.getContent()));
        return data;
        //return new BaseDto(200,"查询成功!",data);
    }
    /**
     * 资源大小：存入数据库的时候统一以byte为单位，取出来给前端的时候要做规范 -> 转换成以 B,KB,MB,GB为单位
     * */
    public static List getFileSizeHaveUnit(List<Resource> resourceList){
        for(Resource resource:resourceList){
            String size = resource.getResSize();
            resource.setResSize(FileUtils.getFileSize(Long.parseLong(size)));
        }
/*        resourceList.forEach(resource -> {
            String size = resource.getResSize();
            resource.setResSize(FileUtils.getFileSize(Long.parseLong(size)));
        });*/
        return resourceList;
    }

    /**
     * Get one resource by id.
     */
    @Transactional(readOnly = true)
    public ResourceDTO getOneResource(Long id) {
        log.debug("Request to get Resource : {}", id);
        Resource resource = resourceRepository.getOneResourceById(id);
        if(resource==null)
            return null;
        Long fileSize = Long.parseLong(resource.getResSize());
        resource.setResSize(FileUtils.getFileSize(fileSize));
        return resourceMapper.toDto(resource);
        //return new BaseDto(200,"查询成功!",resourceDTO);
    }

    /**
     * Delete the resource by id.
     */
    public BaseDto delResource(Long id) {
        log.debug("Request to delete Resource : {}", id);
        Resource tempRes = resourceRepository.findOne(id);
        if(tempRes==null){
            return new BaseDto(408,"资源不存在!");
        }
        if(!tempRes.getUserId().equals(SecurityUtils.getCurrentUserId())){
            return new BaseDto(406,"无权删除!");
        }
        Integer success = resourceRepository.delResourceById(id);
        if(success==1){
            return new BaseDto(200,"删除成功!");
        }
        return new BaseDto(407,"删除失败!");
    }
}
