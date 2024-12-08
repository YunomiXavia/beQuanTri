package com.example.beQuanTri.service.status;

import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.repository.status.StatusRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to Status entities.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class StatusService {

    // Dependencies
    StatusRepository statusRepository;

    /**
     * Retrieves a Status entity by its name.
     *
     * @param statusName the name of the status to find
     * @return the Status entity
     * @throws CustomException if the status is not found
     */
    public Status findByStatusName(String statusName) {
        log.info("In Method findByStatusName: {}", statusName);

        return statusRepository
                .findByStatusName(statusName)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.STATUS_NOT_FOUND
                        )
                );
    }
}