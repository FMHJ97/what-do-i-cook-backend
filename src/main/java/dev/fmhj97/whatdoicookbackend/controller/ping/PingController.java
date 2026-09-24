package dev.fmhj97.whatdoicookbackend.controller.ping;

import dev.fmhj97.whatdoicookbackend.dto.monitor.PingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
@Tag(name = "Monitor", description = "Endpoints used by uptime monitors")
public class PingController {

    /**
     * Liveness check used by uptime monitors. Returns 200 OK with a JSON body.
     * @return the service status.
     */
    @GetMapping
    @Operation(summary = "Liveness check used by uptime monitors")
    public ResponseEntity<PingResponseDto> ping() {
        return ResponseEntity.ok(new PingResponseDto("ok"));
    }
}