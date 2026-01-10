package com.codingshuttle.ecommerce.inventory_service.Product;

import com.codingshuttle.ecommerce.inventory_service.dto.OrderRequestDto;
import com.codingshuttle.ecommerce.inventory_service.dto.OrderRequestItemDto;
import com.codingshuttle.ecommerce.inventory_service.dto.ProductDto;
import com.codingshuttle.ecommerce.inventory_service.entity.Product;
import com.codingshuttle.ecommerce.inventory_service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductDto> getAllInventory(){
        log.info("Fetching all Inventory items");
        List<Product> inventories=productRepository.findAll();
        return inventories
                .stream()
                .map((element) -> modelMapper.map(element, ProductDto.class))
                .collect(Collectors.toList());
    }
    public ProductDto getProductById(Long id){
        log.info("Fetching Product with id:{}",id);
        Optional<Product> inventory = productRepository.findById(id);
      return  inventory.map((element) -> modelMapper.map(element, ProductDto.class))
                .orElseThrow(()->new RuntimeException("Inventory Not found"));
    }

    @Transactional
    public Double reduceStocks(OrderRequestDto orderRequestDto) {
        log.info("Reducing stocks");
        Double price=0.0;
        for(OrderRequestItemDto orderRequestItemDto:orderRequestDto.getItems()){
            Long productId= orderRequestItemDto.getProductId();
            Integer quantity= orderRequestItemDto.getQuantity();

          Product product=  productRepository.findById(productId)
                  .orElseThrow(()->new RuntimeException("Product with id not found"));

          if(product.getStock()<quantity){
              throw new RuntimeException("Product cannot be fulfilled for given quantity");
          }
          product.setStock(product.getStock()-quantity);
            productRepository.save(product);
            price+=quantity*product.getPrice();
        }
        return price;
    }
}
