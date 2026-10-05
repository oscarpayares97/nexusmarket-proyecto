package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;

public class ValidateOwnershipService {

    public void requireOwner(User actor, Long ownerUserId) {
        if (actor == null || actor.getId() == null || !actor.getId().equals(ownerUserId)) {
            throw new UnauthorizedOperationException("Solo el propietario puede realizar esta operación.");
        }
    }
}