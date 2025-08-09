package com.Sayyad.ShopEasy.Service;

import com.Sayyad.ShopEasy.Entity.Role;
import com.Sayyad.ShopEasy.Entity.User;
import com.Sayyad.ShopEasy.Repository.RoleRepository;
import com.Sayyad.ShopEasy.Repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + username));
        return new org.springframework.security.core.userdetails.User(user.getMobileNumber(), user.getPassword(), new ArrayList<>());
    }

    public User registerUser(String name, String mobileNumber, String password, String confirmPassword, String email) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = new User();
        user.setName(name);
        user.setMobileNumber(mobileNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName("Customer").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("Customer");
            return roleRepository.save(newRole);
        });
        roles.add(customerRole);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public void updateAllUserPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Check if password is already BCrypt encoded (starts with $2a$)
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
            }
        }
    }
}
