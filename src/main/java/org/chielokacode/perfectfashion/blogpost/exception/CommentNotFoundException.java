package org.chielokacode.perfectfashion.blogpost.exception;

public class CommentNotFoundException extends Throwable {
    @Override
    public String getMessage() {
        return "Comment Not Found";
    }
}
