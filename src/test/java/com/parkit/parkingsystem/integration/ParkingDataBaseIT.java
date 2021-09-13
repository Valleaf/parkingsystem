package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int spotBefore = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        System.out.println("Available number before test : " + spotBefore);
        // Incoming vehicle
        parkingService.processIncomingVehicle();
        // Check that a ticket is actually saved in DB and Parking table is updated
        // with availability
        // check both db for data
        System.out.println("Please type the vehicle registration number and press enter key");
        try {
            Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
            System.out.println("Ticket : " + ticket.getId());
            ParkingSpot ps = ticket.getParkingSpot();
            System.out.println("Spot " + spotBefore + " available : " + ps.isAvailable());
            assertEquals(false, ps.isAvailable());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Date currentTime = DateUtils.round(new Date(), Calendar.MINUTE);
        // TODO: check that the fare generated and out time are populated correctly in
        // the database
        try {
            Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
            System.out.println("Current time : " + LocalDateTime.now());
            Date timeOnTicket = DateUtils.round(ticket.getOutTime(), Calendar.MINUTE);
            double ticketPrice = ticket.getPrice();
            System.out.println(ticketPrice);
            System.out.println(timeOnTicket);
            assertEquals(ticketPrice, 0.0, "Prices do not match, should be 0");
            assertEquals(timeOnTicket, currentTime, "Time on ticket does not match");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
