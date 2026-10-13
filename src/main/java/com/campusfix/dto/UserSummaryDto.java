package com.campusfix.dto;

import com.campusfix.model.Role;
import com.campusfix.model.User;

public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private String phone;
    private String avatarUrl;

    public UserSummaryDto() {}

    public UserSummaryDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.department = user.getDepartment();
            this.phone = user.getPhone();
            this.avatarUrl = user.getAvatarUrl();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
