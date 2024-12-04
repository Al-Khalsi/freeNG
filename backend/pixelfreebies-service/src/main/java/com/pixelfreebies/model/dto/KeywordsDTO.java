package com.pixelfreebies.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pixelfreebies.model.domain.Keywords}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of keyword.")
public class KeywordsDTO implements Serializable {

    private Long id;
    private String keyword;

}