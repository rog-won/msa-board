package rok.board.like.service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rok.board.like.entity.ArticleLike;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ArticleLikeResponse {
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createAt;

    public static ArticleLikeResponse from(ArticleLike articleLike) {
        ArticleLikeResponse res = new ArticleLikeResponse();
        res.articleLikeId = articleLike.getArticleLikeId();
        res.articleId = articleLike.getArticleId();
        res.userId = articleLike.getUserId();
        res.createAt = articleLike.getCreatedAt();
        return res;
    }
}
