package org.chielokacode.perfectfashion.blogpost.repository;

import org.chielokacode.perfectfashion.blogpost.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    /*

    findByPublished(): returns all Posts with published having value as input published.
    findByTitleContaining(): returns all Posts which title contains input title.

     */
    List<Post> findByPublished(boolean published);

    List<Post> findByTitleContaining(String title);

    Page<Post> findByTitleIgnoreCaseStartsWith(String title, Pageable pageable);

    //Pagination
//    Page<Post> findAll(Pageable pageable);
//    Page<Post> findByPublished(boolean published, Pageable pageable);
//    Page<Post> findByTitleContaining(String title, Pageable pageable);
}
