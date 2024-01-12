package org.chielokacode.perfectfashion.blogpost.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PagedResponse;
import org.chielokacode.perfectfashion.blogpost.dto.CommentLikesDto;
import org.chielokacode.perfectfashion.blogpost.dto.PostLikesDto;
import org.chielokacode.perfectfashion.blogpost.exception.CommentNotFoundException;
import org.chielokacode.perfectfashion.blogpost.exception.PostNotFoundException;
import org.chielokacode.perfectfashion.blogpost.model.Comment;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.serviceImpl.CommentServiceImpl;
import org.chielokacode.perfectfashion.blogpost.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@SecurityRequirement(name = "Bearer Authentication")
@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@RequestMapping("/api/post-comment/")
public class CommentController {

    private final CommentServiceImpl commentService;

    @Autowired
    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }


    //Endpoint to create Comment by the current user to a post posted by admin
    @PostMapping("/{postId}/create-comment")
    public ResponseEntity<Comment> createCommentByPostId(@PathVariable(name = "postId") Long postId,
                                                         @Valid @RequestBody Comment newComment,
                                                         @AuthenticationPrincipal User currentUser){
        Comment newComment1 = commentService.createCommentByPostId(postId,currentUser, newComment);
        return new ResponseEntity<>(newComment1, HttpStatus.CREATED);
    }




    //Endpoint to edit Comment by the user that posted the comment or by the admin
    @PutMapping("/edit-comment/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable(name = "commentId") Long commentId,
                                               @AuthenticationPrincipal User currentUser,
                                                       @Valid @RequestBody Comment newComment){
        Comment updatedComment = commentService.editComment(commentId, currentUser, newComment);

        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }



    //Endpoint to get all Comments associated to a Post
    @GetMapping("/{postId}/get-all-comment")
    public ResponseEntity<PagedResponse<Comment>> getAllComment(@PathVariable(name = "postId") Long postId,
                                                                @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_COMMENT_NUMBER) Integer page,
                                                                @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_COMMENT_SIZE ) Integer size) {

        PagedResponse<Comment> allComments = commentService.getAllComments(postId, page, size);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }



    //Endpoint to get a Comment associated to a Post
    @GetMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable(name = "postId") Long postId,
                                              @PathVariable(name = "commentId") Long commentId){
        Comment comment = commentService.getComment(postId, commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }



    //Endpoint to delete Comment by the user that posted the comment or by the admin
    @DeleteMapping("/{postId}/delete-comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteCommentByCommentId(@PathVariable(name = "postId") Long commentId,
                                                                @PathVariable(name = "commentId") Long postId,
                                                                @AuthenticationPrincipal User currentUser){
        ApiResponse response = commentService.deleteCommentByCommentId(postId, commentId, currentUser);
        HttpStatus status = response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(response, status);
    }


    @GetMapping("/search-comment/{content}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PagedResponse<Comment>> searchComment(@PathVariable String content,
                                                          @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        PagedResponse<Comment>  response = commentService.searchCommentByContent(content, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /////////////like and unlike////////////
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/like-comment/{commentId}")
    public ResponseEntity<String> likeComment(@PathVariable(name = "commentId") Long commentId,
                                           @AuthenticationPrincipal User currentUser) throws CommentNotFoundException {
        commentService.likeComment(commentId, currentUser);
        return new ResponseEntity<>("You Liked Comment", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/unlike-comment/{commentId}")
    public ResponseEntity<String> unLikeComment(@PathVariable(name = "commentId") Long commentId,
                                             @AuthenticationPrincipal User currentUser) throws CommentNotFoundException {
        commentService.unlikeComment(commentId, currentUser);
        return new ResponseEntity<>("You Unliked Comment", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-likes/{commentId}")
    public ResponseEntity<CommentLikesDto> getLikes(@PathVariable(name = "commentId") Long commentId, @AuthenticationPrincipal User currentUser) throws CommentNotFoundException {
        CommentLikesDto response = commentService.getLikes(commentId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
