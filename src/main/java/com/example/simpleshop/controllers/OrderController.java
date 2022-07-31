package com.example.simpleshop.controllers;

import com.example.simpleshop.models.LineItem;
import com.example.simpleshop.models.MessageResponse;
import com.example.simpleshop.models.Product;
import com.example.simpleshop.models.SaleOrder;
import com.example.simpleshop.repo.LineItemRepository;
import com.example.simpleshop.repo.ProductRepository;
import com.example.simpleshop.repo.SaleOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="api/v1")
public class OrderController {
    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(value="/order",method = RequestMethod.GET)
    public ResponseEntity<List<SaleOrder>> getAllOrders() {
        List<SaleOrder> orders= saleOrderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @RequestMapping(value="/order",method = RequestMethod.POST)
    public ResponseEntity<SaleOrder> createOrders(@RequestBody SaleOrder saleOrder) {
        SaleOrder generatedOrder = saleOrderRepository.save(saleOrder);
        return ResponseEntity.ok(generatedOrder);
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.GET)
    public ResponseEntity<SaleOrder> getOrder(@PathVariable("orderid") Integer orderid) {
        Optional<SaleOrder> order = saleOrderRepository.findById(orderid);
        if(order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.POST)
    public ResponseEntity<LineItem> createOrderLineItem(@PathVariable("orderid") Integer orderid, @RequestBody LineItem lineItem) {
        if(lineItem==null)
            return ResponseEntity.badRequest().build();
        Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
        if(temporaryorder.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<Product> temporaryproduct = productRepository.findById(lineItem.getSku());
        if(temporaryproduct.isEmpty())
            return ResponseEntity.notFound().build();
        Product product = temporaryproduct.get();
        if(lineItem.getQuantity()>product.getQuantity())
            return ResponseEntity.badRequest().build();
        lineItem.setSaleOrder(temporaryorder.get());
        LineItem savedLineItem = lineItemRepository.save(lineItem);
        return ResponseEntity.ok(savedLineItem);
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable("orderid") Integer orderid) {
        if(saleOrderRepository.existsById(orderid))
            saleOrderRepository.deleteById(orderid);

        return ResponseEntity.ok(new MessageResponse(200,"delete success"));
    }


    @RequestMapping(value="/order/{orderid}/{lineitemid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrderLineItem(@PathVariable("orderid") Integer orderid, @PathVariable("lineitemid") Integer lineitemid) {
        if(saleOrderRepository.existsById(orderid))
            if(lineItemRepository.existsById(lineitemid))
                lineItemRepository.deleteById(lineitemid);

        return ResponseEntity.ok(new MessageResponse(200,"delete success"));
    }

    @RequestMapping(value="/order/{orderid}/checkout", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> createOrderLineItem(@PathVariable("orderid") Integer orderid) {
        Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
        if(temporaryorder.isPresent()) {
            SaleOrder saleOrder = temporaryorder.get();
            List<LineItem> items = saleOrder.getLineItems();
            for(LineItem item : items) {
                Optional<Product> oproduct = productRepository.findById(item.getSku());
                if(oproduct.isPresent()) {
                    Product product = oproduct.get();
                    int quantity = product.getQuantity();
                    if(quantity>item.getQuantity()) {
                        product.setQuantity(quantity-item.getQuantity());
                        productRepository.save(product);
                    }
                }
            }
            saleOrder.setCheckoutDate(LocalDateTime.now());
            saleOrder.setState("purchased");
            saleOrderRepository.save(saleOrder);
            return ResponseEntity.ok(new MessageResponse(200,"checkouted"));
        } else {
            return ResponseEntity.ok(new MessageResponse(404,"order id not found"));

        }
    }

}

