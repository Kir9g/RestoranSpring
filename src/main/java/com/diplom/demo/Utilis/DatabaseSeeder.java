//package com.diplom.demo.Utilis;
//
//import com.diplom.demo.Entity.*;
//import com.diplom.demo.Enums.UserRole;
//import com.diplom.demo.Repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Configuration
//@RequiredArgsConstructor
//public class DatabaseSeeder {
//
//    private final RestaurantRepository restaurantRepository;
//    private final RoomRepository roomRepository;
//    private final TableEntityRepository tableRepository;
//    private final UserRepository userRepository;
//    private final CategoryRepository categoryRepository;
//    private final MenuItemRepository menuItemRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    @Transactional
//    public CommandLineRunner seedDatabase() {
//        return args -> {
//            // === 1. Restaurant ===
//            Restaurant restaurant = new Restaurant();
//            restaurant.setName("Gourmet Palace");
//            restaurant.setAddress("123 Culinary St.");
//            restaurant.setPhoneNumber("+1234567890");
//            restaurant.setDescription("Fine dining experience");
//            restaurantRepository.save(restaurant);
//
//            // === 2. Room ===
//            Room room = new Room();
//            room.setName("Main Hall");
//            room.setRestaurant(restaurant);
//            roomRepository.save(room);
//
//            // === 3. Tables ===
//            for (int i = 1; i <= 3; i++) {
//                TableEntity table = new TableEntity();
//                table.setLabel("T" + i);
//                table.setDescription("Table " + i);
//                table.setSeats(4);
//                table.setStringUrl("/images/tables/table" + i + ".png");
//                table.setRoom(room);
//                tableRepository.save(table);
//            }
//
//            // === 4. Categories ===
//            CategoryEntity foodCategory = new CategoryEntity();
//            foodCategory.setName("Food");
//            foodCategory.setImageUrl("/images/categories/food.png");
//
//            CategoryEntity drinksCategory = new CategoryEntity();
//            drinksCategory.setName("Drinks");
//            drinksCategory.setImageUrl("/images/categories/drinks.png");
//
//            categoryRepository.saveAll(List.of(foodCategory, drinksCategory));
//
//            // === 5. Menu Items ===
//            MenuItem burger = new MenuItem();
//            burger.setName("Classic Burger");
//            burger.setDescription("Juicy beef burger with cheese");
//            burger.setPrice(BigDecimal.valueOf(9.99));
//            burger.setCategory(foodCategory);
//            burger.setRestaurant(restaurant);
//            burger.setImageUrl("/images/menu/burger.png");
//
//            MenuItem lemonade = new MenuItem();
//            lemonade.setName("Lemonade");
//            lemonade.setDescription("Fresh lemonade with mint");
//            lemonade.setPrice(BigDecimal.valueOf(3.99));
//            lemonade.setCategory(drinksCategory);
//            lemonade.setRestaurant(restaurant);
//            lemonade.setImageUrl("/images/menu/lemonade.png");
//
//            menuItemRepository.saveAll(List.of(burger, lemonade));
//
//            // === 6. Users ===
//            User admin = new User();
//            admin.setUsername("admin");
//            admin.setPassword(passwordEncoder.encode("admin"));
//            admin.setRole(UserRole.ADMIN);
//            admin.setFullName("Administrator");
//            userRepository.save(admin);
//
//            User waiter = new User();
//            waiter.setUsername("waiter");
//            waiter.setPassword(passwordEncoder.encode("waiter"));
//            waiter.setRole(UserRole.WAITER);
//            waiter.setFullName("John Waiter");
//            userRepository.save(waiter);
//
//            User cook = new User();
//            cook.setUsername("cook");
//            cook.setPassword(passwordEncoder.encode("cook"));
//            cook.setRole(UserRole.COOK);
//            cook.setFullName("Jane Cook");
//            userRepository.save(cook);
//
//            User client = new User();
//            client.setUsername("client");
//            client.setPassword(passwordEncoder.encode("client"));
//            client.setRole(UserRole.CLIENT);
//            client.setFullName("Alex Client");
//            client.setEmail("alex@example.com");
//            client.setPhone("+111222333");
//            userRepository.save(client);
//
//            System.out.println("✅ Test data inserted successfully.");
//        };
//    }
//}
