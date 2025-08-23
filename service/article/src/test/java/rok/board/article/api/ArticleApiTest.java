package rok.board.article.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import rok.board.article.service.response.ArticlePageResponse;
import rok.board.article.service.response.ArticleResponse;

public class ArticleApiTest {

    //resttemplate같이 http요청을 할 수 있는 클래스. http요청을 보내는 클라이언트 역할
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        ArticleResponse res = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("res = " + res);
    }

    @Test
    void readTest(){
        ArticleResponse res = read(217559997844258816L);
        System.out.println("res = " + res);
    }

    @Test
    void updateTest(){
        update(217559997844258816L);
        ArticleResponse res = read(217559997844258816L);
        System.out.println("res = " + res);
    }

    @Test
    void deleteTest(){
        restClient.delete()
                .uri("/v1/articles/{articleId}", 217559997844258816L)
                .retrieve()
                .body(Void.class);
    }

    @Test
    void readAllTest(){
        ArticlePageResponse res = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=1")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("res.getArticleCount = " + res.getArticleCount());

        for (ArticleResponse article : res.getArticles() ) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    void update(Long articleId){
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2222", "my content 22222"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse read(Long articleId){
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse create(ArticleCreateRequest req){
        return restClient.post()
                .uri("/v1/articles")
                .body(req)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }

}
