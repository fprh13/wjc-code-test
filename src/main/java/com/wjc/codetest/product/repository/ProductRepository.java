package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
        [문제] 파라미터의 의미가 명확하지 않습니다.

        [원인] category로 조회하는 메서드임에도 불구하고 파라미터 이름이 name으로 되어 있어 혼란을 줍니다.

        [개선안]
            대안:
                파라미터명을 String name에서 String category로 변경합니다.

            선택 근거:
                동작에는 문제가 없지만, 이러한 작은 불일치가 다른 개발자에게 혼동을 줄 수 있습니다.
                정확한 네이밍은 코드의 가독성과 협업 효율성을 높입니다.
    */
    Page<Product> findAllByCategory(String name, Pageable pageable);


    /*
        [문제] 카테고리 책임 문제

        [원인]
            Product가 카테고리 책임까지 가지고 있습니다.
            카테고리 목록 조회 시 Product에 대한 DISTINCT를 빈번하게 사용하게 됩니다.
            즉, 책임이 섞여 있어서 불필요한 연산이 생기고 있습니다.

        [개선안]
            대안:
                카테고리를 별도 테이블로 분리하거나, 카테고리 리스트를 별도로 관리합니다.

            트레이드오프:
                카테고리를 별도 테이블로 분리하면, 카테고리 목록 조회의 역할이 명확해집니다.
                그리고, 추후에 대, 중, 소 카테고리로 분리하는 등 확장성이 높아집니다.
                하지만, 매번 상품과 카테고리 테이블을 조인하여 처리하는것은 오히려 비효율적인 설계가 될 수 있습니다.

                서비스 구조상 카테고리가 거의 변경되지 않는다면
                매번 DISTINCT 조회를 수행하기보다는 Enum으로 관리하거나 캐싱 처리를 진행해서
                카테고리 리스트 조회 부담을 줄여주는게 좋은 선택일것 입니다.

                이렇게 두가지 방법 모두 장단점이 존재합니다.
                "우리 서비스에서 카테고리를 어떻게 가져갈 것인가?"를 고민해야합니다.

            선택 근거:
                "3정규형 이행 함수 종속을 제거한다"

                현재 category는 PK가 아닌 일반 필드 name에 종속하고 있습니다.
                3정규형을 만족하지 못하는 상황입니다.
                물론 Product의 name을 유니크한 후보키로 가져간다면, 3정규형에 부합하게 됩니다.

                그러나 정규화를 철저히 적용하더라도 DISTINCT 조회는 여전히 불가피합니다.

                따라서 이 문제는 정규화의 문제가 아니라, 서비스 내에서 카테고리의 책임을 어떻게 정의할 것인가의 문제라고 생각합니다.
                앞으로 우리 서비스가 카테고리를 어떻게 유지 시킬것인지 논의하여, 서비스 구조에 맞게 카테고리 책임을 분리하는것이 좋다고 생각합니다.
                ex)
                    카테고리 확장이 예상된다면 별도의 테이블로 분리
                    변경 가능성이 거의 없다면 현재 구조를 유지하면서 카테고리 목록을 별도로 관리
     */
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
