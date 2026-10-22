package application.domain.ports.out;

public interface PasswordServicePort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
