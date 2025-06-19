package com.avenga.api.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Represents the Book object
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto {

    private Integer id;
    private String title;
    private String description;
    private Integer pageCount;
    private String excerpt;
    private String publishDate;
}
