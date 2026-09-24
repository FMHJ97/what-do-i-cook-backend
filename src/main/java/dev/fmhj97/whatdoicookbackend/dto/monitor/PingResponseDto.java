package dev.fmhj97.whatdoicookbackend.dto.monitor;

/**
 * Response DTO for the ping endpoint used by uptime monitors.
 * @param status the service status.
 */
public record PingResponseDto(String status) {
}