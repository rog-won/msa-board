package rok.board.like.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_like_count")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLikeCount {
    @Id
    private Long articleId;
}
