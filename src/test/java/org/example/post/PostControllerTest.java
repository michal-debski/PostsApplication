package org.example.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;
    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        //CREATE SOME POSTS
        posts = List.of(new Post(1, 1, "Hello world", "This is my first post.", null),
                new Post(2, 1, "Second Post", "This is my second post.", null)
        );
    }
    //REST API

    //LIST
    @Test
    void shouldFindAllPosts() throws Exception {

        String jsonResponse = """
                [
                    {
                      "userId": 1, 
                      "id": 1,
                      "title": "Hello world",
                      "body": "This is my first post.",
                      "version": null
                    },
                    {
                      "userId": 1,
                      "id": 2,
                      "title": "Second Post",
                      "body": "This is my second post.",
                      "version": null
                    }
                ]
                               
                """;

        when(postRepository.findAll()).thenReturn(posts);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));

        var post = posts.get(0);
        StringBuilder json = new StringBuilder();
        json.append("{\n")
                .append("  \"id\": ").append(post.id()).append(",\n")
                .append("  \"userId\": ").append(post.userId()).append(",\n")
                .append("  \"title\": \"").append(post.title()).append("\",\n")
                .append("  \"body\": \"").append(post.body()).append("\",\n")
                .append("  \"version\": null\n")
                .append("}");

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json.toString()));
    }


    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenPostIsValid() throws Exception {

        var post = new Post(1,1,"NEW TITLE","NEW BODY", null);
        when(postRepository.save(post)).thenReturn(post);

        StringBuilder json = new StringBuilder();
        json.append("{\n")
                .append("  \"id\": ").append(post.id()).append(",\n")
                .append("  \"userId\": ").append(post.userId()).append(",\n")
                .append("  \"title\": \"").append(post.title()).append("\",\n")
                .append("  \"body\": \"").append(post.body()).append("\",\n")
                .append("  \"version\": null\n")
                .append("}");




        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isCreated());
    }


    @Test
    void shouldNotCreatePostWhenIsInvalid() throws Exception {
        var post = new Post(3,1,"","", null);
        when(postRepository.save(post)).thenReturn(post);

        StringBuilder json = new StringBuilder();
        json.append("{\n")
                .append("  \"id\": ").append(post.id()).append(",\n")
                .append("  \"userId\": ").append(post.userId()).append(",\n")
                .append("  \"title\": \"").append(post.title()).append("\",\n")
                .append("  \"body\": \"").append(post.body()).append("\",\n")
                .append("  \"version\": null\n")
                .append("}");




        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(json.toString()))
                    .andExpect(status().isBadRequest());
    }

    //update, delete

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        Post updated = new Post(1,1, "This is new title", "This is new body", null);


        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\n")
                .append("  \"id\": ").append(updated.id()).append(",\n")
                .append("  \"userId\": ").append(updated.userId()).append(",\n")
                .append("  \"title\": \"").append(updated.title()).append("\",\n")
                .append("  \"body\": \"").append(updated.body()).append("\",\n")
                .append("  \"version\": null\n")
                .append("}");


        when(postRepository.findById(1)).thenReturn(Optional.of(updated));
        when(postRepository.save(updated)).thenReturn(updated);

        mockMvc.perform(put("/api/posts/1")
                .contentType("application/json")
                .content(requestBody.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePostWhenGivenValidId() throws Exception {

        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postRepository,times(1)).deleteById(1);
    }

}
