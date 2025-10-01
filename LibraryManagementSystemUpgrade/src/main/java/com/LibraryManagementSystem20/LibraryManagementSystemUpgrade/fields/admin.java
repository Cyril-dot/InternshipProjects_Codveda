package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;                     // ✅ use Spring Data Mongo
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "admins")
public class admin {                                            // ✅ capitalized

    @Id
    private String adminId;

    private String adminName;
    private String adminEmail;
    private String adminPassword;
    private String role;

    public void setRole(String role) {
        this.role = "ADMIN";
    }
}
