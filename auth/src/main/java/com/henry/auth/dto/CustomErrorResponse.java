package com.henry.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
