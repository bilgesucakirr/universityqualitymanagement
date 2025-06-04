package com.universityqualitymanagement.universityqualitymanagement.dtos.yokak;

import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;

public class YokakCriterionResponse {
    private String id;
    private String code;
    private String name;
    private CriterionLevel level;
    private String parentId;
    private String parentCode; // For display, e.g., "A.1"
    private String parentName; // For display, e.g., "Liderlik ve Kalite"

    public YokakCriterionResponse() {}

    public YokakCriterionResponse(String id, String code, String name, CriterionLevel level, String parentId, String parentCode, String parentName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.level = level;
        this.parentId = parentId;
        this.parentCode = parentCode;
        this.parentName = parentName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CriterionLevel getLevel() { return level; }
    public void setLevel(CriterionLevel level) { this.level = level; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
}