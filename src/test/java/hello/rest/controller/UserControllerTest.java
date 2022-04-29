package hello.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.rest.dto.SigninDto;
import hello.rest.dto.UserPasswordChangeDto;
import hello.rest.dto.UserUpdateDto;
import hello.rest.entity.User;
import hello.rest.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        user = userJpaRepository.save(User.builder()
                .email("a@a.com")
                .name("a")
                .password(passwordEncoder.encode("123"))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        String content = objectMapper.writeValueAsString(new SigninDto("a@a.com", "123"));
        MvcResult result = mockMvc.perform(post("/api/signin")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        token = jsonParser.parseMap(resultString).get("data").toString();
        int index = token.indexOf("token=");
        int lastIndex = token.length() - 1;
        token = token.substring(index, lastIndex).substring(6);
    }

    @Test
    void invalidToken() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("AUTHENTICATION", "XXXXXXX"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entrypoint")); //https://codecrafting.tistory.com/2
    }

    @Test
    @WithMockUser(username = "mockUser", roles = {"ADMIN"})
        // 가상의 Mock 유저 대입
    void accessdenied() throws Exception {
        mockMvc.perform(get("/api/users"))
//                        .header("AUTHENTICATION", token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/accessdenied"));
    }

    @Test
    void findAllUser() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    //path variable 적용 https://blusky10.tistory.com/386
    @Test
    void findUser() throws Exception {
        mockMvc.perform(get("/api/user/{userId}", user.getId())
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void modify() throws Exception {
        String content = objectMapper.writeValueAsString(new UserUpdateDto("a@a.com", "123"));
        mockMvc.perform(put("/api/user/{userId}", user.getId())
                        .header("Authorization", token)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform((MockMvcRequestBuilders.delete("/api/user/1")
                        .header("Authorization", token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void changePassword() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(new UserPasswordChangeDto("123", "1234", "1234"));

        //when

        //then
        mockMvc.perform(put("/api/user/{userEmail}", user.getEmail())
                        .header("Authorization", token)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }
}