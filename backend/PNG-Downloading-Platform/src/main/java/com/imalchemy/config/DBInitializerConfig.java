package com.imalchemy.config;

import com.imalchemy.model.domain.Category;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.util.SlugGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBInitializerConfig {

    @Bean
    public CommandLineRunner commandLineRunner(CategoryRepository categoryRepository, SlugGenerator slugGenerator) {
        return args -> {
            String defaultCategoryName = "defaultCategory";
            categoryRepository.findByName(defaultCategoryName)
                    .ifPresentOrElse(category -> category.setName(defaultCategoryName), () -> {
                        Category category = Category.builder()
                                .name(defaultCategoryName)
                                .description("default")
                                .iconUrl("default")
                                .isActive(true)
                                .displayOrder(0)
                                .level(0)
                                .parent(null)
                                .subCategories(null)
                                .slug(slugGenerator.generateSlug(defaultCategoryName))
                                .build();
                        categoryRepository.save(category);
                    });
        };
    }

}
