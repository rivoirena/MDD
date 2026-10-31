package com.mdd.payload.response;

import com.mdd.models.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponse {
    private Long id;
    private String title;
    private String description;
    private boolean subscribed;
}