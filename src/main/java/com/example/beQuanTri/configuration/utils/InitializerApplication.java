package com.example.beQuanTri.configuration.utils;


import com.example.beQuanTri.constant.PredefineStatus;
import com.example.beQuanTri.constant.PredefinedCategory;
import com.example.beQuanTri.constant.PredefinedEmail;
import com.example.beQuanTri.constant.PredefinedRole;
import com.example.beQuanTri.entity.category.Category;
import com.example.beQuanTri.entity.role.Role;
import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.repository.category.CategoryRepository;
import com.example.beQuanTri.repository.role.RoleRepository;
import com.example.beQuanTri.repository.status.StatusRepository;
import com.example.beQuanTri.repository.user.UserRepository;
import com.example.beQuanTri.service.role.RoleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.Optional;


@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Component
public class InitializerApplication implements CommandLineRunner {


    @Autowired
    UserRepository userRepository;


    @Autowired
    RoleRepository roleRepository;


    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    StatusRepository statusRepository;


    @Autowired
    CategoryRepository categoryRepository;


    @Autowired
    RoleService roleService;


    @Override
    public void run(String... arg) {


        createRole(PredefinedRole.ADMIN_ROLE);
        createRole(PredefinedRole.USER_ROLE);
        createRole(PredefinedRole.COLLABORATOR_ROLE);


        // Kiem tra xem da co tai khoan Admin hay chua
        Optional<User> adminUser = userRepository.findByUsername("admin");


        if (adminUser.isEmpty()) {
            // Create Admin Role if Not Exists
            Role adminRole = roleService.getRoleByName(PredefinedRole.ADMIN_ROLE);


            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setEmail(PredefinedEmail.ADMIN_EMAIL);
            admin.setPhoneNumber("0123456789");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setBirthDate(LocalDate.of(2004, 3, 28));


            userRepository.save(admin);
            log.info("Admin account created: admin/admin123");
        } else {
            System.out.println("Admin account already exists");
        }


        createStatus(PredefineStatus.OPEN);
        createStatus(PredefineStatus.IN_PROGRESS);
        createStatus(PredefineStatus.COMPLETE);


        createCategory(PredefinedCategory.MICROSOFT);
        createCategory(PredefinedCategory.NETFLIX);
        createCategory(PredefinedCategory.GOOGLE_DRIVE);
        createCategory(PredefinedCategory.COURSERA);
        createCategory(PredefinedCategory.YOUTUBE);
    }


    // Create Role if Not Exists
    void createRole(String roleName) {
        // Kiem tra xem da co Role hay chua
        Optional<Role> roleCheck = roleRepository.findByRoleName(roleName);
        if (roleCheck.isEmpty()) {
            Role newRole = new Role();
            newRole.setRoleName(roleName);
            roleRepository.save(newRole);
            log.info("{} role created", roleName);
        } else {
            log.info("{} role already exists", roleName);
        }
    }


    // Create Status if Not Exists
    void createStatus(String statusName) {
        //Kiem tra xem da co status hay chua
        Optional<Status> statusCheck = statusRepository.findByStatusName(statusName);
        if (statusCheck.isEmpty()) {
            Status newStatus = new Status();
            newStatus.setStatusName(statusName);
            statusRepository.save(newStatus);
            log.info("{} status created", statusName);
        } else {
            log.info("{} status already exists", statusName);
        }
    }


    // Create Category if Not Exists
    void createCategory(String categoryName) {
        //Kiem tra xem da co category hay chua
        Optional<Category> categoryCheck = categoryRepository.findByName(categoryName);
        if (categoryCheck.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(categoryName);
            categoryRepository.save(newCategory);
            log.info("{} category created", categoryName);
        } else {
            log.info("{} category already exists", categoryName);
        }
    }
}
