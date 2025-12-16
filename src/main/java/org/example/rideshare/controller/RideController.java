package org.example.rideshare.controller;

import jakarta.validation.Valid;
import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.model.Ride;
import org.example.rideshare.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/rides")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Ride> createRide(@Valid @RequestBody CreateRideRequest ridePayload, Authentication authentication) {
        return ResponseEntity.ok(rideService.createRide(ridePayload, authentication.getName()));
    }

    @GetMapping("/user/rides")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Ride>> getUserRides(Authentication authentication) {
        return ResponseEntity.ok(rideService.getUserRides(authentication.getName()));
    }

    @GetMapping("/driver/rides/requests")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<Ride>> getPendingRides() {
        return ResponseEntity.ok(rideService.getPendingRides());
    }

    @PostMapping("/driver/rides/{rideId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Ride> acceptRide(@PathVariable String rideId, Authentication authentication) {
        return ResponseEntity.ok(rideService.acceptRide(rideId, authentication.getName()));
    }

    @PostMapping("/rides/{rideId}/complete")
    @PreAuthorize("hasAnyRole('USER', 'DRIVER')")
    public ResponseEntity<Ride> completeRide(@PathVariable String rideId, Authentication authentication) {
        return ResponseEntity.ok(rideService.completeRide(rideId, authentication.getName()));
    }
}
