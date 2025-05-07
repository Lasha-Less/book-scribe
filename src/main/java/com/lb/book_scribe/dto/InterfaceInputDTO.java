package com.lb.book_scribe.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterfaceInputDTO {

    private String format;
    private String location;
    private Boolean inStock;
    private String title;
    private List<String> authorEditor;

}
