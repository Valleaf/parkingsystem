package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
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

        Duration duration = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());
        long hoursPassed = duration.toHours();
        float minutes = duration.toMinutes() % 60;
        System.out.println("minutes:" + minutes);
        // TODO: Some tests are failing here. Need to check if this logic is correct
        System.out.println("duration: " + duration + "hoursPassed: " + hoursPassed);
        float realDuration = hoursPassed + minutes / 60;
        realDuration = BigDecimal.valueOf(realDuration).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        System.out.println("realDuration: " + realDuration);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(realDuration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(realDuration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}