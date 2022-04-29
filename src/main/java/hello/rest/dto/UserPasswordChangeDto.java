package hello.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordChangeDto {

    private String currentPassword;
    private String newPassword;
    private String checkNewPassword;
}
