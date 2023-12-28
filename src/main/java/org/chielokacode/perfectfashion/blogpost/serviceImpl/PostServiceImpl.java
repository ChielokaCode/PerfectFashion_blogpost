package org.chielokacode.perfectfashion.blogpost.serviceImpl;

import org.apache.coyote.BadRequestException;
import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PagedResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PostRequest;
import org.chielokacode.perfectfashion.blogpost.config.payload.PostResponse;
import org.chielokacode.perfectfashion.blogpost.dto.PostLikesDto;
import org.chielokacode.perfectfashion.blogpost.enums.Role;
import org.chielokacode.perfectfashion.blogpost.exception.ResourceNotFoundException;
import org.chielokacode.perfectfashion.blogpost.exception.UnauthorizedException;
import org.chielokacode.perfectfashion.blogpost.model.Post;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.repository.PostRepository;
import org.chielokacode.perfectfashion.blogpost.repository.UserRepository;
import org.chielokacode.perfectfashion.blogpost.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.chielokacode.perfectfashion.blogpost.utils.AppConstants.*;

@Service
public class PostServiceImpl {

    private PostRepository postRepository;
    private UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


      //Method to create Post by only the Admin
    public PostResponse savePost(Post newPost, User currentUser) throws BadRequestException {
        //check user is present
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));

        //checks if current User is an Admin to be able to Post
        //if not, throw exception
        if (user.getUserRole().equals(Role.ROLE_ADMIN)) {

            //update the post class with the values gotten from PostRequest class
           // Post post = new ObjectMapper().convertValue(newPost, Post.class);
            newPost.setUser(user);
            postRepository.save(newPost);

            //then update the PostResponse class to view Post with the values gotten from the Post class
            //in this case, no setting of user because the PostResponse isn't connected to the Database, it's just for viewing
            //and Security

            //PostResponse newPost1 = new ObjectMapper().convertValue(post, PostResponse.class);

            PostResponse newPost1 = new PostResponse();
            newPost1.setTitle(newPost.getTitle());
            newPost1.setImage(newPost.getImage());
            newPost1.setDescription(newPost.getDescription());
            newPost1.setDescription(newPost.getDescription());

            return newPost1;
        } else {

            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Sorry, You don't have Permission to Post");
            throw new BadRequestException(String.valueOf(apiResponse));
        }
    }

    //Method to get All Posts by the Admin
    public PagedResponse<Post> getAllPosts(int page, int size) {
        //validate that page and size is not less than 0
        AppUtils.validatePageNumberAndSize(page, size);

        //get Post with title from database with page number of 10 on each page, and sorted in descending order
        Pageable pageable = PageRequest.of(page, size);

        //get all post from database with default size of 10 on each page
        Page<Post> posts = postRepository.findAll(pageable);
        //if number of post is empty return empty List else getContent
        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    //Method to search Post by title by the Admin or User
    public PagedResponse<Post> searchPostByTitle(String title, int page, int size) {
        //validate that page and size is not less than 0
        AppUtils.validatePageNumberAndSize(page, size);

        //get Post with title from database with page number of 10 on each page, and sorted in descending order
        Pageable pageable = PageRequest.of(page, size);

        //get all post with title with default size of 10 on each page
        Page<Post> posts = postRepository.findByTitleIgnoreCaseStartsWith(title, pageable);

        //if number of post is empty return empty List else getContent
        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    //Method to delete Post by id by the Admin
    public ApiResponse deletePostById(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST, ID, postId));

        if (post.getUser().getUserRole().equals(currentUser.getUserRole()) ||
                currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.toString()))) {

            postRepository.deleteById(postId);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Sorry, You don't have Permission to delete Post");
        throw new UnauthorizedException(apiResponse);

    }

//Method to edit/Update Post by id by the Admin
    public PostResponse editPostById(Long postId, PostRequest postToBeEdited, User currentUser) throws BadRequestException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST, ID, postId));
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));

        if (user.getUserRole().equals(Role.ROLE_ADMIN)) {
            post.setTitle(postToBeEdited.getTitle());
            post.setImage(postToBeEdited.getImage());
            post.setDescription(postToBeEdited.getDescription());
            post.setPublished(postToBeEdited.isPublished());
            postRepository.save(post);

            PostResponse post1 = new PostResponse();
            post1.setTitle(post.getTitle());
            post1.setImage(post.getImage());
            post1.setDescription(post.getDescription());
            post1.setPublished(post.isPublished());

            return post1;
        } else {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Sorry, You don't have Permission to Post");
            throw new BadRequestException(String.valueOf(apiResponse));
        }
    }




    /*
    This version utilizes a Set<User> for the likedBy field,
    and the likePost method now checks for existing likes using the contains method
    on the likedBy set. This approach ensures that each user can only like a post once.
     */
    public void likePost(Long postId, User currentUser){
        // Retrieve the post by ID, throw a ResourceNotFoundException if not found
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST, ID, postId));

        // Check if the user has already liked the post
        if (!post.getLikedBy().contains(currentUser)) {
            post.getLikedBy().add(currentUser);
            post.setLikes(post.getLikes() + 1);

            // Save the updated post to the repository
            postRepository.saveAndFlush(post);
        }
    }



/*
This unlikePost method complements the likePost method,
 allowing users to toggle their like status for a post.
 It checks if the user has already liked the post, and if so, it removes the like.
 */
    public void unLikePost(Long postId,User currentUser){
        // Retrieve the post by ID, throw a ResourceNotFoundException if not found
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST, ID, postId));

        // Check if the user has liked the post
        if (post.getLikedBy().contains(currentUser)) {
            post.getLikedBy().remove(currentUser);
            post.setLikes(post.getLikes() - 1);

            // Save the updated post to the repository
            postRepository.saveAndFlush(post);
        }
    }


    public PostLikesDto getLikes(Long postId, User currentUser){
        // Retrieve the post by ID, throw a ResourceNotFoundException if not found
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST, ID, postId));

        // Create a PostLikesDto with the total number of likes
        PostLikesDto postLikesDto = PostLikesDto.builder()
                .likes(post.getLikes())
                .build();

        // Initialize liked status as false
        postLikesDto.setLiked(post.getLikedBy().contains(currentUser));

        return postLikesDto;
    }


}
