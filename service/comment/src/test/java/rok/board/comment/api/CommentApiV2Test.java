package rok.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import rok.board.comment.service.response.CommentResponse;

public class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create(){
        CommentResponse res1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse res2 = create(new CommentCreateRequestV2(1L, "my comment2", res1.getPath(), 1L));
        CommentResponse res3 = create(new CommentCreateRequestV2(1L, "my comment3", res1.getPath(), 1L));

        System.out.println("res1.getCommentId() = " + res1.getCommentId());
        System.out.println("\tres2.getCommentId() = " + res2.getCommentId());
        System.out.println("\t\tres3.getCommentId() = " + res3.getCommentId());
    }

    @Test
    void read(){
        CommentResponse res = restClient.get()
                .uri("/v2/comments/{commentId}", 218390474701365248L)
                .retrieve()
                .body(CommentResponse.class);
        System.out.println("res = " + res);
    }

    @Test
    void delete(){
        restClient.delete()
                .uri("/v2/comments/{commentId}", 218390474701365248L)
                .retrieve()
                .body(CommentResponse.class);
    }

    CommentResponse create(CommentCreateRequestV2 req){
        return restClient.post()
                .uri("/v2/comments")
                .body(req)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {

        private Long articleId;
        private String content;
        private String parenPath;
        private Long writerId;

    }
}
