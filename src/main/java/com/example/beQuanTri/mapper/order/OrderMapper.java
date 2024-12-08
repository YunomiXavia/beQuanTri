package com.example.beQuanTri.mapper.order;

import com.example.beQuanTri.dto.basic.collaborator.CollaboratorBasicInfo;
import com.example.beQuanTri.dto.basic.product.ProductBasicInfo;
import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.example.beQuanTri.dto.response.order.OrderItemsResponse;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.order.OrderItems;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "collaborator", source = "collaborator")
    @Mapping(target = "statusName", source = "status.statusName")
    @Mapping(target = "orderItems", source = "orderItems")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "product", source = "product")
    OrderItemsResponse toOrderItemsResponse(OrderItems orderItems);

    default OrderResponse toOrderResponseByRole(Order order, String role) {
        OrderResponse response = toOrderResponse(order);

        // If role is Collaborator, filter fields
        if ("ROLE_Collaborator".equalsIgnoreCase(role)) {
            if(response.getUser() != null) {
                response.getUser().setId(null); // Hide user ID
            }
            if (response.getCollaborator() != null) {
                response.getCollaborator().setId(null); // Hide collaborator ID
            }
            if (response.getAnonymousUser() != null) {
                response.getAnonymousUser().setId(null); // Hide anonymous user ID
            }
            if (response.getOrderItems() != null) {
                response.getOrderItems().forEach(item -> {
                    item.setId(null); // Hide order item ID
                    if (item.getProduct() != null) {
                        item.getProduct().setId(null); // Hide product ID
                    }
                });
            }
        }  else if ("ROLE_User".equalsIgnoreCase(role)) {
            response.setCollaborator(null);
            response.setAnonymousUser(null);
            if (response.getOrderItems() != null) {
                response.getOrderItems().forEach(item -> {
                    item.setId(null); // Hide order item ID
                    if (item.getProduct() != null) {
                        item.getProduct().setId(null); // Hide product ID
                    }
                });
            }
        }  else if ("ROLE_Admin".equalsIgnoreCase(role)) {
            return response;
        } else {
            response.setUser(null);
            response.setCollaborator(null);
            if (response.getOrderItems() != null) {
                response.getOrderItems().forEach(item -> {
                    item.setId(null); // Hide order item ID
                    if (item.getProduct() != null) {
                        item.getProduct().setId(null); // Hide product ID
                    }
                });
            }
        }

        return response;
    }

    default UserBasicInfo toUserBasicInfo(User user) {
        if (user == null) return null;
        return new UserBasicInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    default ProductBasicInfo toProductBasicInfo(Product product) {
        if (product == null) return null;
        return ProductBasicInfo.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .build();
    }

    default CollaboratorBasicInfo toCollaboratorBasicInfo(Collaborator collaborator) {
        if (collaborator == null) return null;
        return new CollaboratorBasicInfo(
                collaborator.getId(),
                toUserBasicInfo(collaborator.getUser()),
                collaborator.getTotalOrdersHandled()
        );
    }
}
