package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/*
    [문제] 요청 데이터를 받을 수 없습니다.

    [원인] 기본 생성자가 없으면 스프링이 UpdateProductRequest 객체를 생성할 수 없습니다.

    [개선안]
        대안:
            기본 생성자를 작성합니다.

        선택 근거:
            스프링은 요청 데이터를 객체로 변환할 때 먼저 기본 생성자를 통해 객체를 생성한 뒤 필드값을 세팅합니다.
            기본 생성자가 없으면 객체 생성이 불가능하기 때문에 오류를 반환합니다.
 */
@Getter
@Setter
public class UpdateProductRequest {
    private Long id;
    private String category;
    private String name;

    public UpdateProductRequest(Long id) {
        this.id = id;
    }

    public UpdateProductRequest(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public UpdateProductRequest(Long id, String category, String name) {
        this.id = id;
        this.category = category;
        this.name = name;
    }
}

