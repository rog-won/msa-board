package rok.board.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rok.board.comment.entity.Comment;
import rok.board.comment.entity.CommentPath;
import rok.board.comment.entity.CommentV2;
import rok.board.comment.repository.CommentRepository;
import rok.board.comment.repository.CommentRepositoryV2;
import rok.board.comment.service.request.CommentCreateRequest;
import rok.board.comment.service.request.CommentCreateRequestV2;
import rok.board.comment.service.response.CommentPageResponse;
import rok.board.comment.service.response.CommentResponse;
import rok.board.common.snowflake.Snowflake;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private final CommentRepositoryV2 commentRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public CommentResponse create(CommentCreateRequestV2 req){
        CommentV2 parent = findParent(req);
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 comment = commentRepository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        req.getContent(),
                        req.getArticleId(),
                        req.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(req.getArticleId(), parentCommentPath.getPath())
                                        .orElse(null)
                        )
                )
        );
        return CommentResponse.from(comment);
    }

    private CommentV2 findParent(CommentCreateRequestV2 req) {

        String parentPath = req.getParenPath();
        if(parentPath == null) {
            return null;
        }
        return commentRepository.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return  CommentResponse.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
                .ifPresent(comment -> {
                    if(hasChildren(comment)){
                        comment.delete();
                    } else {
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(CommentV2 comment) {
        return commentRepository.findDescendantsTopPath(
                comment.getArticleId(),
                comment.getCommentPath().getPath()
        ).isPresent();
    }

    private void delete(CommentV2 comment) {
        commentRepository.delete(comment);
        if(!comment.isRoot()) {
            commentRepository.findByPath(
                            comment.getCommentPath().getParentPath()
                    )
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }
}