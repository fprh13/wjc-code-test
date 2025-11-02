package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/*
    [문제] Entity의 Setter로 인해 가독성과 유지보수성이 저하됩니다.

    [원인] Setter 메서드가 외부에 공개되어 있어, 외부 코드가 객체의 상태를 언제든지 변경할 수 있게 된 것이 원인입니다.

    [개선안]
        대안:
            @Setter를 제거하고, 의도를 명확히 드러내는 의미 있는 메서드를 사용합니다.
            ex)
                public void update(String name, String category) {
                    this.name = name;
                    this.category = category;
                }

        트레이드오프:
            Setter는 가장 간단한 수정 방식입니다.
            IDE 단축키나 Lombok 어노테이션으로 손쉽게 생성할 수 있지만,
            외부에서 무분별하게 사용하면 유지보수성이 떨어집니다.

            Setter보다 번거롭더라도, 명확한 의도를 가진 메서드명을 사용해 업데이트 로직을 표현하는 것이 좋습니다.

        선택 근거:
            setter는 개발 시 편리하지만, 외부에서 객체의 상태를 언제든지 변경할 수 있기 때문에
            객체의 일관성을 해치는 위험이 존재합니다.

            setter 대신 의미 있는 메서드를 사용하면 코드의 의도가 명확해지기 때문에
            협업 시 메서드명만 보더라도 어떤 변경이 일어나는지 쉽게 이해할 수 있어 가독성과 유지보수성이 향상됩니다.
*/

/*
    [문제] null인 컬럼 값이 저장됩니다.

    [원인] @Column에 nullable = false 같은 메타데이터 설정이 누락되어 있습니다.

    [개선안]
        대안:
            메타데이터를 추가하여 컬럼에 제약 조건을 걸어줍니다.

        선택 근거:
            이름이 없는 상품은 존재할 수 없습니다. 따라서 name 컬럼에는 nullable = false 설정이 필요합니다.
 */
@Entity
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /*
        [문제] Getter 중복 문제

        [원인] Lombok의 @Getter는 컴파일 시 자동으로 각 필드의 Getter 메서드를 생성합니다, 따라서 수동으로 작성한 Getter와 중복됩니다.

        [개선안]
            대안:
                Lombok의 @Getter를 유지하고, 수동으로 작성된 Getter 메서드를 삭제합니다.

            트레이드 오프:
                Lombok에 대한 의존성이 생기지만, 가독성과 개발 편의성을 높혀줍니다.

            선택 근거:
                Lombok은 널리 사용되는 라이브러리이기 때문에, Getter 정도는 가독성과 개발 편의성 측면에서 사용하는 것이 효율적이라고 생각합니다.
    */
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
