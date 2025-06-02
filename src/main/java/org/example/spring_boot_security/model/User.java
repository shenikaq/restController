package org.example.spring_boot_security.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_boot_security.dto.UserRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
//@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT) // Отключаем FK
    )
    private Set<Role> role;

    public User(UserRequest userRequest) {
        this.userName = userRequest.getUsername();
        this.lastName = userRequest.getLastName();
        this.age = userRequest.getAge();
        this.email = userRequest.getEmail();
        this.password = userRequest.getPassword();

        this.role = userRequest.getRoles().stream()
                .map(Role::getRole) // Берем название роли из объекта Role
                .map(Role::new)     // Создаем новый объект Role из названия
                .collect(Collectors.toSet());
    }

    public User(String userName, String lastName, int age, String email, String password) {
        this.userName = userName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String lastName, int age, String email, String password, Set<Role> role) {
        this.userName = userName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Long id, String userName, String lastName, int age, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return getAge() == user.getAge() && Objects.equals(getId(), user.getId()) && Objects.equals(userName, user.userName) && Objects.equals(getLastName(), user.getLastName()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getRole(), user.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), userName, getLastName(), getAge(), getEmail(), getPassword(), getRole());
    }

    @Override
    public Collection <? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
