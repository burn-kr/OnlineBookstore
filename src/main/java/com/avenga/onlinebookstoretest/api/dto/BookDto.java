package com.avenga.onlinebookstoretest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;
    private String title;
    private String description;
    private int pageCount;
    private String excerpt;
    private String publishDate;
}
