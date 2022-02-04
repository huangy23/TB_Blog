package com.treebee.blog.entity;

public class AdminUser {
    private Integer admin_user_id;

    private String login_user_name;

    private String login_password;

    private String nick_name;

    private Byte locked;

    public Integer getAdminUserId() {
        return admin_user_id;
    }

    public void setAdminUserId(Integer adminUserId) {
        this.admin_user_id = adminUserId;
    }

    public String getLoginUserName() {
        return login_user_name;
    }

    public void setLoginUserName(String loginUserName) {
        this.login_user_name = loginUserName == null ? null : loginUserName.trim();
    }

    public String getLoginPassword() {
        return login_password;
    }

    public void setLoginPassword(String loginPassword) {
        this.login_password = loginPassword == null ? null : loginPassword.trim();
    }

    public String getNickName() {
        return nick_name;
    }

    public void setNickName(String nickName) {
        this.nick_name = nickName == null ? null : nickName.trim();
    }

    public Byte getLocked() {
        return locked;
    }

    public void setLocked(Byte locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", adminUserId=").append(admin_user_id);
        sb.append(", loginUserName=").append(login_user_name);
        sb.append(", loginPassword=").append(login_password);
        sb.append(", nickName=").append(nick_name);
        sb.append(", locked=").append(locked);
        sb.append("]");
        return sb.toString();
    }
}