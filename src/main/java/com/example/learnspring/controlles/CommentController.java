package com.example.learnspring.controlles;

import com.example.learnspring.models.Comment;
import com.example.learnspring.models.Post;
import com.example.learnspring.repo.CommentRepository;
import com.example.learnspring.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class CommentController {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;



    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


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
        Post comPost = new Post();
        if(post.isPresent()) comPost = post.get();
        Comment comment = new Comment(text);
        comment.setPost(comPost);
        comPost.getComments().add(comment);
        commentRepository.save(comment);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}/comments/edit")
    @PreAuthorize("hasAuthority('developers:read')")
    public String commentEdit(@PathVariable(value = "id") long id, Model model){
        if(!commentRepository.existsById(id)) return "redirect:/blog";
        Optional<Comment> comment = commentRepository.findById(id);
        ArrayList<Comment> res = new ArrayList<>();
        comment.ifPresent(res::add);
        model.addAttribute("comment", res);
        return "comment-edit";
    }
    @PostMapping("/blog/{id}/comments/edit")
    @PreAuthorize("hasAuthority('developers:read')")
    public String commentUpdate(@PathVariable(value = "id") long id, @RequestParam String text, Model model){
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.setText(text);
        commentRepository.save(comment);
        return "redirect:/blog";
    }
}