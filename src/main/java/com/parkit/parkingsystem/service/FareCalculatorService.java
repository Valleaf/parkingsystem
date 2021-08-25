package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        System.out.println(ticket.getInTime().toString());
        System.out.println(ticket.getOutTime().toString());
        Duration duration = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());
        long hoursPassed = duration.toHours();
        System.out.println("inTime: " + ticket.getInTime());
        System.out.println("outTime : " + ticket.getOutTime());

        // TODO: Some tests are failing here. Need to check if this logic is correct
        System.out.println("duration: " + duration + "hoursPassed: " + hoursPassed);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(hoursPassed * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(hoursPassed * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}