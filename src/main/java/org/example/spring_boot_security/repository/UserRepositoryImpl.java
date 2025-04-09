package org.example.spring_boot_security.repository;

import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<User> findAllWithRoles() {
        return new ArrayList<>(jdbcTemplate.query("SELECT u.id as user_id, u.user_name, u.last_name, u.age, u.email, u.password, r.id as role_id, r.role FROM user u " +
                                            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                                            "LEFT JOIN role r ON ur.role_id = r.id ",
                                            (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("user_id"));
                u.setUserName(rs.getString("user_name"));
                u.setLastName(rs.getString("last_name"));
                u.setAge(rs.getInt("age"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));

                Role role = new Role();
                role.setId(rs.getLong("role_id"));
                role.setRole(rs.getString("role"));

                Set<Role> roles = new HashSet<>();
                roles.add(role);
                u.setRole(roles);
                return u;
            }).stream()
            .collect(groupingBy(User::getId, collectingAndThen(
                    mapping(u -> u, toSet()),
                    users -> {
                        User result = users.iterator().next();
                        result.setRole(users.stream()
                                .flatMap(u -> Optional.ofNullable(u.getRole()).orElse(Collections.emptySet()).stream())
                                .collect(toSet()));
                        return result;
                    }
            ))).values());
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT u.id as user_id, u.user_name, u.last_name, u.age, u.email, u.password FROM user u WHERE u.user_name = ?",
                                                    new Object[]{userName}, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("user_id"));
                u.setUserName(rs.getString("user_name"));
                u.setLastName(rs.getString("last_name"));
                u.setAge(rs.getInt("age"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                return u;
            });
            Set<Role> roles = jdbcTemplate.query("SELECT r.id as role_id, r.role FROM user_role ur " +
                                                        "LEFT JOIN role r ON ur.role_id = r.id " +
                                                        "WHERE ur.user_id = ?",
                                                        new Object[]{user.getId()}, (rs, rowNum) -> {
                    Role role = new Role();
                    role.setId(rs.getLong("role_id"));
                    role.setRole(rs.getString("role"));
                    return role;
                }).stream().collect(Collectors.toSet());
            user.setRole(roles);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(String userName, String lastName, int age, String email, String password, String roles) {
        jdbcTemplate.update("INSERT INTO user (user_name, last_name, age, email, password) VALUES (?, ?, ?, ?, ?)",
                                    userName, lastName, age, email, password);
        Optional<Role> role = roleRepository.findByRole(roles);
        Optional<User> user = findByUsername(userName);
        jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", user.get().getId(), role.get().getId());
    }

    @Override
    public void save(String userName, String lastName, int age, String email, String password, Set<String> roles) {
        jdbcTemplate.update("INSERT INTO user (user_name, last_name, age, email, password) VALUES (?, ?, ?, ?, ?)",
                userName, lastName, age, email, password);
        Optional<User> user = findByUsername(userName);
        Iterator<String> iterator = roles.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            Optional<Role> role = roleRepository.findByRole(element);
            jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", user.get().getId(), role.get().getId());
        }
    }

    @Override
    public void updateUser(Long id, String userName, String lastName, int age, String email, String password, String roles) {
        jdbcTemplate.update("UPDATE user SET user_name = ?, last_name = ?, age = ?, email = ?, password = ? WHERE id = ?", userName, lastName, age, email, password, id);
        jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", id);
        Optional<Role> role = roleRepository.findByRole(roles);
        jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", id, role.get().getId());
    }

    @Override
    public void deleteUserById(Long id) {
        jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM user WHERE id = ?", id);
    }

    @Override
    public Optional<User> findById(long userId) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT u.id as user_id, u.user_name, u.last_name, u.age, u.email, u.password FROM user u WHERE u.id = ?",
                                                        new Object[]{userId}, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("user_id"));
                u.setUserName(rs.getString("user_name"));
                u.setLastName(rs.getString("last_name"));
                u.setAge(rs.getInt("age"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                return u;
            });

            Set<Role> roles = jdbcTemplate.query("SELECT r.id as role_id, r.role FROM user_role ur LEFT JOIN role r ON ur.role_id = r.id WHERE ur.user_id = ?",
                                                        new Object[]{user.getId()}, (rs, rowNum) -> {
                Role role = new Role();
                role.setId(rs.getLong("role_id"));
                role.setRole(rs.getString("role"));
                return role;
            }).stream().collect(Collectors.toSet());
            user.setRole(roles);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
