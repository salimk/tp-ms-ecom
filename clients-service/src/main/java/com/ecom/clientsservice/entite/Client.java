package com.ecom.clientsservice.entite;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
public class  Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

     private String name;
     private String email;
     private int etat;

    public Client() {
    }


    public Client(Long id, String name, String email, int etat) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.etat = etat;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}