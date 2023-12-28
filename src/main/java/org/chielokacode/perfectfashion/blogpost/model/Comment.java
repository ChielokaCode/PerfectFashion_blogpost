package org.chielokacode.perfectfashion.blogpost.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;


//
//    @Column(name = "name")
//    @NotBlank
//    @Size(min = 4, max = 50)
//    private String name;
//
//    @Column(name = "email")
//    @NotBlank
//    @Email
//    @Size(min = 4, max = 50)
//    private String email;


            @Column(name = "content")
            @Size(min = 10, message = "Comment body must be minimum 10 characters")
            private String content;

            @ManyToOne(fetch = FetchType.EAGER, optional = false)
            @JoinColumn(name = "post_id", nullable = false)
            @OnDelete(action = OnDeleteAction.CASCADE)
            @JsonIgnore
            private Post post;


            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "user_id")
            @JsonIgnore
            private User user;



            @Column(name = "commentDate")
//            @Temporal(TemporalType.TIMESTAMP)
            @CreationTimestamp
            @JsonIgnore
            private LocalDateTime commentDate;

        public Comment(@NotBlank @Size(min = 10, message = "Comment body must be minimum 10 characters") String content) {
                this.content = content;
        }

            /*
            Now we can see the pros of @ManyToOne annotation.
           – With @OneToMany, we need to declare a collection inside parent class,
           we cannot limit the size of that collection, for example, in case of pagination.
           (so we will not be using this)
           we wont use @OneToMany because we wont be able to limit the size of the collection,
           which we know fully well that in the case of pagination, we need to limit the collection per page

             – With @ManyToOne, you can modify Repository to work with Pagination,
             */

    @JsonIgnore
    public Post getPost() {
        return post;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
}