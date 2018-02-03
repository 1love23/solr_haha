package com.lei.service;

import com.lei.pojo.Result;

public interface ProductService {

    Result queryProduct(String queryString, String catalog_name, String price, int page, int sort) throws Exception;
}
