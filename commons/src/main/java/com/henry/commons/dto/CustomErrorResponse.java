package com.henry.commons.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
