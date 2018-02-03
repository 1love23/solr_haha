package com.lei.service.impl;

import com.lei.pojo.Product;
import com.lei.pojo.Result;
import com.lei.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private HttpSolrClient httpSolrClient;

    @Override
    public Result queryProduct(String queryString, String catalog_name, String price, int page, int sort) throws Exception {
        //创建SolrQuery对象
        SolrQuery solrQuery = new SolrQuery();
        if(StringUtils.isNoneBlank(queryString)){
            //设置查询关键词
            solrQuery.setQuery(queryString);
        }else {
            solrQuery.setQuery("*:*");
        }
        //设置默认域
        solrQuery.set("df","product_name");
        //设置商品类名过滤条件
        //设置商品分类名称
        if(StringUtils.isNoneBlank(catalog_name)){
            catalog_name = "product_catalog_name:"+catalog_name;
        }
        //设置商品价格
        if(StringUtils.isNoneBlank(price)){
            String[] split = price.split("-");
            if(split != null && split.length==2){
                price = "product_price:["+split[0]+" TO "+split[1]+"]";
            }
        }
        solrQuery.setFilterQueries(catalog_name,price);
        //排序 1.降序 0.升序
        if(sort == 1){
            solrQuery.setSort("product_price", SolrQuery.ORDER.desc);
        }else {
            solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
        }
        //查询分页
        int size = 20;
        solrQuery.setStart((page-1)*size);
        solrQuery.setRows(size);
        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("product_name");
        solrQuery.setHighlightSimplePre("<font color=\"red\">");
        solrQuery.setHighlightSimplePost("</font>");
        //查询数据
        QueryResponse response = httpSolrClient.query(solrQuery);
        SolrDocumentList results = response.getResults();
        //获得总数据条数
        long numFound = results.getNumFound();
        //获取高亮数据
        Map<String, Map<String, List<String>>> highMap = response.getHighlighting();
        //解析结果集存放到Product中
        List<Product> productList = new ArrayList<Product>();
        for (SolrDocument result : results) {
            Product product = new Product();
            //商品ID
            product.setPid(result.get("id").toString());
            //商品标题设置高亮
            List<String> highList = highMap.get(result.get("id")).get("product_name");
            if(highList != null && highList.size() > 0){
                product.setName(highList.get(0));
            }else {
                product.setName(result.get("product_name").toString());
            }
            //商品价格
            product.setPrice(result.get("product_price").toString());
            //商品图片
            product.setPicture(result.get("product_picture").toString());

            productList.add(product);
        }
        //封装返回对象
        Result result = new Result();
        result.setCurPage(page);
        result.setRecordCount(numFound);
        result.setProductList(productList);
        result.setPageCount((int) ((numFound%size)==0? (numFound/size) : (numFound/size)+1));
        return result;
    }
}
