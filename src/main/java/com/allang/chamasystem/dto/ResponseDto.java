package com.allang.chamasystem.dto;

public record ResponseDto(
        String message,
        Object data,
        Boolean status,
        int statusCode
) {
}
