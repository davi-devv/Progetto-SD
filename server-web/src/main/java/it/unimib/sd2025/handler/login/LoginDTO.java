package it.unimib.sd2025.handler.login;

public class LoginDTO {
    private final String name;
    private final String surname;
    private final String email;

    public LoginDTO(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }
    public static LoginDTO build(String[] parsing) {
        if (parsing.length != 3) throw new RuntimeException("LoginDTO::build : Invalid number of parameters");
        return new LoginDTO(parsing[0], parsing[1], parsing[2]);
    }
}
