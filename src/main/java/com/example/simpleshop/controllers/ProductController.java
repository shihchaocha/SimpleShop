package com.example.simpleshop.controllers;

import com.example.simpleshop.models.*;
import com.example.simpleshop.repo.ProductRepository;
import com.example.simpleshop.repo.ProductTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="api/v1")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTagRepository productTagRepository;

    @RequestMapping(value="/product",method = RequestMethod.GET)
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products= productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @RequestMapping(value="/productbytag",method = RequestMethod.GET)
    public ResponseEntity<List<Product>> getAllProductsByTag(@RequestParam String tag) {
        List<Product> products = productRepository.findByTag(tag);
        if(products!=null) {
            return ResponseEntity.ok(products);
        } else {
            return (ResponseEntity<List<Product>>) ResponseEntity.notFound();
        }
    }

    @RequestMapping(value="/product", method = RequestMethod.POST)
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product newProduct = productRepository.save(product);
        return ResponseEntity.ok(newProduct);
    }

    @RequestMapping(value="/product/{productsku}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable("productsku") String sku) {
        if (productRepository.existsById(sku))
            productRepository.deleteById(sku);
        return ResponseEntity.ok(new MessageResponse(200,"delete success"));
    }

    @RequestMapping(value="/tag/{tagid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteProductTag(@PathVariable("tagid") Integer iid) {
        if(productTagRepository.existsById(iid))
            productTagRepository.deleteById(iid);
        return ResponseEntity.ok(new MessageResponse(200,"delete success"));
    }


    @RequestMapping(value="/product/{productsku}", method = RequestMethod.POST)
    public ResponseEntity<ProductTag> addProductTag(@PathVariable("productsku") String sku, @RequestParam String tag) {
        Optional<Product> product = productRepository.findById(sku);
        if(product.isPresent()) {
            Product p = product.get();
            if(p.getItems()==null) {
                p.setItems(new ArrayList<ProductTag>());
            }
            List<ProductTag> items = p.getItems();
            for(ProductTag pt: items) {
                if(pt.getTag().equals(tag))
                    return ResponseEntity.ok(pt);
            }
            ProductTag pt = new ProductTag();
            pt.setProduct(p);
            pt.setTag(tag);
            ProductTag newProductTag = productTagRepository.save(pt);
            return ResponseEntity.ok(newProductTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
