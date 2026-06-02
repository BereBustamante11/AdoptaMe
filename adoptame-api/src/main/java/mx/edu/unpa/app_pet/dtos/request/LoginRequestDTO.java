package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
