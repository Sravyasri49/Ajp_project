package Cabs;

import java.sql.*;
import java.util.Scanner;

public class CabBookingApp {
    private static final String URL = "jdbc:mysql://localhost:3306/cab_booking";
    private static final String USER = "root";
    private static final String PASSWORD = "system";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("======================================");
            System.out.println("       Cab Booking Application        ");
            System.out.println("======================================");
            System.out.println("1. Register as User");
            System.out.println("2. Login as User");
            System.out.println("3. Login as Admin");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1: registerUser(); break;
                case 2: userMenu(); break;
                case 3: adminMenu(); break;
                case 4:System.out.println("🙏 Thank you , visit again 🙏"); 
                	System.exit(0);
                default: System.out.println("ℹ️ℹ️Invalid option! Try again.");
            }
        }
    }
    
    // New method for user registration
    private static void registerUser() {
        System.out.println("========= User Registration =========");
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO users (name, role, phone, password) VALUES (?, 'user', ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            
            // Retrieve generated user id
            ResultSet keys = pstmt.getGeneratedKeys();
            int userId = -1;
            if (keys.next()) {
                userId = keys.getInt(1);
            }
            System.out.println("Registration successful! Your User ID is: " + userId);
            System.out.println("Please remember your User ID to log in.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("❌ Error: Phone number already registered! ❌");
            } else {
                e.printStackTrace();
            }
        }
    }
    
    private static void userMenu() {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        
        while (true) {
            System.out.println("\n======================================");
            System.out.println("             User Menu                ");
            System.out.println("======================================");
            System.out.println("1. Book a Cab");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View Ride History");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1: bookCab(userId); break;
                case 2: cancelBooking(userId); break;
                case 3: viewRideHistory(userId); break;
                case 4: return;
                default: System.out.println("❌Invalid option!");
            }
        }
    }
    
    private static void bookCab(int userId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Check if user exists
            String userCheckSQL = "SELECT id FROM users WHERE id = ?";
            PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSQL);
            userCheckStmt.setInt(1, userId);
            ResultSet userResult = userCheckStmt.executeQuery();

            if (!userResult.next()) {
                System.out.println(" ❌Error: User ID does not exist. Please register first.");
                return;
            }
            
            System.out.print("Enter Pickup Location: ");
            String pickup = scanner.nextLine();
            System.out.print("Enter Dropoff Location: ");
            String dropoff = scanner.nextLine();
            System.out.print("Enter Distance (km): ");
            double distance = scanner.nextDouble();
            scanner.nextLine();
            double price = distance * 7; // Assuming 10 currency units per km
            
            // Use RETURN_GENERATED_KEYS to get the booking id after insertion
            String sql = "INSERT INTO bookings (user_id, pickup, dropoff, distance, price, status) VALUES (?, ?, ?, ?, ?, 'Booked')";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setString(2, pickup);
            pstmt.setString(3, dropoff);
            pstmt.setDouble(4, distance);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
            
            // Retrieve generated booking id
            ResultSet keys = pstmt.getGeneratedKeys();
            int bookingId = -1;
            if (keys.next()) {
                bookingId = keys.getInt(1);
            }
            
            System.out.println("\nBooking confirmed!");
            System.out.println("======================================");
            System.out.printf("Booking ID: %d\n", bookingId);
            System.out.println("---------- Receipt -------------------");
            System.out.printf("Pickup Location : %s\n", pickup);
            System.out.printf("Dropoff Location: %s\n", dropoff);
            System.out.printf("Distance        : %.2f km\n", distance);
            System.out.printf("Fare            : %.2f\n", price);
            System.out.println("======================================\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void cancelBooking(int userId) {
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE bookings SET status = 'Cancelled' WHERE id = ? AND user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println(": ✅ Booking cancelled successfully!");
            } else {
                System.out.println("❌Invalid Booking ID or booking already cancelled!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void viewRideHistory(int userId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM bookings WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("\nRide History:");
            System.out.println("--------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-15s | %-15s | %-8s | %-8s | %-10s |\n", "BookingID", "Pickup", "Dropoff", "Distance", "Price", "Status");
            System.out.println("--------------------------------------------------------------------------");
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                String pickup = rs.getString("pickup");
                String dropoff = rs.getString("dropoff");
                double distance = rs.getDouble("distance");
                double price = rs.getDouble("price");
                String status = rs.getString("status");
                System.out.printf("| %-10d | %-15s | %-15s | %-8.2f | %-8.2f | %-10s |\n", bookingId, pickup, dropoff, distance, price, status);
            }
            System.out.println("--------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void adminMenu() {
        System.out.print("Enter Admin ID: ");
        int adminId = scanner.nextInt();
        scanner.nextLine();
        
        while (true) {
            System.out.println("\n======================================");
            System.out.println("             Admin Menu               ");
            System.out.println("======================================");
            System.out.println("1. View All Bookings");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1: viewAllBookings(); break;
                case 2: return;
                default: System.out.println("❌Invalid option!");
            }
        }
    }
    
    private static void viewAllBookings() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM bookings";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\nAll Bookings:");
            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-8s | %-15s | %-15s | %-8s | %-8s | %-10s |\n", "BookingID", "UserID", "Pickup", "Dropoff", "Distance", "Price", "Status");
            System.out.println("---------------------------------------------------------------------------------------------");
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String pickup = rs.getString("pickup");
                String dropoff = rs.getString("dropoff");
                double distance = rs.getDouble("distance");
                double price = rs.getDouble("price");
                String status = rs.getString("status");
                System.out.printf("| %-10d | %-8d | %-15s | %-15s | %-8.2f | %-8.2f | %-10s |\n", bookingId, userId, pickup, dropoff, distance, price, status);
            }
            System.out.println("---------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
