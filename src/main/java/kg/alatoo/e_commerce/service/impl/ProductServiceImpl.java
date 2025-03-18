package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.category.CategoryRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.request.ProductRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.entity.*;
import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.enums.Tag;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.exception.NotFoundException;
import kg.alatoo.e_commerce.mapper.ProductMapper;
import kg.alatoo.e_commerce.repository.CartRepository;
import kg.alatoo.e_commerce.repository.CategoryRepository;
import kg.alatoo.e_commerce.repository.ProductRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.AuthService;
import kg.alatoo.e_commerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ProductMapper productMapper;
    private final CartRepository cartRepository;

    @Transactional
    @Override
    public void addNewProduct(ProductRequest productRequest, String token) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.WORKER)) {
            throw new BadRequestException("You do not have permission to add products.");
        }

        Category category = categoryRepository.findByName(productRequest.getCategory())
                .orElseThrow(() -> new BadRequestException("Category '" + productRequest.getCategory() + "' does not exist."));

        Product product = new Product();
        product.setTitle(productRequest.getTitle());
        product.setCode(productRequest.getCode());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());

        product.setColors(productRequest.getColors().stream()
                .map(Color::valueOf)
                .collect(Collectors.toList()));

        product.setTags(productRequest.getTags().stream()
                .map(Tag::valueOf)
                .collect(Collectors.toList()));

        product.setProductSizes(productRequest.getSizes().stream()
                .map(ProductSize::valueOf)
                .collect(Collectors.toList()));

        product.setQuantity(Math.max(productRequest.getQuantity(), 0));
        product.setCategory(category);
        categoryRepository.save(product.getCategory());
        category.getProducts().add(product);
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void addNewCategory(String token, CategoryRequest categoryRequest) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.WORKER)) {
            throw new BadRequestException("You do not have permission to add categories.");
        }

        if (categoryRepository.findByName(categoryRequest.getName()).isPresent()) {
            throw new BadRequestException("Category already exists.");
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepository.save(category);
    }

    @Transactional
    @Override
    public void update(String token, Long productId, ProductRequest request) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.WORKER)) {
            throw new BadRequestException("You do not have permission to update products.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Invalid product ID: " + productId));

        if (request.getCategory() != null) {
            Category category = categoryRepository.findByName(request.getCategory())
                    .orElseThrow(() -> new BadRequestException("Category does not exist."));
            product.setCategory(category);
        }

        Optional.ofNullable(request.getCode()).ifPresent(product::setCode);
        Optional.ofNullable(request.getSizes()).ifPresent(sizes ->
                product.setProductSizes(sizes.stream()
                        .map(ProductSize::valueOf)
                        .collect(Collectors.toList()))
        );
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getTags()).ifPresent(tags ->
                product.setTags(tags.stream()
                        .map(Tag::valueOf)
                        .collect(Collectors.toList()))
        );
        Optional.ofNullable(request.getColors()).ifPresent(colors ->
                product.setColors(colors.stream()
                        .map(Color::valueOf)
                        .collect(Collectors.toList()))
        );
        Optional.ofNullable(request.getTitle()).ifPresent(product::setTitle);

        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new BadRequestException("Invalid quantity value.");
        }
        product.setQuantity(request.getQuantity());

        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);

        productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        List<Product> allProducts = productRepository.findAll();
        return productMapper.toDtoS(allProducts);
    }

    @Override
    public ProductDetailsResponse showById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with ID " + id + " not found!", HttpStatus.NOT_FOUND));

        return productMapper.toDetailDto(product);
    }

    @Transactional
    @Override
    public void deleteProduct(String token, Long productId) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.WORKER)) {
            throw new BadRequestException("You do not have permission to delete products.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found!", HttpStatus.NOT_FOUND));

//        List<Cart> cartsWithProduct = cartRepository.findAllByProductsListContaining(product);
//        for (Cart cart : cartsWithProduct) {
//            cart.getProductsList().remove(product);
//            cartRepository.save(cart);
//        }
        product.getCategory().getProducts().remove(product);
        product.setCategory(null);
        categoryRepository.save(product.getCategory());
        productRepository.delete(product);
    }

}

