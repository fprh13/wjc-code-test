package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/*
    [문제]
        원자성과 일관성이 깨질 수 있습니다.

    [원인]
        트랜잭션 AOP가 service 레이어에 적용되지 않았습니다.
        여러 command 수행 시 예외가 발생하면 롤백이 되지 않아 일부만 DB에 반영 되는 문제가 생길 수 있습니다.

    [개선안]
        대안:
            command 수행 메서드 레벨에 @Transaction을 설정합니다.

        선택 근거:
            물론, 현재 기능상 문제는 없습니다.
            추후에 있을 확장과 유지보수성을 고려하면
            트랜잭션 범위를 service까지 포함하도록 설정하는 것이 바람직합니다.

            Product가 확장되어 여러 command를 수행한다면,
            일부만 DB에 반영되는 원자성, 일관성이 깨지는 상황이 발생할 수 있습니다.
 */

/*
    [문제] 코드의 신뢰성이 낮습니다.

    [원인] 단위 테스트나 통합 테스트가 작성되지 않아, 로직의 동작을 검증할 수 없습니다.

    [개선안]
        대안:
            1. service <-> repository간 동작을 검증하는 통합 테스트를 작성합니다.
            2. service에 단위 테스트를 작성합니다.

        트레이드오프:
            1. service <-> repository 통합 테스트를 진행하면 실제 환경에 가까워 신뢰도가 높지만 그만큼 비용이 발생합니다.
            2. 단위 테스트는 실제 인프라와 상호작용을 하지 않기 때문에 통합 테스트보다 신뢰도가 상대적으로 낮지만 그만큼 저비용으로 수행가능합니다.

        선택 근거:
            테스트 코드는 코드의 안정성과 변경에 대한 신뢰성을 얻기 위해 필요합니다.
            현재 테스트가 존재하지 않기 때문에 기능 수정이나 추가 시 정상적인 동작을 한다는 보장을 할 수 없습니다.
            유지보수성을 고려해 테스트 코드가 필요합니다.
 */

/*
    [문제] Entity 응답 문제

    [원인] 별도의 응답 Dto없이 Product를 그대로 응답하고 있습니다.

    [개선안]
        대안:
            Product를 응답 Dto로 변환하여 전달합니다.

        선택 근거:
            Entity가 변경되면 응답에 대한 API 스펙이 변경되기 때문에 유지보수성이 떨어집니다.
            또한, 테이블의 구조를 외부에 노출하면, 해커에게 힌트를 제공하게 될 수 있습니다.
            그리고 만약 데이터베이스에 민감 정보가 노출 된다면 보안 사고로 이어질 수 있습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    /*
        [문제] 잘못된 Optional의 활용

        [원인]
            Optional 내부 존재 여부를 isPresent()로 직접 확인한 뒤, 다시 get()으로 값을 꺼내고 있습니다.
            Optional의 활용이 잘못되었으며, 가독성을 떨어뜨리고 있습니다.

        [개선안]
            대안:
                Optional에서 제공하는 메서드를 활용합니다.

            선택 근거:
                Optional의 장점은 NPE를 방지하고 값의 존재 여부에 따라 확실하게 제어를 할 수 있다는 점입니다.
                orElseThrow() 메서드를 사용하면 간결하게 예외 처리가 가능해지기 때문에 가독성도 좋아집니다.
                ex)
                return productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("product not found"));
     */
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    /*
       [문제] 불필요한 order by 문제

       [원인] 특정 카테고리로 조회하고 카테고리 기준으로 정렬을 진행하고 있습니다.

       [개선안]
           대안:
               의미 있는 정렬 기준을 두거나 Sort 조건을 지웁니다.
           선택 근거:
               where category = ? 로 이미 동일한 값만 조회된 상태에서
               order by category를 진행하는것은 논리적인 오류입니다.

               예를 들어, "전자기기" 라는 카테고리를 가진 상품끼리 카테고리 별로 정렬하고 있는것입니다.

       [검증]
           - Sort.by() 제거 전
               select
                   p1_0.product_id, p1_0.category, p1_0.name
               from
                   product p1_0
               where
                   p1_0.category=?
               order by
                   p1_0.category
               fetch
                   first ? rows only

           - Sort.by() 제거 후
               select p1_0.product_id, p1_0.category, p1_0.name
               from
                   product p1_0
               where
                   p1_0.category=?
               fetch
                   first ? rows only
    */
    public Page<Product> getListByCategory(GetProductListRequest dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}