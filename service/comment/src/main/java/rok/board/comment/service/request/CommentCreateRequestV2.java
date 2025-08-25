package rok.board.comment.service.request;

import lombok.Getter;

@Getter
public class CommentCreateRequestV2 {

    private Long articleId;
    private String content;
    private String parenPath;
    private Long writerId;

}