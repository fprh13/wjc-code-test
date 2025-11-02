package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/*
    [문제] 페이지 조회로 인해 DB 부하에 위험이 있습니다.

    [원인] size 최대값이 제한되지 않아 클라이언트가 매우 큰 값(int 범위인 최대 2147483647)으로 요청할 수 있습니다.

    [개선안]
        대안:
            @Max를 활용해 size에 대해 검증을 적용하거나, size의 기본값 혹은 상한값을 명시합니다.

        선택 근거:
            페이지네이션 목적이라면 일반적으로 한 페이지에 표시할 항목 수는 10개, 50개, 100개 정도라고 예상이 됩니다.
            size를 무제한으로 열어두면 대량 조회로 인해 DB 부하 또는 악의적 요청에 취약해집니다.
            따라서 클라이언트가 정해진 범위 내에서만 요청하도록 상한을 두는 것이 안전합니다.
 */
@Getter
@Setter
public class GetProductListRequest {
    private String category;
    private int page;
    private int size;
}