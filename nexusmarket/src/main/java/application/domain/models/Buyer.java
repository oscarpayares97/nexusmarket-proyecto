package application.domain.models;

import application.domain.valueobjects.CommercialStatus;

import java.util.ArrayList;
import java.util.List;

public class Buyer {

    private final Long userId;
    private String primaryAddress;
    private final List<String> additionalAddresses = new ArrayList<>();
    private CommercialStatus commercialStatus;

    public Buyer(Long userId, String primaryAddress) {
        this(userId, primaryAddress, new ArrayList<>(), CommercialStatus.ENABLED);
    }

    public Buyer(Long userId, String primaryAddress, List<String> additionalAddresses,
                 CommercialStatus commercialStatus) {
        Guard.requireNonNull(userId, "El usuario");
        Guard.requireNonNull(commercialStatus, "La situación comercial");
        this.userId = userId;
        this.primaryAddress = Guard.requireText(primaryAddress, "La primera direccion");
        if (additionalAddresses != null) {
            this.additionalAddresses.addAll(additionalAddresses);
        }
        this.commercialStatus = commercialStatus;
    }

    public void addAddress(String address) {
        additionalAddresses.add(Guard.requireText(address, "La direccion"));
    }

    public boolean hasAddress(String address) {
        return address != null
                && (address.trim().equals(primaryAddress) || additionalAddresses.contains(address.trim()));
    }

    public void enable() { this.commercialStatus = CommercialStatus.ENABLED; }
    public void suspend() { this.commercialStatus = CommercialStatus.SUSPENDED; }
    public boolean canPurchase() { return commercialStatus == CommercialStatus.ENABLED; }

    public Long getUserId() { return userId; }
    public String getPrimaryAddress() { return primaryAddress; }
    public CommercialStatus getCommercialStatus() { return commercialStatus; }

    public List<String> getAdditionalAddresses() {
        return new ArrayList<>(additionalAddresses);
    }
}