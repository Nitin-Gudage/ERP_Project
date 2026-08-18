package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.History;
import com.erp.entity.HistoryAction;

public interface HistoryRepository
        extends JpaRepository<History, Long> {

    List<History>
    findByReferenceTypeAndReferenceId(
            String referenceType,
            Long referenceId
    );

    List<History>
    findByAction(
            HistoryAction action
    );

    List<History>
    findByPerformedById(
            Long userId
    );
}