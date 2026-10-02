package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private Long id;
    private final Long sellerId;
    private final String name;
    private final ProductType type;
    private final Money price;
    private final List<String> variants = new ArrayList<>();
    private ProductStatus status;

    // Un producto nuevo nace SUSPENDED (no visible) hasta que el vendedor lo publica
    public Product(Long id, Long sellerId, String name, ProductType type, Money price) {
        this(id, sellerId, name, type, price, new ArrayList<>(), ProductStatus.SUSPENDED);
    }

    public Product(Long id, Long sellerId, String name, ProductType type, Money price,
                   List<String> variants, ProductStatus status) {
        Guard.requireNonNull(sellerId, "Vendedor");
        Guard.requireNonNull(type, "Tipo de producto");
        Guard.requireNonNull(price, "Precio");
        Guard.requireNonNull(status, "Estado");
        if (!price.isGreaterThanZero()) {
            throw new InvalidDataException("El precio debe ser mayor que cero.");
        }
        this.id = id;
        this.sellerId = sellerId;
        this.name = Guard.requireText(name, "El nombre el producto");
        this.type = type;
        this.price = price;
        if (variants != null) {
            this.variants.addAll(variants);
        }
        this.status = status;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El producto ya tiene un identificador.");
        }
        this.id = id;
    }

    public void addVariant(String variant) {
        String value = Guard.requireText(variant, "La variante");
        if (variants.contains(value)) {
            throw new InvalidDataException("Esta variante ya está disponible en este producto.");
        }
        variants.add(value);
    }

    public void publish() {
        requireNotDiscontinued("Publicado");
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        requireNotDiscontinued("suspendido");
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    private void requireNotDiscontinued(String action) {
        if (status == ProductStatus.DISCONTINUED) {
            throw new InvalidStatusTransitionException("Un producto descatalogado no puede ser " + action + "de nuevo.");
        }
    }

    public boolean isPublished() { return status == ProductStatus.PUBLISHED; }
    public boolean isPhysical() { return type == ProductType.PHYSICAL; }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public String getName() { return name; }
    public ProductType getType() { return type; }
    public Money getPrice() { return price; }
    public ProductStatus getStatus() { return status; }

    public List<String> getVariants() {
        return new ArrayList<>(variants);
    }
}