package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    [문제] Restful하지 않은 API 설계로 일관성과 유지보수성이 저하되었습니다.

    [원인] URI의 구성이 표준적이지 않고, Http 메서드가 적절하지 않습니다.

    [개선안]
        대안:
            1. API의 행위를 올바른 HTTP 메서드로 표현하고, 자원을 중심으로 URI로 구성합니다.
                ex)
                    Get /get/product/by/{productId} -> Get /products/{productId}
                    Post /create/product -> Post /products
                    Post /delete/product/{productId} -> Delete /products/{productId}
                    Post /update/product/{productId} -> Put /products/{productId}

            2. 조회는 GET을 사용하고, 데이터는 바디가 아닌 쿼리 파라미터로 전달합니다.
                ex)
                    Post /product/list -> Get /products
                    @RequestBody GetProductListRequest dto -> @ModelAttribute GetProductListRequest dto

        판단 근거:
            Rest API의 장점은 누구나 쉽게 이해하고 사용할 수 있다는 점입니다.
            Http 메서드로 행위를 표현하고, URI로 자원을 표시하기 때문에 요청만 보아도 동작을 예측할 수 있어 협업에 유리합니다.

            또한, "GET 요청시 바디를 이용하지 않고 쿼리 파라미터를 이용한다" 같은 정해진 설계 원칙을 따르기 때문에,
            모두가 일관된 규칙 아래 개발을 진행 할 수 있어 유지보수성이 높아집니다.
 */

/*
    [문제] 요청 Dto의 검증이 제대로 이루어지지 않습니다.

    [원인] Dto의 필드에 빈 값이나 null이 전달되어도 별도의 검증 없이 로직이 진행되어 잘못된 데이터가 DB까지 도달합니다.

    [개선안]
        대안:
            spring-boot-starter-validation 의존성을 사용합니다.
            @Valid를 통해 요청 Dto의 유효성을 검증을 활성화하고,
            @NotNull, @NotBlank, @Size 등의 어노테이션으로 각 필드 단위로 검증을 진행합니다.

        트레이드오프:
            validation 의존성을 활용하면 사전에 문제 있는 요청 데이터를 판별해 클라이언트에게 적절한 예외를 반환할 수 있습니다.
            @ControllerAdvice를 활용한다면, 어떤 필드에 문제가 있는지도 명확히 전달할 수 있어 협업에 도움이 됩니다.

            다만, 보안 관점에서의 검증 설계는 다르게 접근해야 합니다.
            예를 들어 로그인 요청 시, "아이디는 8자리를 넘을 수 없습니다.", "비밀번호는 특수 문자를 포함합니다.", "비밀번호는 16자리 이하입니다." 같은
            상세한 검증 메시지를 그대로 노출하면 해커에게 유용한 힌트를 제공하게 됩니다.
            따라서 보안에 영향을 주는 요청 데이터의 검증 결과를 노출하지 않도록 조심해야합니다.

        선택 근거:
            값이 비어있는 name이나 category를 가진 Product를 저장할 수 있다면 이것은 바람직하지 않습니다.
            DB 설정으로 제약을 걸었다 하더라도, 쿼리 실행 후 에러를 반환하기보다 사전 검증으로 차단하는 것이 좋습니다.

            "단일 책임의 원칙"
            요청 Dto는 전달받은 데이터를 정상적이고 유효한 상태로 서비스 레이어에 전달할 책임이 있습니다.
            잘못된 데이터를 전달한다면 요청 Dto로서의 역할을 다하지 못한 것이므로, 요청 단계에서 유효성 검증을 수행하는것이 바람직합니다.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    /*
        [문제] 메서드명이 의도와 다릅니다.

        [원인] 복사, 붙여넣기 과정에서 발생한 오타로 판단되고, 현재는 오버로딩으로 인해 정상 동작 중입니다.

        [개선안]
            대안:
                카테고리 목록 조회에 맞는 메서드명으로 변경합니다.
                이처럼 식별하기 어려운 오타나 의도와 다른 메서드명 문제는 발견이 쉽지 않습니다.
                AI 코드 리뷰 봇을 도입하면 PR 단계에서 이러한 오타나 잘못된 메서드명을 자동으로 검증할 수 있습니다.
                또한, 팀원끼리 컨트롤러 메서드명 컨벤션을 정의하여 코드 리뷰 시 확인하는 절차를 거치도록 할 수 있습니다.

            트레이드오프:
                이런 사소한 문제는 리뷰어도 놓치기 쉽지만, AI 코드 리뷰 봇을 사용하면 손쉽게 식별할 수 있어 개발 생산성을 높일 수 있습니다.
                다만, 실무에서는 회사의 보안 정책을 고려해야합니다.

            선택 근거:
                카테고리 목록 조회에 맞는 네이밍으로 변경하면 문제는 해결됩니다.
                하지만 이런 사소한 오타는 코드 리뷰 과정에서 놓치기 쉽습니다.

                기능의 정확성이나 설계 의도를 함께 검토하고, 각자의 의견을 교환하는 과정이 코드리뷰라고 생각합니다.
                개발자가 코드 품질과 설계에 집중할 수 있도록, 이런 사소한 부분을 AI 코드 리뷰 봇이 자동으로 검출해준다면
                개발 생산성이 향상될 것입니다.

                이것이 AI 시대를 맞은 개발자들의 올바른 AI 활용 전략이라고 생각합니다.
     */
    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}