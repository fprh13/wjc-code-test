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