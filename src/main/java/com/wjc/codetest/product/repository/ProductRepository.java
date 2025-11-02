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


    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
