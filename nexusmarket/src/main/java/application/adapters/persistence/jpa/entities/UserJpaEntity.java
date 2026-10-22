package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA persistence entity (repository DTO): System users (one role per user).
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "identification", length = 20, nullable = false, unique = true)
    private String identification;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "role", length = 30, nullable = false)
    private String role;

    @Column(name = "status", length = 15, nullable = false)
    private String status;

    @Column(name = "auth_token_version", nullable = false)
    private Integer authTokenVersion;

    @Column(name = "buyer_identification", length = 20)
    private String buyerIdentification;

    @Column(name = "seller_identification", length = 20)
    private String sellerIdentification;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAuthTokenVersion() {
        return authTokenVersion;
    }

    public void setAuthTokenVersion(Integer authTokenVersion) {
        this.authTokenVersion = authTokenVersion;
    }

    public String getBuyerIdentification() {
        return buyerIdentification;
    }

    public void setBuyerIdentification(String buyerIdentification) {
        this.buyerIdentification = buyerIdentification;
    }

    public String getSellerIdentification() {
        return sellerIdentification;
    }

    public void setSellerIdentification(String sellerIdentification) {
        this.sellerIdentification = sellerIdentification;
    }
}
