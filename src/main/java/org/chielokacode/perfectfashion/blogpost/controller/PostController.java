package org.chielokacode.perfectfashion.blogpost.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PagedResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PostRequest;
import org.chielokacode.perfectfashion.blogpost.config.payload.PostResponse;
import org.chielokacode.perfectfashion.blogpost.dto.PostLikesDto;
import org.chielokacode.perfectfashion.blogpost.exception.PostNotFoundException;
import org.chielokacode.perfectfashion.blogpost.model.Post;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.serviceImpl.PostServiceImpl;
import org.chielokacode.perfectfashion.blogpost.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostServiceImpl postService;

    @Autowired
    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    //Endpoint to create Post by only Users with a role of Role_Admin
    @PostMapping("/create-post")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody Post newPost, @AuthenticationPrincipal User currentUser) throws BadRequestException {
        PostResponse post = postService.savePost(newPost, currentUser);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }


    //Endpoint to get all Posts with default page number 0 and size 10
    @GetMapping("/all-post")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<Post>> getAllPosts(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<Post> response = postService.getAllPosts(page, size);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    //Endpoint to get/Search all Posts with Title with default page number 0 and size 10
    @GetMapping("/search-post/{title}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PagedResponse<Post>> searchPost(@PathVariable String title,
                                                          @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        PagedResponse<Post>  response = postService.searchPostByTitle(title, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //Endpoint to delete Post by id only by Admin
    @DeleteMapping("/delete-post/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePostById(@PathVariable Long postId, @AuthenticationPrincipal User currentUser){

        ApiResponse response = postService.deletePostById(postId, currentUser);

        HttpStatus status = response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(response, status);
    }


    //Endpoint to edit/Update Post
    @PutMapping("/edit-post/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponse> editPostById(@PathVariable Long postId,
                                                     @Valid @RequestBody PostRequest postToBeEdited,
                                                     @AuthenticationPrincipal User currentUser) throws BadRequestException {

        PostResponse response = postService.editPostById(postId, postToBeEdited, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/like-post/{postId}")
    public ResponseEntity<String> likePost(@PathVariable(name = "postId") Long postId,
                                           @AuthenticationPrincipal User currentUser) throws PostNotFoundException {
        postService.likePost(postId, currentUser);
        return new ResponseEntity<>("You Liked Post", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/unlike-post/{postId}")
    public ResponseEntity<String> unLikePost(@PathVariable(name = "postId") Long postId,
                                             @AuthenticationPrincipal User currentUser) throws PostNotFoundException {
        postService.unlikePost(postId, currentUser);
        return new ResponseEntity<>("You Unliked Post", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-likes/{postId}")
    public ResponseEntity<PostLikesDto> getLikes(@PathVariable(name = "postId") Long postId, @AuthenticationPrincipal User currentUser) throws PostNotFoundException {
        PostLikesDto response = postService.getLikes(postId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
