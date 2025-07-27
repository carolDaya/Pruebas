package com.example.login.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(tableName = "users", // Nombre de la tabla en la DB
        indices = {@Index(value = {"phone_number"}, unique = true)}) // Asegura que phone_number sea único
public class User {
    @PrimaryKey(autoGenerate = true) // ID autoincremental
    public int uid; // Puedes usar un int o String para el ID

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phone_number") // Nombre de la columna en la DB
    public String phoneNumber;

    @ColumnInfo(name = "password_hash") // Guardaremos el hash de la contraseña, NO el texto plano
    public String passwordHash;

    // Constructor para Room (Room lo usa para reconstruir objetos)
    public User(String name, String phoneNumber, String passwordHash) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
    }

    // Getters (necesarios para que Room acceda a los datos)
    public int getUid() { return uid; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPasswordHash() { return passwordHash; }

    // Setters (pueden ser útiles, pero no estrictamente necesarios para Room si el constructor es suficiente)
    public void setUid(int uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
