package namoragroup.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import namoragroup.model.Admin;
import namoragroup.repository.AdminRepository;

@Component
public class DatabaseSeeder implements ApplicationRunner {
    private static final String EMAIL_ADMIN = "admin@gmail.com";
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AdminRepository admins, PasswordEncoder passwordEncoder) {
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (admins.findByEmail(EMAIL_ADMIN).isEmpty()) {
            admins.save(new Admin("Mouhamed Ngom", EMAIL_ADMIN,
                    passwordEncoder.encode("admin"), "ADMIN", true));
        }
    }
}
