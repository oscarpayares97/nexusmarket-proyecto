package application.domain.valueobjects;

import application.domain.exceptions.InvalidDataException;

import java.math.BigDecimal;

public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null) {
            throw new InvalidDataException("Es obligatorio indicar el importe.");
        }
        if (amount.signum() < 0) {
            throw new InvalidDataException("La cantidad no puede ser negativa.");
        }
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money multiply(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public boolean isGreaterThanZero() {
        return amount.signum() > 0;
    }
}