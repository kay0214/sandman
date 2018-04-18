package com.sandman.download.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Resource.
 */
@Entity
@Table(name = "resource")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "res_name")
    private String resName;

    @Column(name = "res_url")
    private String resUrl;

    @Column(name = "res_gold")
    private Integer resGold;

    @Column(name = "res_desc")
    private String resDesc;

    @Column(name = "res_size")
    private String resSize;

    @Column(name = "res_type")
    private String resType;

    @Column(name = "down_count")
    private Integer downCount;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Resource userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResName() {
        return resName;
    }

    public Resource resName(String resName) {
        this.resName = resName;
        return this;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResUrl() {
        return resUrl;
    }

    public Resource resUrl(String resUrl) {
        this.resUrl = resUrl;
        return this;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public Integer getResGold() {
        return resGold;
    }

    public Resource resGold(Integer resGold) {
        this.resGold = resGold;
        return this;
    }

    public void setResGold(Integer resGold) {
        this.resGold = resGold;
    }

    public String getResDesc() {
        return resDesc;
    }

    public Resource resDesc(String resDesc) {
        this.resDesc = resDesc;
        return this;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public String getResSize() {
        return resSize;
    }

    public Resource resSize(String resSize) {
        this.resSize = resSize;
        return this;
    }

    public void setResSize(String resSize) {
        this.resSize = resSize;
    }

    public String getResType() {
        return resType;
    }

    public Resource resType(String resType) {
        this.resType = resType;
        return this;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public Integer getDownCount() {
        return downCount;
    }

    public Resource downCount(Integer downCount) {
        this.downCount = downCount;
        return this;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public Integer getStatus() {
        return status;
    }

    public Resource status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public Resource createTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public Resource updateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resource resource = (Resource) o;
        if (resource.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resource.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Resource{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", resName='" + getResName() + "'" +
            ", resUrl='" + getResUrl() + "'" +
            ", resGold=" + getResGold() +
            ", resDesc='" + getResDesc() + "'" +
            ", resSize='" + getResSize() + "'" +
            ", resType='" + getResType() + "'" +
            ", downCount=" + getDownCount() +
            ", status=" + getStatus() +
            ", createTime=" + getCreateTime() +
            ", updateTime=" + getUpdateTime() +
            "}";
    }
}
