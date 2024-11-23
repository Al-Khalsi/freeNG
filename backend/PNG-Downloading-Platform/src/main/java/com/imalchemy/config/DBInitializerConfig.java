package com.imalchemy.config;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.Roles;
import com.imalchemy.model.domain.User;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.repository.RolesRepository;
import com.imalchemy.repository.UserRepository;
import com.imalchemy.util.SlugGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DBInitializerConfig {

    @Bean
    public CommandLineRunner commandLineRunner(CategoryRepository categoryRepository, SlugGenerator slugGenerator,
                                               UserRepository userRepository, RolesRepository rolesRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultCategoryName = "defaultCategory";
            categoryRepository.findByNameIgnoreCase(defaultCategoryName)
                    .ifPresentOrElse(category -> category.setName(defaultCategoryName), () -> {
                        Category category = Category.builder()
                                .name(defaultCategoryName)
                                .description("default")
                                .iconUrl("default")
                                .isActive(true)
                                .displayOrder(0)
                                .level(0)
                                .parentCategory(null)
                                .subCategories(null)
                                .slug(slugGenerator.generateSlug(defaultCategoryName))
                                .build();
                        categoryRepository.save(category);
                    });

            Roles roleMaster = Roles.builder().roleName("ROLE_MASTER").build();
            rolesRepository.findByRoleName("ROLE_MASTER")
                    .ifPresentOrElse(Roles::getRoleName, () -> rolesRepository.save(roleMaster));

            String hashedPassword = passwordEncoder.encode("password");

            User seyed = new User();
            seyed.setUsername("Seyed Ali");
            seyed.setPassword(hashedPassword);
            seyed.setEmail("seyed.ali.devl@gmail.com");
            seyed.setRoles(Set.of(roleMaster));
            userRepository.findByEmail(seyed.getEmail())
                    .ifPresentOrElse(User::getEmail, () -> userRepository.save(seyed));

            User mmdHassan = new User();
            mmdHassan.setUsername("Mohammad Hassan Al-Khalsi");
            mmdHassan.setPassword(hashedPassword);
            mmdHassan.setEmail("mohammad.hassan.alkhalsi@gmail.com");
            mmdHassan.setRoles(Set.of(roleMaster));
            userRepository.findByEmail(mmdHassan.getEmail())
                    .ifPresentOrElse(User::getEmail, () -> userRepository.save(mmdHassan));
        };
    }

}
