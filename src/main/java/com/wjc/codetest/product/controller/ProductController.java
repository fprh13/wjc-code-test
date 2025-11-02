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

    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}