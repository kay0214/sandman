package com.sandman.download.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A User.
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "jhi_password")
    private String password;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "gold")
    private Integer gold;

    @Column(name = "available")
    private Integer available;

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

    public String getUserName() {
        return userName;
    }

    public User userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public User mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGold() {
        return gold;
    }

    public User gold(Integer gold) {
        this.gold = gold;
        return this;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getAvailable() {
        return available;
    }

    public User available(Integer available) {
        this.available = available;
        return this;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public User createTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public User updateTime(Long updateTime) {
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
        User user = (User) o;
        if (user.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
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
