//package com.tphone.config;
//
//import com.tphone.entity.Account;
//import com.tphone.entity.Brand;
//import com.tphone.entity.Category;
//import com.tphone.entity.Product;
//import com.tphone.entity.Role;
//import com.tphone.enums.AccountProvider;
//import com.tphone.enums.AccountStatus;
//import com.tphone.enums.ProductStatus;
//import com.tphone.repository.AccountRepository;
//import com.tphone.repository.BrandRepository;
//import com.tphone.repository.CategoryRepository;
//import com.tphone.repository.ProductRepository;
//import com.tphone.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.HashSet;
//
//@Component
//@RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//    private final AccountRepository accountRepository;
//    private final CategoryRepository categoryRepository;
//    private final BrandRepository brandRepository;
//    private final ProductRepository productRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//        seedRoles();
//        seedAdminAccount();
//        seedCategories();
//        seedBrands();
//        seedProducts();
//    }
//
//    private void seedRoles() {
//        if (roleRepository.findByCode("ROLE_ADMIN").isEmpty()) {
//            Role adminRole = new Role();
//            adminRole.setCode("ROLE_ADMIN");
//            adminRole.setName("Administrator");
//            adminRole.setDescription("System administrator");
//            roleRepository.save(adminRole);
//        }
//
//        if (roleRepository.findByCode("ROLE_USER").isEmpty()) {
//            Role userRole = new Role();
//            userRole.setCode("ROLE_USER");
//            userRole.setName("User");
//            userRole.setDescription("Normal customer");
//            roleRepository.save(userRole);
//        }
//    }
//
//    private void seedAdminAccount() {
//        if (accountRepository.findByEmail("admin@tphone.com").isPresent()) {
//            return;
//        }
//
//        Role adminRole = roleRepository.findByCode("ROLE_ADMIN")
//                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
//
//        Account admin = new Account();
//        admin.setFullName("System Admin");
//        admin.setEmail("admin@tphone.com");
//        admin.setPhone("0900000000");
//        admin.setPasswordHash(passwordEncoder.encode("123456"));
//        admin.setProvider(AccountProvider.LOCAL);
//        admin.setStatus(AccountStatus.ACTIVE);
//
//        admin.setRoles(new HashSet<>());
//        admin.getRoles().add(adminRole);
//
//        accountRepository.save(admin);
//    }
//
//    private void seedCategories() {
//        if (categoryRepository.findAllByDeletedAtIsNull().size() > 0) {
//            return;
//        }
//
//        Category iphone = new Category();
//        iphone.setName("iPhone");
//        iphone.setSlug("iphone");
//        iphone.setDescription("Apple iPhone");
//        iphone.setIsActive(true);
//        iphone.setSortOrder(1);
//        categoryRepository.save(iphone);
//
//        Category samsung = new Category();
//        samsung.setName("Samsung");
//        samsung.setSlug("samsung");
//        samsung.setDescription("Samsung Galaxy");
//        samsung.setIsActive(true);
//        samsung.setSortOrder(2);
//        categoryRepository.save(samsung);
//
//        Category xiaomi = new Category();
//        xiaomi.setName("Xiaomi");
//        xiaomi.setSlug("xiaomi");
//        xiaomi.setDescription("Xiaomi phones");
//        xiaomi.setIsActive(true);
//        xiaomi.setSortOrder(3);
//        categoryRepository.save(xiaomi);
//    }
//
//    private void seedBrands() {
//        if (brandRepository.findAllByDeletedAtIsNull().size() > 0) {
//            return;
//        }
//
//        Brand apple = new Brand();
//        apple.setName("Apple");
//        apple.setSlug("apple");
//        apple.setDescription("Apple brand");
//        apple.setLogoUrl("/uploads/images/apple-logo.png");
//        apple.setIsActive(true);
//        apple.setSortOrder(1);
//        brandRepository.save(apple);
//
//        Brand samsung = new Brand();
//        samsung.setName("Samsung");
//        samsung.setSlug("samsung-brand");
//        samsung.setDescription("Samsung brand");
//        samsung.setLogoUrl("/uploads/images/samsung-logo.png");
//        samsung.setIsActive(true);
//        samsung.setSortOrder(2);
//        brandRepository.save(samsung);
//
//        Brand xiaomi = new Brand();
//        xiaomi.setName("Xiaomi");
//        xiaomi.setSlug("xiaomi-brand");
//        xiaomi.setDescription("Xiaomi brand");
//        xiaomi.setLogoUrl("/uploads/images/xiaomi-logo.png");
//        xiaomi.setIsActive(true);
//        xiaomi.setSortOrder(3);
//        brandRepository.save(xiaomi);
//    }
//
//    private void seedProducts() {
//        if (productRepository.findAllByDeletedAtIsNull().size() > 0) {
//            return;
//        }
//
//        Category iphoneCategory = categoryRepository.findBySlug("iphone")
//                .orElseThrow(() -> new RuntimeException("iphone category not found"));
//
//        Category samsungCategory = categoryRepository.findBySlug("samsung")
//                .orElseThrow(() -> new RuntimeException("samsung category not found"));
//
//        Brand appleBrand = brandRepository.findBySlug("apple")
//                .orElseThrow(() -> new RuntimeException("apple brand not found"));
//
//        Brand samsungBrand = brandRepository.findBySlug("samsung-brand")
//                .orElseThrow(() -> new RuntimeException("samsung brand not found"));
//
//        Product p1 = new Product();
//        p1.setCategory(iphoneCategory);
//        p1.setBrand(appleBrand);
//        p1.setName("iPhone 15");
//        p1.setSlug("iphone-15");
//        p1.setSku("IP15-001");
//        p1.setShortDescription("iPhone 15 128GB");
//        p1.setDescription("Apple iPhone 15 chính hãng, hiệu năng mạnh, camera đẹp.");
//        p1.setPrice(new BigDecimal("19990000"));
//        p1.setCompareAtPrice(new BigDecimal("21990000"));
//        p1.setStockQuantity(20);
//        p1.setMinStockAlert(5);
//        p1.setThumbnailUrl("/uploads/images/sample-iphone15.jpg");
//        p1.setWeightGrams(171);
//        p1.setStatus(ProductStatus.ACTIVE);
//        productRepository.save(p1);
//
//        Product p2 = new Product();
//        p2.setCategory(samsungCategory);
//        p2.setBrand(samsungBrand);
//        p2.setName("Samsung Galaxy S24");
//        p2.setSlug("samsung-galaxy-s24");
//        p2.setSku("SS24-001");
//        p2.setShortDescription("Samsung Galaxy S24 256GB");
//        p2.setDescription("Samsung Galaxy S24 với màn hình đẹp và hiệu năng ổn định.");
//        p2.setPrice(new BigDecimal("18490000"));
//        p2.setCompareAtPrice(new BigDecimal("19990000"));
//        p2.setStockQuantity(15);
//        p2.setMinStockAlert(5);
//        p2.setThumbnailUrl("/uploads/images/sample-s24.jpg");
//        p2.setWeightGrams(168);
//        p2.setStatus(ProductStatus.ACTIVE);
//        productRepository.save(p2);
//    }
//}