package com.wjc.codetest.product.model.response;

import com.wjc.codetest.product.model.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
    [문제] 응답 Dto의 책임이 명확하지 않습니다.

    [원인]
        가변 상태로 설계되어, "응답 Dto는 완성된 응답 데이터를 전달한다"라는 역할을 수행하지 못하고 있습니다.
        즉, 단일 책임 원칙이 지켜지지 않았습니다.

    [개선안]
        대안:
            Setter를 제거하고, 각 필드를 final로 선언하거나 record를 활용해 불변하게 설계합니다.

        트레이드오프:
            필드에 final 키워드를 통해 불변한 객체를 구성하는 방식은 개발자들에게 익숙하고 이해하기 쉬운 방법입니다.
            해당 방식의 반복되는 코드를 줄이기 위해 record가 등장했습니다.

            record를 사용하면 더 적은 보일러플레이트 코드와 더 적은 바이트코드 생성한다는 이점을 얻을 수 있습니다.
            하지만, 아직 일부 개발자들에게는 낯설게 느껴질 수 있습니다.

            결국 중요한 것은 팀의 컨벤션입니다.
            어떤 Dto 설계 방식을 선택할지 팀 내에서 컨벤션을 정하는게 좋습니다.
            record 사용에 익숙하지 않은 개발자에게는 오히려 생산성이 떨어질 수 있으니 팀 상황에 맞는 방식을 선택하는 것이 좋습니다.

        선택 근거:
            응답 Dto는 컨트롤러에 응답 데이터를 전달하는 역할까지만 책임져야 합니다.
            응답 Dto가 데이터를 전달한 이후 수정할 수 있다면, 그 책임이 흐려지기 떄문에, 단일 책임 원칙에 어긋나게 됩니다.
 */
/**
 * <p>
 *
 * </p>
 *
 * @author : 변영우 byw1666@wjcompass.com
 * @since : 2025-10-27
 */
@Getter
@Setter
public class ProductListResponse {
    private List<Product> products;
    private int totalPages;
    private long totalElements;
    private int page;

    public ProductListResponse(List<Product> content, int totalPages, long totalElements, int number) {
        this.products = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.page = number;
    }
}
