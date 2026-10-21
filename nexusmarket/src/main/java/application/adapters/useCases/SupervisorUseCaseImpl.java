package application.adapters.useCases;

import application.domain.models.User;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.user.ConsultUserService;

import java.util.List;

public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ConsultUserService consultUserService;

    public SupervisorUseCaseImpl(ConsultUserService consultUserService) {
        this.consultUserService = consultUserService;
    }

    @Override
    public List<User> consultUsers(User actor) {
        return consultUserService.consultUsers(actor);
    }
}