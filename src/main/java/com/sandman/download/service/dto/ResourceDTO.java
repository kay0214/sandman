package com.sandman.download.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Resource entity.
 */
public class ResourceDTO implements Serializable {

    private Long id;

    private Long userId;

    private String resName;

    private String resUrl;

    private Integer resGold;

    private String resDesc;

    private String resSize;

    private Integer resType;

    private Integer downCount;

    private Integer status;

    private Long createTime;

    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public Integer getResGold() {
        return resGold;
    }

    public void setResGold(Integer resGold) {
        this.resGold = resGold;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public String getResSize() {
        return resSize;
    }

    public void setResSize(String resSize) {
        this.resSize = resSize;
    }

    public Integer getResType() {
        return resType;
    }

    public void setResType(Integer resType) {
        this.resType = resType;
    }

    public Integer getDownCount() {
        return downCount;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceDTO resourceDTO = (ResourceDTO) o;
        if(resourceDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resourceDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ResourceDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", resName='" + getResName() + "'" +
            ", resUrl='" + getResUrl() + "'" +
            ", resGold=" + getResGold() +
            ", resDesc='" + getResDesc() + "'" +
            ", resSize='" + getResSize() + "'" +
            ", resType=" + getResType() +
            ", downCount=" + getDownCount() +
            ", status=" + getStatus() +
            ", createTime=" + getCreateTime() +
            ", updateTime=" + getUpdateTime() +
            "}";
    }
}
