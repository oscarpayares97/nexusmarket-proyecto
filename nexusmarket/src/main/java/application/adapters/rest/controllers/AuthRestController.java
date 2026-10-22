package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.mappers.BuyerRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.ports.in.PublicAccessPort;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final PublicAccessPort publicAccessPort;
    private final long expirationMs;

    public AuthRestController(PublicAccessPort publicAccessPort,
                              @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.publicAccessPort = publicAccessPort;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthenticationResult result = publicAccessPort.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(UserRestMapper.toLoginResponse(result, expirationMs / 1000));
    }

    @PostMapping("/register/buyer")
    public ResponseEntity<BuyerResponseDTO> registerBuyer(@Valid @RequestBody RegisterBuyerRequestDTO request) {
        Buyer buyer = publicAccessPort.registerBuyer(BuyerRestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(BuyerRestMapper.toResponseDTO(buyer));
    }
}