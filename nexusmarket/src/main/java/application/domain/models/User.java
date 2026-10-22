package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.DocumentNumber;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

public class User {

    private Long id;
    private final DocumentNumber document;
    private final String fullName;
    private final Email email;
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
        this.document = new DocumentNumber(document);
        this.fullName = Guard.requireText(fullName, "El nombre completo");
        this.email = new Email(email);
        this.passwordHash = Guard.requireText(passwordHash, "La contraseña");
        Guard.requireNonNull(role, "ROL");
        Guard.requireNonNull(status, "El estado");
        this.role = role;
        this.status = status;
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
    public DocumentNumber getDocument() { return document; }
    public String getFullName() { return fullName; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
}