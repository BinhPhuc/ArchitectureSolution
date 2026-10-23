package com.architecture.solution.dto.auth.request;

import com.architecture.solution.annotation.ValueOfEnum;
import com.architecture.solution.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @JsonProperty("retype_password")
    private String retypePassword;

    @Pattern(regexp = "^\\d{9,15}$", message = "Phone must be 9-15 digits")
    private String phone;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @NotNull
    @ValueOfEnum(enumClass = RoleName.class, message = "Role must be one of: CUSTOMER, SELLER")
    @JsonProperty("role")
    private String role;
}
