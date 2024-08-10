package org.example.post;

import org.springframework.data.repository.ListCrudRepository;


public interface PostRepository extends ListCrudRepository<Post, Integer> {
}
