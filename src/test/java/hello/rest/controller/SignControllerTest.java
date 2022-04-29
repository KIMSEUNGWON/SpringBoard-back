package hello.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.rest.dto.SigninDto;
import hello.rest.dto.SignupDto;
import hello.rest.entity.User;
import hello.rest.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userJpaRepository.save(User.builder()
                .email("a@a.com")
                .name("a")
                .password(passwordEncoder.encode("123"))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
    }

    @Test
    void signin() throws Exception {
        String content = objectMapper.writeValueAsString(new SigninDto("a@a.com", "123"));
        mockMvc.perform(post("/api/signin")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)) //https://shinsunyoung.tistory.com/52
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void signup() throws Exception {
        long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        String content = objectMapper.writeValueAsString(SignupDto.builder().email("a_" + epochTime + "@a.com").password("123").name("a").build());
        mockMvc.perform(post("/api/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").exists());

    }

    @Test
    void signinFail() throws Exception {
        String content = objectMapper.writeValueAsString(new SigninDto("a", "123"));
        mockMvc.perform(post("/api/signin")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)) //https://shinsunyoung.tistory.com/52
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(-1001))
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void signupFail() throws Exception {
        String content = objectMapper.writeValueAsString(SignupDto.builder().email("a@a.com").password("123").name("a").build());
        mockMvc.perform(post("/api/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(-999));

    }

}