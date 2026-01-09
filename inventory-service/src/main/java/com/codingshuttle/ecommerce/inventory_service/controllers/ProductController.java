package com.codingshuttle.ecommerce.inventory_service.controllers;


import com.codingshuttle.ecommerce.inventory_service.Product.ProductService;
import com.codingshuttle.ecommerce.inventory_service.clients.OrdersFeignClient;
import com.codingshuttle.ecommerce.inventory_service.dto.ProductDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    private final OrdersFeignClient ordersfeignClient;

    @GetMapping("/fetchOrders")
    public String fetchFromOrdersService(HttpServletRequest httpServletRequest){

        log.info(httpServletRequest.getHeader("x-custom-header"));

//        ServiceInstance orderService = discoveryClient
//                .getInstances("order-service")
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Order-service not available"));



//        return restClient.get()
//               .uri(orderService.getUri()+"/orders/core/helloOrders")
//               .retrieve().body(String.class);

        return ordersfeignClient.helloOrders();

    }

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
