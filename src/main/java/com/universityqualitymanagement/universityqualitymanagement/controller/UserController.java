package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.models.User;
import com.universityqualitymanagement.universityqualitymanagement.models.Role;
import com.universityqualitymanagement.universityqualitymanagement.models.Faculty;
import com.universityqualitymanagement.universityqualitymanagement.models.Department;
import com.universityqualitymanagement.universityqualitymanagement.repositories.UserRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.RoleRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.FacultyRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.DepartmentRepository;

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
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

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
        String requestFacultyId = (String) request.get("facultyId");
        String requestDepartmentId = (String) request.get("departmentId");

        if (email == null || password == null || name == null || roleId == null)
            return ResponseEntity.badRequest().body("All fields are required (name, email, password, roleId).");

        if (userRepository.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body("Email already exists.");

        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty())
            return ResponseEntity.badRequest().body("Role not found.");

        Role selectedRole = roleOpt.get();

        // Validation for ADMIN, RECTOR, STAFF roles: These roles cannot be assigned to a faculty or department
        if ("ADMIN".equals(selectedRole.getName()) || "RECTOR".equals(selectedRole.getName()) || "STAFF".equals(selectedRole.getName())) {
            if (requestFacultyId != null && !requestFacultyId.isEmpty() || requestDepartmentId != null && !requestDepartmentId.isEmpty()) {
                return ResponseEntity.badRequest().body(selectedRole.getName() + " users cannot be assigned to a specific faculty or department.");
            }
            // For these roles, ensure faculty and department are explicitly null in the User object
            requestFacultyId = null; // Set to null before finding faculty/department
            requestDepartmentId = null; // Set to null before finding faculty/department
        }

        Faculty faculty = null;
        if (requestFacultyId != null && !requestFacultyId.isEmpty()) {
            final String fId = requestFacultyId;
            faculty = facultyRepository.findById(fId)
                    .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + fId));
        }

        Department department = null;
        if (requestDepartmentId != null && !requestDepartmentId.isEmpty()) {
            final String dId = requestDepartmentId;
            department = departmentRepository.findById(dId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + dId));
            // Ensure department belongs to the selected faculty if both are provided
            if (faculty != null && !department.getFaculty().getId().equals(faculty.getId())) {
                return ResponseEntity.badRequest().body("Department does not belong to the selected faculty.");
            }
        }

        User user = new User(email, password, name, selectedRole, faculty, department);
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
        String password = (String) request.get("password");
        String name = (String) request.get("name");
        String roleId = (String) request.get("roleId");
        String requestFacultyId = (String) request.get("facultyId");
        String requestDepartmentId = (String) request.get("departmentId");

        if (email != null && !user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent())
                return ResponseEntity.badRequest().body("Email already exists.");
            user.setEmail(email);
        }
        if (name != null)
            user.setName(name);
        if (password != null)
            user.setPassword(password); // IMPORTANT: Still plain text password. Hash it later!

        // Determine the effective role for validation
        Role effectiveRole = user.getRole();
        if (roleId != null) {
            Optional<Role> newRoleOpt = roleRepository.findById(roleId);
            if (newRoleOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Role not found.");
            }
            effectiveRole = newRoleOpt.get(); // Use the new role for validation
            user.setRole(effectiveRole); // Update user's role
        }

        // Validation for ADMIN, RECTOR, STAFF roles on update: These roles cannot have faculty/department
        if ("ADMIN".equals(effectiveRole.getName()) || "RECTOR".equals(effectiveRole.getName()) || "STAFF".equals(effectiveRole.getName())) {
            // If the request tries to assign faculty/department or if the user is changing TO ADMIN/RECTOR/STAFF
            if ((requestFacultyId != null && !requestFacultyId.isEmpty()) || (requestDepartmentId != null && !requestDepartmentId.isEmpty())) {
                return ResponseEntity.badRequest().body(effectiveRole.getName() + " users cannot be assigned to a specific faculty or department.");
            }
            // For these roles, ensure faculty and department are explicitly null
            user.setFaculty(null);
            user.setDepartment(null);
        } else {
            // For roles that can have faculty/department (e.g., STUDENT, DEAN), update based on request
            if (request.containsKey("facultyId")) { // Only update if facultyId is explicitly sent in request
                if (requestFacultyId == null || requestFacultyId.isEmpty()) {
                    user.setFaculty(null);
                } else {
                    final String fId = requestFacultyId;
                    Faculty faculty = facultyRepository.findById(fId)
                            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + fId));
                    user.setFaculty(faculty);
                }
            }

            if (request.containsKey("departmentId")) { // Only update if departmentId is explicitly sent in request
                if (requestDepartmentId == null || requestDepartmentId.isEmpty()) {
                    user.setDepartment(null);
                } else {
                    final String dId = requestDepartmentId;
                    Department department = departmentRepository.findById(dId)
                            .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + dId));
                    // Ensure department belongs to the selected faculty if both are provided
                    if (user.getFaculty() != null && !department.getFaculty().getId().equals(user.getFaculty().getId())) {
                        return ResponseEntity.badRequest().body("Department does not belong to the selected faculty.");
                    }
                    user.setDepartment(department);
                }
            }
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

        if (user.getFaculty() != null) {
            Map<String, Object> facultyMap = new HashMap<>();
            facultyMap.put("id", user.getFaculty().getId());
            facultyMap.put("name", user.getFaculty().getName());
            map.put("faculty", facultyMap);
        } else {
            map.put("faculty", null);
        }

        if (user.getDepartment() != null) {
            Map<String, Object> departmentMap = new HashMap<>();
            departmentMap.put("id", user.getDepartment().getId());
            departmentMap.put("name", user.getDepartment().getName());
            map.put("department", departmentMap);
        } else {
            map.put("department", null);
        }

        return map;
    }
}