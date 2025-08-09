package com.Sayyad.ShopEasy;

import com.Sayyad.ShopEasy.Entity.Role;
import com.Sayyad.ShopEasy.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultRolesLoader implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles if they don't exist
        if (roleRepository.findByName("Customer").isEmpty()) {
            roleRepository.save(new Role("Customer"));
        }
        if (roleRepository.findByName("Super Admin").isEmpty()) {
            roleRepository.save(new Role("Super Admin"));
        }
        if (roleRepository.findByName("Vendor").isEmpty()) {
            roleRepository.save(new Role("Vendor"));
        }
        if (roleRepository.findByName("Delivery Partner").isEmpty()) {
            roleRepository.save(new Role("Delivery Partner"));
        }
    }
}
