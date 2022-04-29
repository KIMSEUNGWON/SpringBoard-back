//package hello.rest.controller.comment;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import hello.rest.dto.SigninDto;
//import hello.rest.entity.User;
//import hello.rest.entity.board.Post;
//import hello.rest.entity.comment.Comment;
//import hello.rest.repository.UserJpaRepository;
//import hello.rest.service.CommentService;
//import hello.rest.service.PostService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.json.JacksonJsonParser;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@Transactional
//@AutoConfigureMockMvc
//class CommentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private UserJpaRepository userJpaRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private CommentService commentService;
//    @Autowired
//    private PostService postService;
//
//    private String token;
//    private User user;
//
//
//    @BeforeEach
//    public void setUp() throws Exception {
////        user = userJpaRepository.save(User.builder()
////                .email("a@a.com")
////                .name("a")
////                .password(passwordEncoder.encode("123"))
////                .roles(Collections.singletonList("ROLE_USER"))
////                .build());
//
//        String content = objectMapper.writeValueAsString(new SigninDto("a@a.com", "123"));
//        MvcResult result = mockMvc.perform(post("/api/signin")
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").exists())
//                .andExpect(jsonPath("$.data").exists())
//                .andReturn();
//
//        String resultString = result.getResponse().getContentAsString();
//        JacksonJsonParser jsonParser = new JacksonJsonParser();
//        token = jsonParser.parseMap(resultString).get("data").toString();
//        int index = token.indexOf("token=");
//        int lastIndex = token.length() - 1;
//        token = token.substring(index, lastIndex).substring(6);
//    }
//
//    @Test
//    public void getCommentsInPostTest() throws Exception {
//        //given
//        String boardName = "free";
//        List<Post> posts = postService.findPosts(boardName);
//        Post post = posts.get(0);
//        commentService.addComment()
//
//        //when
//
//
//        //then
//        ResultActions authorization = mockMvc.perform(get("/api/comment/")
//                        .header("Authorization", token)
//                        .param(""))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data").exists());
//
//        System.out.println("authorization = " + authorization);
//
//    }
//}