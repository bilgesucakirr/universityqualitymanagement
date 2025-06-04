package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.List; // For children list

@Entity
@Table(name = "yokak_criteria")
public class YokakCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., "A", "A.1", "A.1.1"

    @Column(nullable = false)
    private String name; // e.g., "Liderlik, Yönetişim ve Kalite", "Yönetişim modeli ve idari yapı"

    @Enumerated(EnumType.STRING) // Enum'ı String olarak kaydet
    @Column(nullable = false)
    private CriterionLevel level; // HEADER, MAIN_CRITERION, SUB_CRITERION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Self-referencing foreign key
    private YokakCriterion parent; // Reference to parent criterion (e.g., A.1 is parent of A.1.1)

    // Optional: For easily retrieving children from a parent
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YokakCriterion> children;

    public YokakCriterion() {}

    public YokakCriterion(String code, String name, CriterionLevel level, YokakCriterion parent) {
        this.code = code;
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CriterionLevel getLevel() { return level; }
    public void setLevel(CriterionLevel level) { this.level = level; }
    public YokakCriterion getParent() { return parent; }
    public void setParent(YokakCriterion parent) { this.parent = parent; }
    public List<YokakCriterion> getChildren() { return children; }
    public void setChildren(List<YokakCriterion> children) { this.children = children; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YokakCriterion that = (YokakCriterion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}