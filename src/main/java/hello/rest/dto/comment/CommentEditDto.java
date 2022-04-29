package hello.rest.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEditDto {

    @NotEmpty
    @Size(min = 2, max = 140)
    private String content;

    private Boolean isSecret;
}
