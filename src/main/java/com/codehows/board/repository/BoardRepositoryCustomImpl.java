package com.codehows.board.repository;

import com.codehows.board.dto.SearchDto;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.QBoard;
import com.codehows.board.entity.QHash;
import com.codehows.board.entity.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QBoard board = QBoard.board;
    public final QHash hash = QHash.hash1;
    private final QUser user = QUser.user;

    public BoardRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("title", searchBy)) {
            return board.title.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("writer", searchBy)) {
            return board.writer.uid.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("content", searchBy)) {
            return board.content.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("hash", searchBy)) {
            return hash.hash.like("%" + searchQuery + "%");
        }
        return null;
    }

    // sort 사용
    private OrderSpecifier[] createOrderSpecifier(String sortType, String direction) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (sortType.equals("title")) {
            if (direction.equals("desc")) {
                orderSpecifiers.add(new OrderSpecifier(Order.DESC, board.title));
            } else {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, board.title));
            }
        } else if (sortType.equals("writer")) {
            if (direction.equals("desc")) {
                orderSpecifiers.add(new OrderSpecifier(Order.DESC, board.writer));
            } else {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, board.writer));
            }
        } else if (sortType.equals("regTime")) {
            if (direction.equals("desc")) {
                orderSpecifiers.add(new OrderSpecifier(Order.DESC, board.regTime));
            } else {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, board.regTime));
            }
        } else {
            if (direction.equals("desc")) {
                orderSpecifiers.add(new OrderSpecifier(Order.DESC, board.bno));
            } else {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, board.bno));
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<Board> getBoardPage(SearchDto searchDto, String sortType, String direction, Pageable pageable) {
        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(sortType, direction);

        System.out.println("Search By: " + searchDto.getSearchBy());
        System.out.println("Search Query: " + searchDto.getSearchQuery());

        List<Board> content = queryFactory
                .selectFrom(board)
                .distinct()
                .leftJoin(hash).on(board.bno.eq(hash.bno.bno))
                .where(searchByLike(searchDto.getSearchBy(), searchDto.getSearchQuery()))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 디버깅용 로그 출력
        System.out.println("Offset: " + pageable.getOffset());
        System.out.println("Page Size: " + pageable.getPageSize());
        System.out.println("Content Size: " + content.size());

        Long total = queryFactory
                .select(Wildcard.count)
                .from(board)
                .leftJoin(hash).on(board.bno.eq(hash.bno.bno))
                .where(searchByLike(searchDto.getSearchBy(), searchDto.getSearchQuery()))
                .fetchOne();

        if (total == null) {
            total = 0L;
        }
        return new PageImpl<>(content, pageable, total);
    }
}
