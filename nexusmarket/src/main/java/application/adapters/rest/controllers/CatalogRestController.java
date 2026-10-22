package application.adapters.rest.controllers;

import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.mappers.CatalogRestMapper;
import application.domain.ports.in.PublicAccessPort;
import application.domain.valueobjects.ProductCategory;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public catalog: only PUBLISHED products are visible.
 */
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogRestController {

    private final PublicAccessPort publicAccessPort;

    public CatalogRestController(PublicAccessPort publicAccessPort) {
        this.publicAccessPort = publicAccessPort;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> consultCatalog(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(publicAccessPort.consultCatalog(ProductCategory.fromCode(category)).stream()
                .map(CatalogRestMapper::toResponse).toList());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> consultProduct(@PathVariable String productId) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(
                publicAccessPort.consultProduct(CatalogRestMapper.reference(productId))));
    }
}
