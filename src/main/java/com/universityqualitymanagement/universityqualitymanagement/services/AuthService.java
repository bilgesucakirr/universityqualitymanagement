package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.request.LoginRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.request.RegisterRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.response.LoginResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.response.UserResponse;
import com.universityqualitymanagement.universityqualitymanagement.models.Role;
import com.universityqualitymanagement.universityqualitymanagement.models.User;
import com.universityqualitymanagement.universityqualitymanagement.repositories.RoleRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.UserRepository;
import com.universityqualitymanagement.universityqualitymanagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(RoleRepository roleRepository,UserRepository userRepository, JwtUtil jwtUtil) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {
        Role defaultUserRole = roleRepository.findByName("STUDENT") // Assuming default registration is for STUDENT
                .orElseThrow(() -> new RuntimeException("Default user role (STUDENT) not found."));

        // Use the new constructor with null for faculty and department
        User user = new User(request.getEmail(), request.getPassword(), request.getName(), defaultUserRole, null, null);
        userRepository.save(user);

        // Populate UserResponse with null for faculty/department as they are not set during registration
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), null, null, null, null);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole().getName());
        return new LoginResponse(token);
    }

    public UserResponse getCurrUser(String token) {
        if(!jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String userId = jwtUtil.extractId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String facultyId = user.getFaculty() != null ? user.getFaculty().getId() : null;
        String facultyName = user.getFaculty() != null ? user.getFaculty().getName() : null;
        String departmentId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        String departmentName = user.getDepartment() != null ? user.getDepartment().getName() : null;

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), facultyId, facultyName, departmentId, departmentName);
    }

}