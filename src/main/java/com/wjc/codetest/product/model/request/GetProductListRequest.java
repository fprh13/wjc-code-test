package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProductListRequest {
    private String category;
    private int page;
    private int size;
}