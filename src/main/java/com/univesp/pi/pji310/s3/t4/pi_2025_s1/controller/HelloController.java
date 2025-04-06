package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Autenticação - Validação")
public class HelloController {

    @GetMapping({"/", "/hello"})
    public ResponseEntity<String> hello() {

        return ResponseEntity.ok("Univesp - PI 2025 S1 - T4 S3");
    }
}
