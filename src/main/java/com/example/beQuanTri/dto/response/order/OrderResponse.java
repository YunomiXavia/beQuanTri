package com.example.beQuanTri.dto.response.order;

import com.example.beQuanTri.dto.basic.collaborator.CollaboratorBasicInfo;
import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.example.beQuanTri.entity.user.AnonymousUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    String id;
    UserBasicInfo user;
    AnonymousUser anonymousUser;
    CollaboratorBasicInfo collaborator;
    String statusName;
    List<OrderItemsResponse> orderItems;
    double totalAmount;
    Date orderDate;
    Date startDate;
    Date endDate;
    String referralCodeUsed;
}
