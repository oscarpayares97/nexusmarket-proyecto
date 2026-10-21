package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

public class User {

    private Long id;
    private final String document;
    private final String fullName;
    private final String email;
    private String passwordHash;
    private final UserRole role;          // un único rol por usuario (RG-02)
    private UserStatus status;

    // Para crear un usuario nuevo (inicia ACTIVE)
    public User(Long id, String document, String fullName, String email,
                String passwordHash, UserRole role) {
        this(id, document, fullName, email, passwordHash, role, UserStatus.ACTIVE);
    }

    // Para reconstruir un usuario guardado (conserva su estado)
    public User(Long id, String document, String fullName, String email,
                String passwordHash, UserRole role, UserStatus status) {
        this.id = id;
        this.document = Guard.requireText(document, "El documento");
        this.fullName = Guard.requireText(fullName, "El nombre completo");
        this.email = validateEmail(email);
        this.passwordHash = Guard.requireText(passwordHash, "La contraseña");
        Guard.requireNonNull(role, "The role");
        Guard.requireNonNull(status, "The status");
        this.role = role;
        this.status = status;
    }

    private static String validateEmail(String email) {
        String value = Guard.requireText(email, "El email").toLowerCase();
        int at = value.indexOf('@');
        if (at < 1 || value.indexOf('.', at) < 0) {
            throw new InvalidDataException("El correo electrónico no tiene un formato válido.");
        }
        return value;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El usuario ya tiene un ID.");
        }
        this.id = id;
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = Guard.requireText(newPasswordHash, "La contraseña");
    }

    public void block() { this.status = UserStatus.BLOCKED; }
    public void deactivate() { this.status = UserStatus.INACTIVE; }
    public void activate() { this.status = UserStatus.ACTIVE; }
    public boolean isActive() { return status == UserStatus.ACTIVE; }

    public Long getId() { return id; }
    public String getDocument() { return document; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
}