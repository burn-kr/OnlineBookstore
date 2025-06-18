package com.avenga.onlinebookstoretest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private int id;
    private String title;
    private String description;
    private int pageCount;
    private String excerpt;
    private String publishDate;
}
