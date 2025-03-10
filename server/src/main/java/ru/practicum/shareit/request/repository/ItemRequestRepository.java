package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequesterIdOrderByCreatedAsc(Integer requesterId);
    
    List<ItemRequest> findByRequesterIdNotOrderByCreatedAsc(Integer userId);

}