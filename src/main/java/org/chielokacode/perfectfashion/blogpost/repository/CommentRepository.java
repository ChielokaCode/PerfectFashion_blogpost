package org.chielokacode.perfectfashion.blogpost.repository;

import jakarta.transaction.Transactional;
import org.chielokacode.perfectfashion.blogpost.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {

//    findByPostId(): returns all Comments of a Post specified by postId.

    Page<Comment> findByPostId(Long postId, Pageable pageable);

//    Page<Comment> findByContentIgnoreCaseStartsWith(String content, Pageable pageable);

//    Page<Comment> findByContentIgnoreCaseContaining(String content, Pageable pageable);

    Page<Comment> findContentByPostIdIgnoreCaseContains(String content, Pageable pageable);
}
