package com.tphone.config;

import com.tphone.entity.Account;
import com.tphone.entity.Role;
import com.tphone.enums.AccountProvider;
import com.tphone.enums.AccountStatus;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String LOGIN_EMAIL = "giacuong200@gmail.com";
    private static final String LOGIN_PASSWORD = "password";

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role userRole = ensureUserRole();
        ensureLoginAccount(userRole);
    }

    private Role ensureUserRole() {
        return roleRepository.findByCode("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setCode("ROLE_USER");
                    role.setName("User");
                    role.setDescription("Normal customer");
                    return roleRepository.save(role);
                });
    }

    private void ensureLoginAccount(Role userRole) {
        Account account = accountRepository.findByEmail(LOGIN_EMAIL)
                .orElseGet(Account::new);

        account.setFullName(
                account.getFullName() == null || account.getFullName().isBlank()
                        ? "Nguyen Gia Cuong"
                        : account.getFullName()
        );
        account.setEmail(LOGIN_EMAIL);
        account.setPasswordHash(passwordEncoder.encode(LOGIN_PASSWORD));
        account.setProvider(AccountProvider.LOCAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setDeletedAt(null);

        if (account.getRoles() == null) {
            account.setRoles(new HashSet<>());
        }
        account.getRoles().add(userRole);

        accountRepository.save(account);
    }
}
