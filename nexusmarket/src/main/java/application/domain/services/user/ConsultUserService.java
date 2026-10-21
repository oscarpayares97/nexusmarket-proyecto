package application.domain.services.user;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultUserService {

    private final UserRepositoryPort userRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultUserService(UserRepositoryPort userRepository,
                              ValidateRoleAuthorizationService authorization) {
        this.userRepository = userRepository;
        this.authorization = authorization;
    }

    public List<User> consultUsers(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return userRepository.findAll();
    }
}