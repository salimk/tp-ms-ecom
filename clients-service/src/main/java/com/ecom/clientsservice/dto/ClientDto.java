package com.ecom.clientsservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link com.ecom.clientsservice.entite.Client}
 */
public class ClientDto implements Serializable {
    private final Long id;
    @NotBlank
    @Size(max = 255)
    private final String name;
    @NotBlank
    @Email
    @Size(max = 255)
    private final String email;
    @Min(0)
    @Max(2)
    @NotNull
    private final Integer etat;


    @JsonCreator
    public ClientDto(@JsonProperty("id") Long id, @JsonProperty("name") String name,
                     @JsonProperty("email") String email, @JsonProperty("etat") Integer etat) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.etat = etat;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDto entity = (ClientDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.etat, entity.etat) &&
                Objects.equals(this.email, entity.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email,etat);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "etat = " + etat + ", " +
                "email = " + email + ")";
    }

    public Integer getEtat() {
        return etat;
    }
}
