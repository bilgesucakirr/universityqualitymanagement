package com.universityqualitymanagement.universityqualitymanagement.dtos.yokak;

import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;

public class CreateYokakCriterionRequest {
    private String code;
    private String name;
    private CriterionLevel level;
    private String parentId; // ID of the parent criterion, null for HEADER level

    public CreateYokakCriterionRequest() {}

    public CreateYokakCriterionRequest(String code, String name, CriterionLevel level, String parentId) {
        this.code = code;
        this.name = name;
        this.level = level;
        this.parentId = parentId;
    }

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CriterionLevel getLevel() { return level; }
    public void setLevel(CriterionLevel level) { this.level = level; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}