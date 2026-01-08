package com.codingshuttle.ecommerce.inventory_service.controllers;


import com.codingshuttle.ecommerce.inventory_service.Product.ProductService;
import com.codingshuttle.ecommerce.inventory_service.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllInventory(){
        List<ProductDto> inventories=productService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }
    @GetMapping("/{id}")
    public  ResponseEntity<ProductDto> getInventoryById(@PathVariable Long id){
        ProductDto inventory= productService.getProductById(id);
        return ResponseEntity.ok(inventory);
    }

}
