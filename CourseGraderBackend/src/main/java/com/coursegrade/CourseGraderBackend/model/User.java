package com.coursegrade.CourseGraderBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private Integer expectedGrad;

    @Column(name = "college")
    private String college; // e.g., "CAS", "ENG", "COM"

    @Column(name = "major")
    private String major; // e.g., "Computer Science", "Mathematics"

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    @ManyToMany
    @JoinTable(
            name = "completed_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> completedCourses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_hub_progress",
            joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "hub_requirement")
    @Column(name = "completed_count")
    private Map<HubRequirement, Integer> hubProgress = new HashMap<>();

    private boolean enabled = false; // email isn't verified initially

    public void updateHubProgress() {
        hubProgress.clear();
        for (Course course : completedCourses) {
            for (HubRequirement hub : course.getHubRequirements()) {
                hubProgress.put(hub, hubProgress.getOrDefault(hub, 0) + 1);
            }
        }
    }

    public boolean isHubFulfilled(HubRequirement hub) {
        Integer count = hubProgress.get(hub);
        return count != null && count >= hub.getReqCount();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
