package org.chielokacode.perfectfashion.blogpost.serviceImpl;

import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.chielokacode.perfectfashion.blogpost.config.payload.PagedResponse;
import org.chielokacode.perfectfashion.blogpost.enums.Role;
import org.chielokacode.perfectfashion.blogpost.exception.BlogApiException;
import org.chielokacode.perfectfashion.blogpost.exception.ResourceNotFoundException;
import org.chielokacode.perfectfashion.blogpost.model.Comment;
import org.chielokacode.perfectfashion.blogpost.model.Post;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.repository.CommentRepository;
import org.chielokacode.perfectfashion.blogpost.repository.PostRepository;
import org.chielokacode.perfectfashion.blogpost.repository.UserRepository;
import org.chielokacode.perfectfashion.blogpost.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl {
    private static final String THIS_COMMENT = " this comment";

    private static final String YOU_DON_T_HAVE_PERMISSION_TO = "You don't have permission to ";

    private static final String ID_STR = "id";

    private static final String COMMENT_STR = "Comment";

    private static final String POST_STR = "Post";

    private static final String COMMENT_DOES_NOT_BELONG_TO_POST = "Comment does not belong to post";


    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }




    //Method to create comment by the current user to a post posted by the admin
    public Comment createCommentByPostId(Long postId, User currentUser, Comment newComment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_STR, ID_STR, postId));

        User user = userRepository.getUser(currentUser);
        Comment comment = new Comment(newComment.getContent());
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }




    //Method to edit Comment by the user that posted the comment or by the admin
    public Comment editComment(Long commentId, Long postId, User currentUser, Comment newComment) {

        //check that post and comment is present
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_STR, ID_STR, postId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_STR, ID_STR, commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
        }

        /*
         the code checks whether the current user has the authority to update a comment
         based on either being the creator of the comment or having administrative privileges.
         If the conditions are met, the comment is updated, and a success response is returned.
          If the conditions are not met, the comment is not updated.
         */
        if (comment.getUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.toString()))) {

            comment.setContent(newComment.getContent());
            return commentRepository.save(comment);
        }

        throw new BlogApiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO + "update" + THIS_COMMENT);
    }





    //Method to get All Comment associated to a Post
    public PagedResponse<Comment> getAllComments(Long postId, int page, int size) {
        //to validate page and size number
        AppUtils.validatePageNumberAndSize(page, size);

        /*
            //page (current page number), and size (number of items per page)
        this method retrieves comments for a specific post using pagination.
        It validates the page number and size, creates a Pageable object for pagination,
         queries the repository for comments, and constructs a PagedResponse object containing
         the paginated comments and metadata.
         */

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        return new PagedResponse<>(comments.getContent(), comments.getNumber(), comments.getSize(),
        				comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
    }





    //Method to get a Comment associated to a Post
     public Comment getComment(Long postId, Long commentId) {
         Post post = postRepository.findById(postId)
                 .orElseThrow(() -> new ResourceNotFoundException(POST_STR, ID_STR, postId));
         Comment comment = commentRepository.findById(commentId)
                 .orElseThrow(() -> new ResourceNotFoundException(COMMENT_STR, ID_STR, commentId));
         /*
         It checks if the retrieved comment belongs to the specified post by comparing the IDs of the post
         associated with the comment and the provided post (postId). If they match, it returns the comment.
          */

         if (comment.getPost().getId().equals(post.getId())) {
         			return comment;
         }
         throw new BlogApiException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
     }






    //Method to delete Comment by the user that posted the comment or by the admin
    public ApiResponse deleteCommentByCommentId(Long commentId, Long postId, User currentUser) {

        //check that post and comment is present
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_STR, ID_STR, postId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_STR, ID_STR, commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            return new ApiResponse(Boolean.FALSE, COMMENT_DOES_NOT_BELONG_TO_POST);
        }

        /*
         the code checks whether the current user has the authority to delete a comment
         based on either being the creator of the comment or having administrative privileges.
         If the conditions are met, the comment is deleted, and a success response is returned.
          If the conditions are not met, the comment deletion does not occur.
         */
        if (comment.getUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.toString()))) {
            commentRepository.deleteById(comment.getId());
            return new ApiResponse(Boolean.TRUE, "You successfully deleted comment");
        }

        throw new BlogApiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO + "delete" + THIS_COMMENT);
    }


    public PagedResponse<Comment> searchCommentByContent(String content, Long postId, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_STR, ID_STR, postId));


        //get Comment with content from database with page number of 10 on each page, and sorted in descending order
        Pageable pageable = PageRequest.of(page, size);

        //get all comment with content with default size of 10 on each page
        Page<Comment> comment = commentRepository.findContentByPostIdIgnoreCaseContains(content, pageable);

        //if number of comment is empty return empty List else getContent
        List<Comment> searchedComments = comment.getNumberOfElements() == 0 ? Collections.emptyList() : comment.getContent();

        post.setComments(searchedComments);

        return new PagedResponse<>(searchedComments, comment.getNumber(), comment.getSize(), comment.getTotalElements(),
                comment.getTotalPages(), comment.isLast());
    }
}
