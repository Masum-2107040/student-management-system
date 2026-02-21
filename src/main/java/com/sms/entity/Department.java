package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Course> courses = new HashSet<>();
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Teacher> teachers = new HashSet<>();
}
