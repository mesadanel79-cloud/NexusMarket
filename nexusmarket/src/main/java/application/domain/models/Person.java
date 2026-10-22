package application.domain.models;

import application.domain.exceptions.InvalidPersonDataException;
import java.util.regex.Pattern;

/**
 * Abstract identifiable person of the marketplace (buyers, sellers and system users).
 *
 * <p>Centralizes identity and contact data and the validation rules shared by all
 * participants: mandatory identification, name, email, phone and address; email must
 * contain '@' and a domain; phone must have between 7 and 15 digits.
 */
public abstract class Person {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{7,15}$");

    private String identification;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    /**
     * Validates the identity and contact invariants of the person.
     */
    public void validateContactData() {
        requireText(identification, "identification", 20);
        requireText(name, "name", 100);
        requireText(email, "email", 100);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidPersonDataException("Email must contain '@' and a valid domain.");
        }
        requireText(phoneNumber, "phoneNumber", 15);
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidPersonDataException("Phone number must contain between 7 and 15 digits.");
        }
        requireText(address, "address", 200);
    }

    /**
     * Copies the mutable contact data (name, email, phone, address) from the desired
     * state. The identification is an identity attribute and cannot change.
     */
    protected void copyContactDataFrom(Person desired) {
        if (desired == null) {
            throw new InvalidPersonDataException("Desired contact data must be provided.");
        }
        if (desired.getIdentification() != null && !desired.getIdentification().equals(identification)) {
            throw new InvalidPersonDataException("Identification cannot be changed.");
        }
        String previousName = name;
        String previousEmail = email;
        String previousPhone = phoneNumber;
        String previousAddress = address;
        name = desired.getName();
        email = desired.getEmail();
        phoneNumber = desired.getPhoneNumber();
        address = desired.getAddress();
        try {
            validateContactData();
        } catch (InvalidPersonDataException ex) {
            name = previousName;
            email = previousEmail;
            phoneNumber = previousPhone;
            address = previousAddress;
            throw ex;
        }
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidPersonDataException("Field '" + field + "' is required.");
        }
        if (value.length() > maxLength) {
            throw new InvalidPersonDataException(
                    "Field '" + field + "' must not exceed " + maxLength + " characters.");
        }
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
}
