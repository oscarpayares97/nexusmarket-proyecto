package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;

public class ValidateRoleAuthorizationService {

    // RG-01 y RG-03: el usuario debe existir, estar activo y tener un rol permitido
    public void requireRole(User actor, UserRole... allowedRoles) {
        if (actor == null) {
            throw new UnauthorizedOperationException("Es necesario ser un usuario autenticado.");
        }
        if (!actor.isActive()) {
            throw new UnauthorizedOperationException("El usuario no esta activo.");
        }
        for (UserRole role : allowedRoles) {
            if (actor.getRole() == role) {
                return;
            }
        }
        throw new UnauthorizedOperationException(
                "EL papel " + actor.getRole() + " No tiene permiso para realizar esta operación.");
    }
}