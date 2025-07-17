package com.coursegrade.CourseGraderBackend.dto;

import com.coursegrade.CourseGraderBackend.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {
    private VoteType voteType;
}
