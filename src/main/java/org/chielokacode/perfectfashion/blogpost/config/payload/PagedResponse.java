package org.chielokacode.perfectfashion.blogpost.config.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;


    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        setContent(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<T> getContent() {
        return content == null ? null : new ArrayList<>(content);
    }

    public final void setContent(List<T> content) {
        if (content == null) {
            this.content = null;
        } else {
            this.content = Collections.unmodifiableList(content);
        }
    }

    public boolean isLast() {
        return last;
    }
}

/*
the PagedResponse class is designed to represent paginated responses,
encapsulating information about the content, page details,
and whether the current page is the last page.
The use of generics allows it to handle different types of content.
The unmodifiable list ensures that the content is not mutable from outside the class.
 */