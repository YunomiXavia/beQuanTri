package com.example.beQuanTri.entity.product;

import com.example.beQuanTri.entity.category.Category;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[products]")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String productName;

    @Column(nullable = false)
    double price = 0.0;

    @Column(length = 500)
    String description;

    // San pham ton kho de ban
    @Column(nullable = false)
    int stock = 1;

    @Column(nullable = false)
    int subscriptionDuration;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(nullable = false, unique = true)
    String productCode;

    String originalImageName;

    @PrePersist
    public void generateProductCode() {
        String categoryCode = category.getName().substring(0, 3).toUpperCase();
        String randomSuffix = String.valueOf(System.currentTimeMillis()).substring(8);
        this.productCode = categoryCode + "-" + randomSuffix;
    }
}
