package org.example.post;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private PostRepository postRepository;

    @GetMapping("")
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(
                postRepository
                        .findById(id)
                        .orElseThrow(PostNotFoundException::new)
        );

    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    Post create(@RequestBody @Validated Post post){
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @RequestBody Post post){
        Optional<Post> existing = postRepository.findById(id);
        if(existing.isPresent()){
            Post updated = new Post(
                    existing.get().id(),
                    existing.get().userId(),
                    post.body(),
                    post.title(),
                    existing.get().version()
            );
            return postRepository.save(updated);
        }else {
            throw new PostNotFoundException();
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deletePostById(@PathVariable Integer id){
        postRepository.deleteById(id);
    }

}
