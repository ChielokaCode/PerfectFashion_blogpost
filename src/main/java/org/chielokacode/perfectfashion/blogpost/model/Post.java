package org.chielokacode.perfectfashion.blogpost.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;


@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotBlank
    @Column(name = "title", unique = true)
    private String title;

    @NotBlank
    @Column(name = "image")
    private String image;

    @NotBlank
    @Column(name = "description")
    private String description;

    @Column(name = "published")
    private boolean published;

    @JsonIgnore
    private Integer likes = 0;

    /*
    The ManyToMany annotation with a join table (post_likes)
     is used to model the many-to-many relationship between posts and users who liked them.
     */
    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> likedBy = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(name = "postDate")
//    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    @JsonIgnore
    private LocalDateTime postDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

//    public Post(){}
//
//    public Post( String title, String image, String description, boolean published) {
//        this.title = title;
//        this.image = image;
//        this.description = description;
//        this.published = published;
//    }
//    @JsonIgnore
    public List<Comment> getComments() {
        return comments == null ? null : new ArrayList<>(comments);
    }
//    @JsonIgnore

    public void setComments(List<Comment> comments) {
//        if (comments == null) {
//            this.comments = null;
//        } else {
//            this.comments = Collections.unmodifiableList(comments);
//        }
        this.comments = comments;
    }

}
