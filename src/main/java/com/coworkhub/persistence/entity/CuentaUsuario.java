package com.coworkhub.persistence.entity;

import com.coworkhub.persistence.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity 
@Table (name = "cuentas_usuarios")

public class CuentaUsuario {
@Id 
@GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
private Long id;

@Column (nullable = false, length = 50, unique = true)
private String nombreUsuario;

@Column (nullable = false, length = 100)
private String contrasena;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private Rol rol;

@OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
@JoinColumn (name = "miembro_id", unique = true)
private Miembro miembro;
// Constructor
public CuentaUsuario() {
}
// getters y setters
public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}
public String getNombreUsuario() {
    return nombreUsuario;
}
public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
}
public String getContrasena() {
    return contrasena;
}
public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
}
public Rol getRol() {
    return rol;
}
public void setRol(Rol rol) {
    this.rol = rol;
}
public Miembro getMiembro() {
    return miembro;
}
public void setMiembro(Miembro miembro) {
    this.miembro = miembro;
}



}