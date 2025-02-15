package com.codehows.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDto {
    private int page; //현재 페이지
    private int recordSize; //페이지당 출력할 데이터 개수
    private int pageSize; // 화면 하단에 출력할 페이지 사이즈
    private String searchBy; // 검색 키워드
    private String searchDataType; // 검색 유형
    private String searchQuery = "";
}
