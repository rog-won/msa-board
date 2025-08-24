package rok.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import rok.board.comment.service.response.CommentPageResponse;
import rok.board.comment.service.response.CommentResponse;

import java.util.List;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create(){
        CommentResponse res1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse res2 = createComment(new CommentCreateRequest(1L, "my comment2", res1.getCommentId(), 1L));
        CommentResponse res3 = createComment(new CommentCreateRequest(1L, "my comment3", res1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(res1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(res2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(res3.getCommentId()));

//        commentId=217881321379823616
//        commentId=217881321572761600
//        commentId=217881321610510336


    }

    CommentResponse createComment(CommentCreateRequest req){
        return restClient.post()
                .uri("/v1/comments")
                .body(req)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read(){
        CommentResponse res = restClient.get()
                .uri("/v1/comments/{commentId}", 217881321379823616L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("res = " + res);
    }

    @Test
    void delete(){

//        commentId=217881321379823616
//        commentId=217881321572761600
//        commentId=217881321610510336

        restClient.delete()
                .uri("/v1/comments/{commentId}", 217881321610510336L)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void readAll(){
        CommentPageResponse res = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("res.getCommentCount() = " + res.getCommentCount());
        for (CommentResponse comment : res.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
        // 1번 페이지 수행 결과
//        comment.getCommentId() = 217883781116612608
//        comment.getCommentId() = 217883781162749962
//        comment.getCommentId() = 217883781116612609
//        comment.getCommentId() = 217883781158555654
//        comment.getCommentId() = 217883781116612610
//        comment.getCommentId() = 217883781158555649
//        comment.getCommentId() = 217883781116612611
//        comment.getCommentId() = 217883781158555653
//        comment.getCommentId() = 217883781116612612
//        comment.getCommentId() = 217883781158555648

    }

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> res1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse comment : res1) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        Long lastParentCommentId = res1.getLast().getParentCommentId();
        Long lastCommentId = res1.getLast().getCommentId();

        List<CommentResponse> res2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse comment : res2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }
}
