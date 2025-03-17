package com.example.springapp.app.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class ChangePasswordForm {
    @NotNull
    private Long id;

    @NotBlank(message="Current Password must not be blank")
    private String currentPassword;

    @NotBlank(message="New Password must not be blank")
    private String newPassword;

    @NotBlank(message="Confirm Password must not be blank")
    private String confirmPassword;

    public ChangePasswordForm() { }
    public ChangePasswordForm(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChangePasswordForm that = (ChangePasswordForm) o;
        return Objects.equals(id, that.id) && Objects.equals(currentPassword, that.currentPassword) && Objects.equals(newPassword, that.newPassword) && Objects.equals(confirmPassword, that.confirmPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currentPassword, newPassword, confirmPassword);
    }

    @Override
    public String toString() {
        return "ChangePasswordForm{" +
                "id=" + id +
                ", currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
