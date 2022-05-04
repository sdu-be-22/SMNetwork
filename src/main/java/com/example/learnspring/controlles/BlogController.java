package com.example.learnspring.controlles;


import antlr.collections.List;
import com.example.learnspring.models.Comment;
import com.example.learnspring.models.Post;
import com.example.learnspring.models.User;
import com.example.learnspring.repo.PostRepository;
import com.example.learnspring.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Controller
public class BlogController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/blog")
    @PreAuthorize("hasAuthority('developers:read')")
    public String blogMain(Model model){
        Iterable<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("post", posts);
        return "blog-main";
    }

    @GetMapping("/blog/add")
    @PreAuthorize("hasAuthority('developers:write')")
    public String blogAdd(Model model){
        try{
            return "blog-add";
        }catch (AccessDeniedException e){
            return "access-exception";
        }

    }

    @PostMapping("/blog/add")
    @PreAuthorize("hasAuthority('developers:write')")
    public String blogPostAdd(@RequestParam String title,@RequestParam String anons,@RequestParam String full_text, Model model){
        Post post = new Post(title, anons, full_text);
        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserName);
        post.setUser(user);
        post.getUser().setPost(post);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String blogDetails(@PathVariable(value = "id") long id, Model model){
        if(!postRepository.existsById(id)) return "redirect:/blog";
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "blog-details";

    }

    @GetMapping("/blog/{id}/edit")
    @PreAuthorize("hasAuthority('developers:write')")
    public String blogEdit(@PathVariable(value = "id") long id, Model model){
        if(!postRepository.existsById(id)) return "redirect:/blog";
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "blog-edit";
    }

    @PostMapping("/blog/{id}/edit")
    @PreAuthorize("hasAuthority('developers:write')")
    public String blogPostUpdate(@PathVariable(value = "id") long id, @RequestParam String title,@RequestParam String anons,@RequestParam String full_text, Model model){
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @PostMapping("/blog/{id}/remove")
    @PreAuthorize("hasAuthority('developers:write')")
    public String blogPostDelete(@PathVariable(value = "id") long id, Model model){
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/blog";
    }
}