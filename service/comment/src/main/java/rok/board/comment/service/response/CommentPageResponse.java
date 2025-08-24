package rok.board.comment.service.response;

import lombok.Getter;

import java.util.List;

@Getter
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private Long commentCount;

    public static CommentPageResponse of(List<CommentResponse> comments, Long commentCount) {
        CommentPageResponse res = new CommentPageResponse();
        res.comments = comments;
        res.commentCount = commentCount;
        return res;
    }
}