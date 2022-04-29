package hello.rest.service;

import hello.rest.advice.exception.CNewPasswordNotMatchWithNewPasswordCheckException;
import hello.rest.advice.exception.CUserNotFoundException;
import hello.rest.advice.exception.CUserPasswordNotMatchException;
import hello.rest.dto.UserCreateDto;
import hello.rest.dto.UserDto;
import hello.rest.dto.UserPasswordChangeDto;
import hello.rest.dto.UserUpdateDto;
import hello.rest.entity.User;
import hello.rest.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        List<User> userList = userJpaRepository.findAll();

        List<UserDto> userDtoList = userList.stream().map(user ->
                new UserDto(user.getId(), user.getEmail(), user.getName())).collect(Collectors.toList());

        return userDtoList;
    }

    public User findUser(String email) {
        User user = userJpaRepository.findByEmail(email).orElseThrow(CUserNotFoundException::new);

        return user;
    }

    public User findUser(Long userId) {
        User user = userJpaRepository.findById(userId).orElseThrow(CUserNotFoundException::new);

        return user;
    }

    public UserDto findUserById(Long userId) {
        User user = this.findUser(userId);

        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        return userDto;
    }

    public UserDto modifyUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = this.findUser(userId);

        user.updateEmailAndName(userUpdateDto.getEmail(), userUpdateDto.getName());

        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        return userDto;
    }

    public void deleteUser(Long userId) {
        User user = this.findUser(userId);

        userJpaRepository.delete(user);
    }

    public UserDto saveUser(UserCreateDto userCreateDto) {
        User user = User.builder()
                .email(userCreateDto.getEmail())
                .name(userCreateDto.getName())
                .build();

        userJpaRepository.save(user);

        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        return userDto;
    }

    public UserDto changePasswordUser(String email,
                                      UserPasswordChangeDto userPasswordChangeDto) {
        User user = this.findUser(email);

        if (!this.isNewPasswordMatchWithNewPasswordCheck(userPasswordChangeDto)) {
            throw new CNewPasswordNotMatchWithNewPasswordCheckException();
        }

        if (this.isPasswordMatchWithCurrentPassword(userPasswordChangeDto.getCurrentPassword(),
                user.getPassword())) {
            user.changePassword(passwordEncoder.encode(userPasswordChangeDto.getNewPassword()));
        } else {
            throw new CUserPasswordNotMatchException();
        }

        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        return userDto;
    }

    public boolean isNewPasswordMatchWithNewPasswordCheck(UserPasswordChangeDto userPasswordChangeDto) {
        if (userPasswordChangeDto.getNewPassword()
                .equals(userPasswordChangeDto.getCheckNewPassword())) {
            return true;
        }
        return false;
    }

    public boolean isPasswordMatchWithCurrentPassword(String currentPassword, String userPassword) {
        if (passwordEncoder.matches(currentPassword, userPassword)) {
            return true;
        }
        return false;
    }
}
