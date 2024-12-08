package com.example.beQuanTri.service.commission;

import com.example.beQuanTri.entity.commission.Commission;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.repository.collaborator.CommissionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage commission-related operations.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CommissionService {

    // Dependency
    CommissionRepository commissionRepository;

    /**
     * Finds a commission record associated with a specific order.
     *
     * @param order the order entity to find the commission for
     * @return the commission entity associated with the order
     * @throws CustomException if no commission is found for the given order
     */
    public Commission findCommissionByOrder(Order order) {
        log.info("In Method findCommissionByOrder");

        return commissionRepository
                .findByOrder(order)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.COMMISSION_NOT_FOUND
                ));
    }
}