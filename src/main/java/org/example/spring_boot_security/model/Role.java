package org.example.spring_boot_security.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority, Comparable<Role> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", unique = true)
    private String role;

    @Transient
    @ManyToMany(mappedBy = "role")
    private Set<User> user;

    public Role (String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Role role1)) return false;
        return Objects.equals(getId(), role1.getId()) && Objects.equals(getRole(), role1.getRole()) && Objects.equals(getUser(), role1.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRole(), getUser());
    }

    public Set<User> getUser() {
        return user;
    }

    public void setUser(Set<User> user) {
        this.user = user;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    @Override
    public int compareTo(Role other) {
        return Long.compare(this.id, other.id);
    }
}
