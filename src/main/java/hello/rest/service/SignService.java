package hello.rest.service;

import hello.rest.advice.exception.CEmailSigninFailedException;
import hello.rest.advice.exception.CUserDuplicatedException;
import hello.rest.config.security.JwtTokenProvider;
import hello.rest.dto.SigninDto;
import hello.rest.dto.SignupDto;
import hello.rest.entity.User;
import hello.rest.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SignService {

    private final UserJpaRepository userJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String signin(SigninDto signinDto) {
        User user = userJpaRepository.findByEmail(signinDto.getEmail()).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(signinDto.getPassword(), user.getPassword())) {
            throw new CEmailSigninFailedException();
        }

        return jwtTokenProvider.createToken(String.valueOf(user.getId()), user.getRoles());
    }

    public SignupDto signup(SignupDto signupDto) {

        if (this.isSignupDuplicate(signupDto)) {
            log.warn("중복된 회원가입");
            throw new CUserDuplicatedException();
        }

        User user = userJpaRepository.save(User.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .name(signupDto.getName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        SignupDto result = SignupDto.builder()
                .email(user.getEmail())
                .password(signupDto.getPassword())
                .name(signupDto.getName())
                .build();

        return result;
    }

    public boolean isSignupDuplicate(SignupDto signupDto) {
        Optional<User> user = userJpaRepository.findByEmail(signupDto.getEmail());

        if (user.isPresent()) {
            return true;
        }

        return false;
    }
}
