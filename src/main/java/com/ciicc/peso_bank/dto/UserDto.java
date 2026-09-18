package com.ciicc.peso_bank.dto;

public record UserDto(Long userId, String username, String userStatus, UserProfileDto profile) {}
