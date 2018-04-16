package com.sandman.download.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the User entity.
 */
public class UserDTO implements Serializable {

    private Long id;

    private String userName;

    private String password;

    private String mobile;

    private String email;

    private Integer gold;

    private Integer available;

    private Long createTime;

    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
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

        UserDTO userDTO = (UserDTO) o;
        if(userDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserDTO{" +
            "id=" + getId() +
            ", userName='" + getUserName() + "'" +
            ", password='" + getPassword() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", email='" + getEmail() + "'" +
            ", gold=" + getGold() +
            ", available=" + getAvailable() +
            ", createTime=" + getCreateTime() +
            ", updateTime=" + getUpdateTime() +
            "}";
    }
}
