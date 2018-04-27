package com.sandman.download.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ValidateCode.
 */
@Entity
@Table(name = "validate_code")
public class ValidateCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contact")
    private String contact;

    @Column(name = "code")
    private String code;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "dead_line")
    private Long deadLine;

    //是否可用:1可用；0不可用
    @Column(name = "is_valid")
    private Integer isValid;
    //是否已发送：1已发送；0未发送
    @Column(name = "sended")
    private Integer sended;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public ValidateCode contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public ValidateCode code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public ValidateCode createTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getDeadLine() {
        return deadLine;
    }

    public ValidateCode deadLine(Long deadLine) {
        this.deadLine = deadLine;
        return this;
    }

    public void setDeadLine(Long deadLine) {
        this.deadLine = deadLine;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public ValidateCode isValid(Integer isValid) {
        this.isValid = isValid;
        return this;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getSended() {
        return sended;
    }

    public ValidateCode sended(Integer sended) {
        this.sended = sended;
        return this;
    }

    public void setSended(Integer sended) {
        this.sended = sended;
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
        ValidateCode validateCode = (ValidateCode) o;
        if (validateCode.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), validateCode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ValidateCode{" +
            "id=" + getId() +
            ", contact='" + getContact() + "'" +
            ", code='" + getCode() + "'" +
            ", createTime=" + getCreateTime() +
            ", deadLine=" + getDeadLine() +
            ", isValid=" + getIsValid() +
            ", sended=" + getSended() +
            "}";
    }
}
