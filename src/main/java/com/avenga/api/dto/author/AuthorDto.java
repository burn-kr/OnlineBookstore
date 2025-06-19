package com.avenga.api.dto.author;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Represents the Author object
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AuthorDto {

    private Integer id;
    @JsonProperty("idBook")
    private Integer bookId;
    private String firstName;
    private String lastName;
}
