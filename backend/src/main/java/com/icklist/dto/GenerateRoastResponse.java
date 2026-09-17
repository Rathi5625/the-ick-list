package com.icklist.dto;

import java.util.List;

public class GenerateRoastResponse {
    private List<RoastDto> roasts;

    public GenerateRoastResponse() {
    }

    public GenerateRoastResponse(List<RoastDto> roasts) {
        this.roasts = roasts;
    }

    public List<RoastDto> getRoasts() {
        return roasts;
    }

    public void setRoasts(List<RoastDto> roasts) {
        this.roasts = roasts;
    }
}
