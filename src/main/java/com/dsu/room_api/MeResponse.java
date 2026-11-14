package com.dsu.room_api;

import java.util.List;

public record MeResponse(String username, List<String> authorities) { }