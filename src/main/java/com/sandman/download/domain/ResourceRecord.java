package com.sandman.download.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ResourceRecord.
 */
@Entity
@Table(name = "resource_record")
public class ResourceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "res_id")
    private Long resId;

    @Column(name = "ori_gold")
    private Integer oriGold;

    @Column(name = "res_gold")
    private Integer resGold;

    @Column(name = "cur_gold")
    private Integer curGold;

    @Column(name = "res_name")
    private String resName;

    @Column(name = "op_desc")
    private String opDesc;

    @Column(name = "record_time")
    private Long recordTime;

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

    public ResourceRecord userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResId() {
        return resId;
    }

    public ResourceRecord resId(Long resId) {
        this.resId = resId;
        return this;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public Integer getOriGold() {
        return oriGold;
    }

    public ResourceRecord oriGold(Integer oriGold) {
        this.oriGold = oriGold;
        return this;
    }

    public void setOriGold(Integer oriGold) {
        this.oriGold = oriGold;
    }

    public Integer getResGold() {
        return resGold;
    }

    public ResourceRecord resGold(Integer resGold) {
        this.resGold = resGold;
        return this;
    }

    public void setResGold(Integer resGold) {
        this.resGold = resGold;
    }

    public Integer getCurGold() {
        return curGold;
    }

    public ResourceRecord curGold(Integer curGold) {
        this.curGold = curGold;
        return this;
    }

    public void setCurGold(Integer curGold) {
        this.curGold = curGold;
    }

    public String getResName() {
        return resName;
    }

    public ResourceRecord resName(String resName) {
        this.resName = resName;
        return this;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getOpDesc() {
        return opDesc;
    }

    public ResourceRecord opDesc(String opDesc) {
        this.opDesc = opDesc;
        return this;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }

    public Long getRecordTime() {
        return recordTime;
    }

    public ResourceRecord recordTime(Long recordTime) {
        this.recordTime = recordTime;
        return this;
    }

    public void setRecordTime(Long recordTime) {
        this.recordTime = recordTime;
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
        ResourceRecord resourceRecord = (ResourceRecord) o;
        if (resourceRecord.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resourceRecord.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ResourceRecord{" +
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
