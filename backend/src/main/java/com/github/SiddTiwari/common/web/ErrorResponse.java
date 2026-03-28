package com.github.SiddTiwari.common.web;

import java.time.OffsetDateTime;

public record ErrorResponse(String message, int status, OffsetDateTime timestamp) {}
