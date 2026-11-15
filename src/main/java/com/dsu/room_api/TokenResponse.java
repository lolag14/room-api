package com.dsu.room_api;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) { }