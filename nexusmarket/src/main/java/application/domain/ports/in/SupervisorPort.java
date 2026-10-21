package application.domain.ports.in;

import application.domain.models.User;

import java.util.List;

public interface SupervisorPort {

    List<User> consultUsers(User actor);
}