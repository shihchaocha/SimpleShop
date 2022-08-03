package com.example.simpleshop.services;

import com.example.simpleshop.models.MessageResponse;
import com.example.simpleshop.models.Product;
import com.example.simpleshop.models.ProductTag;
import com.example.simpleshop.repo.ProductRepository;
import com.example.simpleshop.repo.ProductTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTagRepository productTagRepository;

    public List<Product> getAllProducts()  {
        List<Product> products = productRepository.findAll();
        return products;
    }

    public Product getProductbyID(String sku) {
        Optional<Product> product = productRepository.findById(sku);
        if(product.isPresent())
            return product.get();
        return null;
    }


    public List<Product> getAllProductsByTag(String tag) {
        List<Product> products = productRepository.findByTag(tag);
        if(products!=null) {
            return products;
        } else {
            return null;
        }
    }

    public Product addProduct(Product product) {
        Product newProduct = productRepository.save(product);
        return newProduct;
    }

    public void deleteProduct(String sku) {
        if (productRepository.existsById(sku))
            productRepository.deleteById(sku);
    }

    public void deleteProductTag(Integer iid) {
        if (productTagRepository.existsById(iid))
            productTagRepository.deleteById(iid);
    }

    public ProductTag addProductTag(String sku, String tag) {
        Optional<Product> product = productRepository.findById(sku);
        if(product.isPresent()) {
            Product p = product.get();
            if(p.getItems()==null) {
                throw new RuntimeException("DB access error");
            }
            List<ProductTag> items = p.getItems();
            for(ProductTag pt: items) {
                if(pt.getTag().equals(tag))
                    return pt;
            }
            ProductTag pt = new ProductTag();
            pt.setProduct(p);
            pt.setTag(tag);
            ProductTag newProductTag = productTagRepository.save(pt);
            return newProductTag;
        } else {
            return null;
        }
    }
}
