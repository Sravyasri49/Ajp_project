# Cab Booking System

## Overview
The **Cab Booking System** is a console-based Java application that allows users to book cabs,
manage their bookings, and track ride history. Admins can oversee users, cabs, and bookings. 
The system is designed for efficient cab allocation and fare calculation based on distance, 
ensuring a seamless experience for both customers and administrators. 
The application is backed by a MySQL database for secure and efficient data handling.

## Features
### **User Features:**
- **User Registration & Login** (Supports role-based access: User & Admin)
- **Book a Cab** (Assigns the nearest available cab automatically)
- **Price Calculation** (Dynamically calculated based on distance traveled)
- **Cancel Booking** (Users can cancel their booked rides before dispatch)
- **View Ride History** (Complete list of previous bookings and their details)
- **Invoice Generation** (Displays a detailed receipt after booking completion)

### **Admin Features:**
- **Manage Users** (View and remove registered users)
- **Manage Cabs** (Add, update, and remove cabs from the fleet)
- **View All Bookings** (Monitor ongoing and completed bookings)
- **Update Cab Status** (Change the availability of cabs dynamically)

## Database Schema

### 1. users
- id (Primary Key)
- name
- role (user/admin)
- phone (Unique, required for login and communication)
- password (Encrypted for security)

### 2. cabs
- id (Primary Key)
- driver_name
- cab_number (Unique, ensures no duplicate registration)
- status (Available/Booked)

### 3. bookings
- id (Primary Key)
- user_id (Foreign Key → users.id)
- cab_id (Foreign Key → cabs.id)
- pickup (Location where the ride starts)
- dropoff (Destination of the ride)
- distance (Kilometers calculated between pickup and dropoff)
- price (Calculated dynamically based on distance and fixed rates)
- status (Booked/Cancelled/Completed)
- booking_time (Timestamp of booking confirmation)

## Entity-Relationships
1. **A User can make multiple bookings**, but each booking belongs to only one user. *(One-to-Many: users → bookings)*
2. **A Cab can be assigned to multiple bookings**, but each booking is linked to only one cab. *(One-to-Many: cabs → bookings)*

## Technologies Used
- **Java** (Core Java for business logic and application handling)
- **MySQL** (Relational database for storing user, cab, and booking details)
- **JDBC** (Java Database Connectivity for seamless interaction with MySQL)

## Installation & Setup
1. **Database Setup:**
   - Create a MySQL database named `cab_booking`.
   - Execute the provided SQL script to create tables.
   - Update the database credentials in the Java application.

2. **Compile and Run the Java Application:**
   ```sh
   javac CabBookingApp.java
   java CabBookingApp
   ```

## Future Enhancements
- Implement a GUI for a more interactive user experience.
- Add real-time GPS tracking for cab location updates.
- Integrate a payment gateway for online fare transactions.
- Introduce promo codes and discounts for frequent users.
- Implement AI-based cab allocation for optimized ride assignment.

## Author
**Sravya** - Developed as part of a project in Computer Science & Design.

