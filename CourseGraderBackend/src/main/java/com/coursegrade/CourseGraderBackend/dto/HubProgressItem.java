package com.coursegrade.CourseGraderBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubProgressItem {
    private String hubCode;
    private String hubName;
    private String category;
    private Integer required;
    private Integer completed;
    private Integer projected;
    private boolean projectedFulfilled;
    private boolean fulfilled;
    private List<String> fulfillingCourses;
}
