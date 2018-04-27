package com.sandman.download.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ValidateCode entity.
 */
public class ValidateCodeDTO implements Serializable {

    private Long id;

    private String contact;

    private String code;

    private Long createTime;

    private Long deadLine;

    private Integer isValid;

    private Integer sended;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Long deadLine) {
        this.deadLine = deadLine;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getSended() {
        return sended;
    }

    public void setSended(Integer sended) {
        this.sended = sended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ValidateCodeDTO validateCodeDTO = (ValidateCodeDTO) o;
        if(validateCodeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), validateCodeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ValidateCodeDTO{" +
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
