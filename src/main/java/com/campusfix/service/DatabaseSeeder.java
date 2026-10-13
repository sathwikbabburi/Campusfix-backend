package com.campusfix.service;

import com.campusfix.model.*;
import com.campusfix.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintTimelineRepository timelineRepository;
    private final ComplaintUpvoteRepository upvoteRepository;
    private final FeedbackRepository feedbackRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          ComplaintRepository complaintRepository,
                          ComplaintTimelineRepository timelineRepository,
                          ComplaintUpvoteRepository upvoteRepository,
                          FeedbackRepository feedbackRepository,
                          AnnouncementRepository announcementRepository,
                          PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.timelineRepository = timelineRepository;
        this.upvoteRepository = upvoteRepository;
        this.feedbackRepository = feedbackRepository;
        this.announcementRepository = announcementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Verifying CampusFix initial seed data...");

        // 1. Seed Categories if empty
        if (categoryRepository.count() == 0) {
            log.info("Seeding campus maintenance categories...");
            List<Category> categories = List.of(
                    new Category("Electrical & Power", "Power cuts, switches, lighting, fans, AC, short circuits, generators", 12, "Zap", "light,bulb,fan,ac,air conditioner,switch,socket,power,electricity,short circuit,wire,breaker,generator,dark,cooler"),
                    new Category("Plumbing & Water", "Pipe leakage, washroom water supply, taps, flush, drainage, drinking water", 18, "Droplets", "water,tap,leak,leakage,pipe,washroom,toilet,restroom,flush,sink,basin,drainage,drinking water,ro filter,tank,overflow"),
                    new Category("IT & Campus Network", "Campus WiFi, LAN cables, lab computers, projectors, smart boards, printers", 24, "Wifi", "wifi,internet,lan,ethernet,router,network,computer,pc,desktop,lab,projector,screen,printer,smart board,portal,login"),
                    new Category("Civil & Carpentry", "Benches, desks, broken doors, windows, locks, ceiling cracks, whiteboards", 36, "Hammer", "door,lock,key,window,glass,bench,desk,table,chair,podium,cupboard,board,whiteboard,crack,wall,ceiling,tile,plaster"),
                    new Category("Housekeeping & Sanitation", "Dustbins, classroom sweeping, washroom cleanliness, garbage disposal", 8, "Sparkles", "clean,cleaning,sweep,sweeper,dustbin,garbage,trash,smell,odor,stink,dirty,hygiene,spill,dust,mop,waste"),
                    new Category("Hostel & Mess", "Room amenities, mess food quality, water dispensers, hostel gym, laundry", 24, "Home", "hostel,mess,food,curry,rice,chapati,plate,cot,bed,mattress,wardrobe,geyser,warden,curfew,gym,laundry"),
                    new Category("Security & Safety", "CCTV cameras, fire extinguishers, emergency lights, stray dogs, parking, gates", 6, "ShieldAlert", "security,guard,gate,cctv,camera,fire,extinguisher,alarm,parking,stray,theft,stolen,lost,id card,hazard,unsafe")
            );
            categoryRepository.saveAll(categories);
        }

        // 2. Seed Users if empty
        if (userRepository.count() == 0) {
            log.info("Seeding default demo users with BCrypt hashing...");
            String defaultHash = passwordEncoder.encode("password123");

            User admin = new User("Dr. Rajesh Rao (Admin)", "admin@campusfix.edu", defaultHash, Role.ADMIN, "Estate & Facilities Office", "+91 9876543210", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80");
            User staffElec = new User("Manoj Kumar (Electrical)", "staff.electrical@campusfix.edu", defaultHash, Role.STAFF, "Electrical & Power Dept", "+91 9876543211", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80");
            User staffIt = new User("Suresh Patel (IT & Systems)", "staff.it@campusfix.edu", defaultHash, Role.STAFF, "IT & Campus Network Dept", "+91 9876543212", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80");
            User staffPlumb = new User("Ramesh Naik (Plumbing)", "staff.plumbing@campusfix.edu", defaultHash, Role.STAFF, "Plumbing & Water Dept", "+91 9876543213", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=150&q=80");
            User faculty = new User("Prof. Sunita Sharma", "prof.sharma@campusfix.edu", defaultHash, Role.FACULTY, "Computer Science & Engineering", "+91 9876543214", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=150&q=80");
            User student1 = new User("Rahul Verma (Student)", "rahul.verma@campusfix.edu", defaultHash, Role.STUDENT, "CSE - 3rd Year", "+91 9876543215", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80");
            User student2 = new User("Ananya Sen (Student)", "ananya.sen@campusfix.edu", defaultHash, Role.STUDENT, "ECE - 2nd Year", "+91 9876543216", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80");

            userRepository.saveAll(List.of(admin, staffElec, staffIt, staffPlumb, faculty, student1, student2));
        }

        // 3. Seed Sample Complaints if empty
        if (complaintRepository.count() == 0) {
            log.info("Seeding realistic sample campus complaints...");
            List<Category> cats = categoryRepository.findAll();
            Category electrical = cats.stream().filter(c -> c.getName().contains("Electrical")).findFirst().orElse(cats.get(0));
            Category plumbing = cats.stream().filter(c -> c.getName().contains("Plumbing")).findFirst().orElse(cats.get(1));
            Category it = cats.stream().filter(c -> c.getName().contains("IT")).findFirst().orElse(cats.get(2));
            Category civil = cats.stream().filter(c -> c.getName().contains("Civil")).findFirst().orElse(cats.get(3));
            Category hostel = cats.stream().filter(c -> c.getName().contains("Hostel")).findFirst().orElse(cats.get(4));

            User admin = userRepository.findByEmail("admin@campusfix.edu").orElse(null);
            User staffElec = userRepository.findByEmail("staff.electrical@campusfix.edu").orElse(null);
            User staffIt = userRepository.findByEmail("staff.it@campusfix.edu").orElse(null);
            User staffPlumb = userRepository.findByEmail("staff.plumbing@campusfix.edu").orElse(null);
            User faculty = userRepository.findByEmail("prof.sharma@campusfix.edu").orElse(null);
            User student1 = userRepository.findByEmail("rahul.verma@campusfix.edu").orElse(null);
            User student2 = userRepository.findByEmail("ananya.sen@campusfix.edu").orElse(null);

            // Complaint 1: IN_PROGRESS
            Complaint c1 = new Complaint();
            c1.setTicketNumber("CF-2026-001");
            c1.setTitle("Ceiling Fan Not Working and Making Sparking Noise");
            c1.setDescription("The ceiling fan in the front row of Seminar Hall 2 suddenly stopped spinning and sparks were observed from the regulator box.");
            c1.setCategory(electrical);
            c1.setPriority(Priority.HIGH);
            c1.setStatus(Status.IN_PROGRESS);
            c1.setLocationBlock("Academic Block A");
            c1.setRoomNumber("Seminar Hall 2");
            c1.setComplainant(student1);
            c1.setAssignedStaff(staffElec);
            c1.setImageUrl("https://images.unsplash.com/photo-1517420704952-d9f39e95b43e?auto=format&fit=crop&w=600&q=80");
            c1.setResolutionNotes("Technician inspected regulator capacitor. Replacement coil ordered from central store.");
            c1.setUpvotesCount(5);
            complaintRepository.save(c1);

            timelineRepository.save(new ComplaintTimeline(c1, student1, null, "SUBMITTED", "Complaint lodged with initial photo evidence."));
            timelineRepository.save(new ComplaintTimeline(c1, admin, "SUBMITTED", "ASSIGNED", "System auto-routed to Electrical Dept. Assigned to Manoj Kumar."));
            timelineRepository.save(new ComplaintTimeline(c1, staffElec, "ASSIGNED", "IN_PROGRESS", "Technician on-site inspecting regulator and wiring harness."));

            // Complaint 2: ASSIGNED
            Complaint c2 = new Complaint();
            c2.setTicketNumber("CF-2026-002");
            c2.setTitle("High Speed WiFi Router Drops Connection Every 10 Mins");
            c2.setDescription("Access point in Central Library 2nd floor digital reading lounge keeps rebooting during peak study hours. DHCP pool runs out.");
            c2.setCategory(it);
            c2.setPriority(Priority.CRITICAL);
            c2.setStatus(Status.ASSIGNED);
            c2.setLocationBlock("Central Library");
            c2.setRoomNumber("2nd Floor Reading Room");
            c2.setComplainant(student2);
            c2.setAssignedStaff(staffIt);
            c2.setImageUrl("https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&w=600&q=80");
            c2.setUpvotesCount(12);
            complaintRepository.save(c2);

            timelineRepository.save(new ComplaintTimeline(c2, student2, null, "SUBMITTED", "Network disruption logged with MAC and floor location."));
            timelineRepository.save(new ComplaintTimeline(c2, admin, "SUBMITTED", "ASSIGNED", "Assigned to IT Admin Suresh Patel with SLA 24 Hours."));

            // Complaint 3: RESOLVED + Feedback
            Complaint c3 = new Complaint();
            c3.setTicketNumber("CF-2026-003");
            c3.setTitle("Severe Water Leakage from Overhead Pipe near Washroom");
            c3.setDescription("Continuous dripping of water near Ground Floor Restroom of Block B causing slippery floor and water wastage.");
            c3.setCategory(plumbing);
            c3.setPriority(Priority.HIGH);
            c3.setStatus(Status.RESOLVED);
            c3.setLocationBlock("Academic Block B");
            c3.setRoomNumber("Restroom 104");
            c3.setComplainant(faculty);
            c3.setAssignedStaff(staffPlumb);
            c3.setImageUrl("https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=600&q=80");
            c3.setResolutionImageUrl("https://images.unsplash.com/photo-1585704032915-c3400ca199e7?auto=format&fit=crop&w=600&q=80");
            c3.setResolutionNotes("Replaced ruptured PVC elbow joint with heavy-duty brass valve. Tested under full pressure for 1 hour without leaks.");
            c3.setUpvotesCount(8);
            c3.setResolvedAt(LocalDateTime.now().minusHours(12));
            complaintRepository.save(c3);

            timelineRepository.save(new ComplaintTimeline(c3, faculty, null, "SUBMITTED", "Faculty reported major water overflow."));
            timelineRepository.save(new ComplaintTimeline(c3, admin, "SUBMITTED", "ASSIGNED", "Auto-routed to Plumbing Dept. Assigned to Ramesh Naik."));
            timelineRepository.save(new ComplaintTimeline(c3, staffPlumb, "ASSIGNED", "IN_PROGRESS", "Main water line isolated. Commencing pipe replacement."));
            timelineRepository.save(new ComplaintTimeline(c3, staffPlumb, "IN_PROGRESS", "RESOLVED", "Joint welded and tested. Cleaned floor area."));

            feedbackRepository.save(new Feedback(c3, faculty, 5, "Remarkably fast repair! Prevented major classroom floor damage."));

            // Complaint 4: SUBMITTED
            Complaint c4 = new Complaint();
            c4.setTicketNumber("CF-2026-004");
            c4.setTitle("Broken Bench and Unstable Wooden Desk in Classroom 302");
            c4.setDescription("Two rows of benches have loose bolts and one desk has a cracked wooden surface causing snagging on clothes.");
            c4.setCategory(civil);
            c4.setPriority(Priority.MEDIUM);
            c4.setStatus(Status.SUBMITTED);
            c4.setLocationBlock("Academic Block C");
            c4.setRoomNumber("Room 302");
            c4.setComplainant(student1);
            c4.setUpvotesCount(3);
            complaintRepository.save(c4);

            timelineRepository.save(new ComplaintTimeline(c4, student1, null, "SUBMITTED", "New classroom maintenance ticket submitted."));

            // Complaint 5: IN_PROGRESS
            Complaint c5 = new Complaint();
            c5.setTicketNumber("CF-2026-005");
            c5.setTitle("Drinking Water Cooler RO Filter Taste Unclean");
            c5.setDescription("Water from the 1st floor water dispenser in Boys Hostel 3 tastes muddy. Filter cartridge needs periodic replacement.");
            c5.setCategory(hostel);
            c5.setPriority(Priority.MEDIUM);
            c5.setStatus(Status.IN_PROGRESS);
            c5.setLocationBlock("Boys Hostel 3");
            c5.setRoomNumber("1st Floor Water Station");
            c5.setComplainant(student1);
            c5.setAssignedStaff(staffPlumb);
            c5.setUpvotesCount(9);
            complaintRepository.save(c5);

            timelineRepository.save(new ComplaintTimeline(c5, student1, null, "SUBMITTED", "Lodge ticket for water dispenser."));
            timelineRepository.save(new ComplaintTimeline(c5, staffPlumb, "SUBMITTED", "IN_PROGRESS", "Replacement cartridges requisitioned from hostel supplies."));

            // Add upvotes
            upvoteRepository.save(new ComplaintUpvote(c1, student2));
            upvoteRepository.save(new ComplaintUpvote(c2, student1));
        }

        // 4. Seed Campus Announcements if empty
        if (announcementRepository.count() == 0) {
            log.info("Seeding campus announcements...");
            User admin = userRepository.findByEmail("admin@campusfix.edu").orElse(null);
            if (admin != null) {
                announcementRepository.save(new Announcement(
                        "Overhead Water Tank Annual Cleaning & Sanitation",
                        "Water supply to Academic Blocks A & B will be suspended this Saturday from 2:00 PM to 6:00 PM for scheduled chlorination and mechanical cleaning.",
                        "Plumbing & Water Dept",
                        "HIGH",
                        admin
                ));
                announcementRepository.save(new Announcement(
                        "Central Library Core Switch Firmware Upgrade",
                        "Campus IT will be performing scheduled edge-switch maintenance in the Central Library tonight between 11:00 PM and 1:00 AM. Intermittent WiFi expected.",
                        "IT & Systems Dept",
                        "NORMAL",
                        admin
                ));
            }
        }

        log.info("CampusFix database seeding completed successfully.");
    }
}
