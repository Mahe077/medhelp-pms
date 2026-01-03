package com.medhelp.pms.shared.api.validators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {
    private String field;
    private String message;
    private Object rejectedValue;
    private String code;
}
