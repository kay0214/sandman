package com.sandman.download.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ResourceRecord entity.
 */
public class ResourceRecordDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long resId;

    private Integer oriGold;

    private Integer resGold;

    private Integer curGold;

    private String resName;

    private String opDesc;

    private Long recordTime;

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

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public Integer getOriGold() {
        return oriGold;
    }

    public void setOriGold(Integer oriGold) {
        this.oriGold = oriGold;
    }

    public Integer getResGold() {
        return resGold;
    }

    public void setResGold(Integer resGold) {
        this.resGold = resGold;
    }

    public Integer getCurGold() {
        return curGold;
    }

    public void setCurGold(Integer curGold) {
        this.curGold = curGold;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getOpDesc() {
        return opDesc;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }

    public Long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Long recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceRecordDTO resourceRecordDTO = (ResourceRecordDTO) o;
        if(resourceRecordDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resourceRecordDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ResourceRecordDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", resId=" + getResId() +
            ", oriGold=" + getOriGold() +
            ", resGold=" + getResGold() +
            ", curGold=" + getCurGold() +
            ", resName='" + getResName() + "'" +
            ", opDesc='" + getOpDesc() + "'" +
            ", recordTime=" + getRecordTime() +
            "}";
    }
}
