package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // 'user' reserved keyword olabilir, 'users' daha güvenli
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY) // Bir kullanıcının ait olduğu fakülte
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY) // Bir kullanıcının ait olduğu bölüm
    @JoinColumn(name = "department_id")
    private Department department;

    public User() {
    }

    // Existing constructor (update if needed, or add new ones)
    public User(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // New constructor including faculty and department
    public User(String email, String password, String name, Role role, Faculty faculty, Department department) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.faculty = faculty;
        this.department = department;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}