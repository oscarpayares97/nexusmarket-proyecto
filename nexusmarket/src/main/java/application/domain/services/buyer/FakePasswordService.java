package application.domain.services.buyer;

import application.domain.ports.out.PasswordServicePort;

class FakePasswordService implements PasswordServicePort {

    @Override
    public String encode(String rawPassword) {
        return "hashed:" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}