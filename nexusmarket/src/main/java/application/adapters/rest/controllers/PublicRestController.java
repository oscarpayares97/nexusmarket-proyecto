package application.adapters.rest.controllers;

import application.adapters.rest.dtos.responses.ResponseDTOs.ProductResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.ports.in.PublicAccessPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints sin sesión (catálogo visible para cualquier visitante). */
@RestController
@RequestMapping("/api/v1/public")
public class PublicRestController {

    private final PublicAccessPort publicAccessPort;

    public PublicRestController(PublicAccessPort publicAccessPort) {
        this.publicAccessPort = publicAccessPort;
    }

    @GetMapping("/products")
    public List<ProductResponse> consultPublishedProducts() {
        return DomainRestMapper.mapList(publicAccessPort.consultPublishedProducts(),
                DomainRestMapper::toProduct);
    }
}