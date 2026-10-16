package com.gestionmedica.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController { @GetMapping("/actuator/health") public Map<String,String> health() { return Map.of("status","UP"); } }
