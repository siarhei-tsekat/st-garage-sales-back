package org.garagesale.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.garagesale.exception.ApiException;
import org.garagesale.exception.ResourceNotFoundException;
import org.garagesale.model.AppUser;
import org.garagesale.model.Image;
import org.garagesale.model.Product;
import org.garagesale.payload.ImageDTO;
import org.garagesale.payload.ProductDTO;
import org.garagesale.payload.ProductResponse;
import org.garagesale.payload.ProductUpdateDTO;
import org.garagesale.repository.AppUserRepository;
import org.garagesale.repository.ImageRepository;
import org.garagesale.repository.ProductRepository;
import org.garagesale.security.AuthUser;
import org.garagesale.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    @Autowired
    public AuthUtil authUtil;

    @Autowired
    public ModelMapper modelMapper;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public AppUserRepository appUserRepository;

    @Autowired
    private ImageRepository imageRepository;

    private final Validator validator;

    public ProductService() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public ProductDTO addProduct(ProductDTO productDTO) {

        AuthUser authUser = authUtil.loggedInUser();

        AppUser appUser = appUserRepository.findById(authUser.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "user_id", authUser.getUserId()));

        Product product = modelMapper.map(productDTO, Product.class);
        product.setAppUser(appUser);

        Product savedProduct = productRepository.save(product);

        //imageRepository.save();

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public ProductDTO addProductWithImages(ProductDTO productDTO, List<MultipartFile> files) {

        validateProduct(productDTO);
        AuthUser authUser = authUtil.loggedInUser();

        AppUser appUser = appUserRepository.findById(authUser.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "user_id", authUser.getUserId()));

        Product product = modelMapper.map(productDTO, Product.class);
        product.setAppUser(appUser);

        Product savedProduct = productRepository.save(product);

        try {

            List<Image> images = new ArrayList<>();

            for (MultipartFile file : files) {

                Image image = new Image();
                image.setProduct(savedProduct);
                image.setName(file.getOriginalFilename());
                image.setType(file.getContentType());
                image.setData(file.getBytes());
                images.add(image);

            }

            imageRepository.saveAll(images);
            savedProduct.setImages(images);

            productRepository.save(savedProduct);
            return modelMapper.map(savedProduct, ProductDTO.class);

        } catch (IOException e) {
            throw new ApiException(e);
        }

    }

    public ImageDTO uploadProductImage(Long productId, MultipartFile multipartFile) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        try {

            Image image = new Image();
            image.setProduct(product);
            image.setName(multipartFile.getOriginalFilename());
            image.setType(multipartFile.getContentType());
            image.setData(multipartFile.getBytes());

            Image savedImage = imageRepository.save(image);

            // product.getImages().add(savedImage);

            return modelMapper.map(savedImage, ImageDTO.class);

        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    public void validateProduct(ProductDTO productDTO) {
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(productDTO);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<ProductDTO> violation : violations) {
                System.out.println("Validation error: " + violation.getMessage());
            }
            throw new IllegalArgumentException("Validation failed");
        }
    }

    public ProductResponse getAllUserProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        AuthUser authUser = authUtil.loggedInUser();

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> userProducts = productRepository.findProductsByAppUserId(authUser.getUserId(), pageDetails);
        List<ProductDTO> productDTOS = new ArrayList<>();

        for (Product userProduct : userProducts.getContent()) {

            ProductDTO productDTO = new ProductDTO();
            productDTO.setQuantity(userProduct.getQuantity());
            productDTO.setProductName(userProduct.getProductName());
            productDTO.setProductId(userProduct.getProductId());
            productDTO.setPrice(userProduct.getPrice());
            productDTO.setDescription(userProduct.getDescription());
            productDTO.setSpecialPrice(userProduct.getSpecialPrice());

            List<String> imagesUrl = new ArrayList<>();

            for (Image image : userProduct.getImages()) {

//                String uriString = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").path(image.getImageId().toString()).toUriString();
                String imageEncoded = "data:image/png;base64," + Base64.getEncoder().encodeToString(image.getData());
                imagesUrl.add(imageEncoded);

            }
            productDTO.setImages(imagesUrl);
            productDTOS.add(productDTO);
        }

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProducts(productDTOS);
        productResponse.setPageNumber(userProducts.getNumber());
        productResponse.setPageSize(userProducts.getSize());
        productResponse.setTotalElements(userProducts.getTotalElements());
        productResponse.setTotalPages(userProducts.getTotalPages());
        productResponse.setLastPage(userProducts.isLast());

        return productResponse;

    }

    public ProductDTO deleteProduct(Long productId) {
        AuthUser authUser = authUtil.loggedInUser();

        Product product = productRepository.findProductByAppUserIdAndProductId(authUser.getUserId(), productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //imageRepository.

        productRepository.delete(product);

        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO updateProduct(Long productId, ProductUpdateDTO productUpdateDTO) {
        AuthUser authUser = authUtil.loggedInUser();

        Product productFromDb = productRepository.findProductByAppUserIdAndProductId(authUser.getUserId(), productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productUpdateDTO, Product.class);

        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setPrice(product.getPrice());

        productRepository.save(productFromDb);

        return modelMapper.map(productFromDb, ProductDTO.class);
    }
}
