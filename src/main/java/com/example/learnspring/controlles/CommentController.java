package com.example.learnspring.controlles;

import com.example.learnspring.models.Comment;
import com.example.learnspring.models.Post;
import com.example.learnspring.repo.CommentRepository;
import com.example.learnspring.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;


    @GetMapping("/blog/{id}/comments")
    public String getAllCommentsByPostId(@PathVariable (value = "id") Long postId,
                                                Pageable pageable, Model model) {
        Page<Comment> comment = commentRepository.findByPostId(postId, pageable);
        List<Comment> res = comment.getContent();
        model.addAttribute("comment", res);
        return "comment";
    }

    @PostMapping("/blog/{id}/comments")
    public String createComment(@PathVariable (value = "id") Long postId, @RequestParam String text, Model model) {
        Optional<Post> post = postRepository.findById(postId);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        Post post1 = res.get(0);
        Comment comment = new Comment(text);
        comment.setPost(post1);
        post1.getComments().add(comment);
        commentRepository.save(comment);
        return "blog-details";
    }

    @PostMapping("/blog/{id}/comments/{commentId}")
    public String updateComment(@PathVariable (value = "id") Long postId,
                                 @PathVariable (value = "commentId") Long commentId,
                                 Model model) {
//        Optional<Post> post = postRepository.findById(id);
//        ArrayList<Post> res = new ArrayList<>();
//        post.ifPresent(res::add);
//        model.addAttribute("post", res);
//        return "blog-edit";

        Optional<Comment> comment = commentRepository.findById(commentId);
        ArrayList<Comment> com = new ArrayList<>();
        comment.ifPresent(com::add);
        Comment myCom = com.get(0);
        model.addAttribute("comment",  com);

        return "blog-details";

//        if(!postRepository.existsById(postId)) {
//            throw new ResourceNotFoundException("PostId " + postId + " not found");
//        }
//
//        return commentRepository.findById(commentId).map(comment -> {
//            comment.setText(commentRequest.getText());
//            return commentRepository.save(comment);
//        }).orElseThrow(() -> new ResourceNotFoundException("CommentId " + commentId + "not found"));
    }
}