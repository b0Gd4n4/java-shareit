package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestBody ItemRequestDto itemRequestDto) {

        log.info("User {}, add new request", userId);
        return ResponseEntity.ok(itemRequestService.createRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Get requests by user Id {}", userId);
        return ResponseEntity.ok(itemRequestService.getRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                               @RequestParam(defaultValue = "10", required = false) Integer size) {

        log.info("Get all requests by All users ");
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable("requestId") Long requestId) {

        log.info("Get request {}", requestId);
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }
}
