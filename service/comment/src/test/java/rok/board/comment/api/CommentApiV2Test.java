package rok.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import rok.board.comment.service.response.CommentPageResponse;
import rok.board.comment.service.response.CommentResponse;

import java.util.List;

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

    @Test
    void readAll(){
        CommentPageResponse res = restClient.get()
                .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("res.getCommentCount() = " + res.getCommentCount());
        for (CommentResponse comment : res.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> res1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("first call done.");
        for (CommentResponse res : res1) {
            System.out.println("res1.getCommentId() = " + res.getCommentId());
        }

        String lastPath = res1.getLast().getPath();
        List<CommentResponse> res2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("second call done.");
        for (CommentResponse res : res2) {
            System.out.println("res2.getCommentId() = " + res.getCommentId());
        }
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
