package com.universityqualitymanagement.universityqualitymanagement.dtos.yokak;

import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;

public class UpdateYokakCriterionRequest {
    private String code;
    private String name;
    private CriterionLevel level; // Level might not be updated often, but included for flexibility
    private String parentId; // Parent can be changed in some scenarios

    public UpdateYokakCriterionRequest() {}

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