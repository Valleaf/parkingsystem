package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

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
        float realDuration = hoursPassed + minutes / 60;
        realDuration = BigDecimal.valueOf(realDuration).setScale(2, RoundingMode.HALF_EVEN).floatValue();
        if (realDuration < 0.5) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(Math.round(((realDuration * Fare.CAR_RATE_PER_HOUR) * 100.0) / 100.0));
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

    /**
     * If customer is a regular, we discount 5% from the price
     */
    public void calculateFare(Ticket ticket, boolean isRegular) {
        calculateFare(ticket);
        if (isRegular)
            ticket.setPrice(Math.round((ticket.getPrice() * 0.95) * 100.0) / 100.0);
    }
}