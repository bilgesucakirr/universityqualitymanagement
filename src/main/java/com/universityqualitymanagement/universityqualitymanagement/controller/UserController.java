package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.models.User;
import com.universityqualitymanagement.universityqualitymanagement.models.Role;
import com.universityqualitymanagement.universityqualitymanagement.repositories.UserRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> userList = new ArrayList<>();
        for (User user : users) {
            userList.add(maskUser(user));
        }
        return userList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(maskUser(userOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String name = (String) request.get("name");
        String roleId = (String) request.get("roleId");

        if (email == null || password == null || name == null || roleId == null)
            return ResponseEntity.badRequest().body("All fields are required.");

        if (userRepository.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body("Email already exists.");

        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty())
            return ResponseEntity.badRequest().body("Role not found.");

        User user = new User(email, password, name, roleOpt.get());
        User saved = userRepository.save(user);
        return ResponseEntity.ok(maskUser(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, Object> request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();

        User user = userOpt.get();

        String email = (String) request.get("email");
        String password = (String) request.get("password"); // optional
        String name = (String) request.get("name");
        String roleId = (String) request.get("roleId");

        if (email != null && !user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent())
                return ResponseEntity.badRequest().body("Email already exists.");
            user.setEmail(email);
        }
        if (name != null)
            user.setName(name);
        if (password != null)
            user.setPassword(password);
        if (roleId != null) {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            roleOpt.ifPresent(user::setRole);
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(maskUser(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (!userRepository.existsById(id))
            return ResponseEntity.notFound().build();

        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> maskUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        map.put("name", user.getName());
        if (user.getRole() != null) {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("id", user.getRole().getId());
            roleMap.put("name", user.getRole().getName());
            map.put("role", roleMap);
        }
        return map;
    }
}
