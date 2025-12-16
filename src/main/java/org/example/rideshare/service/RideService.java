package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.exception.BadRequestException;
import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.Ride;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserService userService;

    public RideService(RideRepository rideRepository, UserService userService) {
        this.rideRepository = rideRepository;
        this.userService = userService;
    }

    public Ride createRide(CreateRideRequest rideRequest, String passengerUsername) {
        User passengerAccount = userService.findByUsername(passengerUsername);
        Ride newRide = new Ride();
        newRide.setUserId(passengerAccount.getId());
        newRide.setPickupLocation(rideRequest.getPickupLocation());
        newRide.setDropLocation(rideRequest.getDropLocation());
        newRide.setStatus("REQUESTED");
        newRide.setCreatedAt(new Date());
        return rideRepository.save(newRide);
    }

    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    public Ride acceptRide(String rideId, String driverUsername) {
        User driverAccount = userService.findByUsername(driverUsername);
        Ride targetRide = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!targetRide.getStatus().equals("REQUESTED")) {
            throw new BadRequestException("Ride is not in REQUESTED status");
        }

        targetRide.setDriverId(driverAccount.getId());
        targetRide.setStatus("ACCEPTED");
        return rideRepository.save(targetRide);
    }

    public Ride completeRide(String rideId, String username) {
        User actingUser = userService.findByUsername(username);
        Ride rideToClose = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!rideToClose.getStatus().equals("ACCEPTED")) {
            throw new BadRequestException("Ride must be ACCEPTED before completing");
        }

        if (!rideToClose.getUserId().equals(actingUser.getId()) && !actingUser.getId().equals(rideToClose.getDriverId())) {
            throw new BadRequestException("You are not authorized to complete this ride");
        }

        rideToClose.setStatus("COMPLETED");
        return rideRepository.save(rideToClose);
    }

    public List<Ride> getUserRides(String username) {
        User rider = userService.findByUsername(username);
        return rideRepository.findByUserId(rider.getId());
    }
}
