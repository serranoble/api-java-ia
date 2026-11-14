package com.qa.practice.products.service;

import com.qa.practice.products.dto.PagedResponse;
import com.qa.practice.products.dto.ProductCreateRequest;
import com.qa.practice.products.dto.ProductResponse;
import com.qa.practice.products.dto.ProductUpdateRequest;
import com.qa.practice.products.exception.ProductAlreadyExistsException;
import com.qa.practice.products.exception.ProductNotFoundException;
import com.qa.practice.products.model.Product;
import com.qa.practice.products.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ProductResponse getByGtin(String gtin) {
        log.info("Consultando producto por GTIN: gtin={}", gtin);
        Product product = productRepository.findById(gtin)
                .orElseThrow(() -> new ProductNotFoundException(gtin));
        return ProductResponse.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAll(Pageable pageable) {
        log.info("Listando productos paginados: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<ProductResponse> page = productRepository.findAll(pageable)
                .map(ProductResponse::fromEntity);
        return PagedResponse.fromPage(page);
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        log.info("Creando producto: gtin={}, name={}", request.gtin(), request.name());
        if (productRepository.existsById(request.gtin())) {
            throw new ProductAlreadyExistsException(request.gtin());
        }
        Product product = new Product(
                request.gtin(),
                request.name().trim(),
                request.description() != null ? request.description().trim() : null,
                request.price()
        );
        Product saved = productRepository.save(product);
        log.info("Producto creado exitosamente: gtin={}", saved.getGtin());
        return ProductResponse.fromEntity(saved);
    }

    @Transactional
    public ProductResponse update(String gtin, ProductUpdateRequest request) {
        log.info("Actualizando producto: gtin={}", gtin);
        Product product = productRepository.findById(gtin)
                .orElseThrow(() -> new ProductNotFoundException(gtin));

        product.setName(request.name().trim());
        product.setDescription(request.description() != null ? request.description().trim() : null);
        product.setPrice(request.price());

        Product updated = productRepository.save(product);
        log.info("Producto actualizado exitosamente: gtin={}", updated.getGtin());
        return ProductResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(String gtin) {
        log.info("Eliminando producto: gtin={}", gtin);
        if (!productRepository.existsById(gtin)) {
            throw new ProductNotFoundException(gtin);
        }
        productRepository.deleteById(gtin);
        log.info("Producto eliminado exitosamente: gtin={}", gtin);
    }
}
