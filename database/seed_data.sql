-- ====================================================================
-- CampusFix Seed Data
-- Default Credentials:
-- Admin:        admin@campusfix.edu / password123
-- Staff/Technician: staff.electrical@campusfix.edu / password123
-- Staff/IT:         staff.it@campusfix.edu / password123
-- Faculty:      prof.sharma@campusfix.edu / password123
-- Student:      rahul.verma@campusfix.edu / password123
-- Student:      ananya.sen@campusfix.edu / password123
-- BCrypt hash for 'password123': $2a$10$7zB3iWlK8wSg08Y0k9pGpu5F8d712f5R9p1h1c5j1i4a5q6t7y8u9 (or handled in Spring Boot initializer)
-- ====================================================================

USE campusfix_db;

-- 1. Insert Categories with Automated Routing Keywords & SLA
INSERT INTO categories (id, name, description, sla_hours, icon, keywords) VALUES
(1, 'Electrical & Power', 'Power cuts, switches, lighting, fans, AC, short circuits, generators', 12, 'Zap', 'light,bulb,fan,ac,air conditioner,switch,socket,power,electricity,short circuit,wire,breaker,generator,dark,cooler'),
(2, 'Plumbing & Water', 'Pipe leakage, washroom water supply, taps, flush, drainage, drinking water', 18, 'Droplets', 'water,tap,leak,leakage,pipe,washroom,toilet,restroom,flush,sink,basin,drainage,drinking water,ro filter,tank,overflow'),
(3, 'IT & Campus Network', 'Campus WiFi, LAN cables, lab computers, projectors, smart boards, printers', 24, 'Wifi', 'wifi,internet,lan,ethernet,router,network,computer,pc,desktop,lab,projector,screen,printer,smart board,portal,login'),
(4, 'Civil & Carpentry', 'Benches, desks, broken doors, windows, locks, ceiling cracks, whiteboards', 36, 'Hammer', 'door,lock,key,window,glass,bench,desk,table,chair,podium,cupboard,board,whiteboard,crack,wall,ceiling,tile,plaster'),
(5, 'Housekeeping & Sanitation', 'Dustbins, classroom sweeping, washroom cleanliness, garbage disposal', 8, 'Sparkles', 'clean,cleaning,sweep,sweeper,dustbin,garbage,trash,smell,odor,stink,dirty,hygiene,spill,dust,mop,waste'),
(6, 'Hostel & Mess', 'Room amenities, mess food quality, water dispensers, hostel gym, laundry', 24, 'Home', 'hostel,mess,food,curry,rice,chapati,plate,cot,bed,mattress,wardrobe,geyser,warden,curfew,gym,laundry'),
(7, 'Security & Safety', 'CCTV cameras, fire extinguishers, emergency lights, stray dogs, parking, gates', 6, 'ShieldAlert', 'security,guard,gate,cctv,camera,fire,extinguisher,alarm,parking,stray,theft,stolen,lost,id card,hazard,unsafe');

-- 2. Insert Users
INSERT INTO users (id, full_name, email, password, role, department, phone, avatar_url) VALUES
(1, 'Dr. Rajesh Rao (Admin)', 'admin@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'ADMIN', 'Estate & Facilities Office', '+91 9876543210', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80'),
(2, 'Manoj Kumar (Electrical)', 'staff.electrical@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'STAFF', 'Electrical Maintenance', '+91 9876543211', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80'),
(3, 'Suresh Patel (IT & Systems)', 'staff.it@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'STAFF', 'IT & Systems Support', '+91 9876543212', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80'),
(4, 'Ramesh Naik (Plumbing)', 'staff.plumbing@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'STAFF', 'Water & Plumbing Dept', '+91 9876543213', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=150&q=80'),
(5, 'Prof. Sunita Sharma', 'prof.sharma@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'FACULTY', 'Computer Science & Engineering', '+91 9876543214', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=150&q=80'),
(6, 'Rahul Verma (Student)', 'rahul.verma@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'STUDENT', 'CSE - 3rd Year', '+91 9876543215', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80'),
(7, 'Ananya Sen (Student)', 'ananya.sen@campusfix.edu', '$2a$10$w09ZlMsq1x41gDqFw9iK6uQ2H6mD1Zc8sD/qR7zS1wW7/uXjG9.iW', 'STUDENT', 'ECE - 2nd Year', '+91 9876543216', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80');

-- 3. Insert Sample Complaints across different stages
INSERT INTO complaints (id, ticket_number, title, description, category_id, priority, status, location_block, room_number, complainant_id, assigned_staff_id, image_url, resolution_image_url, resolution_notes, upvotes_count, created_at, updated_at, resolved_at) VALUES
(1, 'CF-2026-001', 'Ceiling Fan Not Working and Making Sparking Noise', 'The ceiling fan in the front row of Seminar Hall 2 suddenly stopped spinning and sparks were observed from the regulator box.', 1, 'HIGH', 'IN_PROGRESS', 'Academic Block A', 'Seminar Hall 2', 6, 2, 'https://images.unsplash.com/photo-1517420704952-d9f39e95b43e?auto=format&fit=crop&w=600&q=80', NULL, 'Technician inspected regulator capacitor. Replacement coil ordered from central store.', 5, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NULL),

(2, 'CF-2026-002', 'High Speed WiFi Router Drops Connection Every 10 Mins', 'Access point in Central Library 2nd floor digital reading lounge keeps rebooting during peak study hours. DHCP pool runs out.', 3, 'CRITICAL', 'ASSIGNED', 'Central Library', '2nd Floor Reading Room', 7, 3, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&w=600&q=80', NULL, NULL, 12, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NULL),

(3, 'CF-2026-003', 'Severe Water Leakage from Overhead Pipe near Washroom', 'Continuous dripping of water near Ground Floor Restroom of Block B causing slippery floor and water wastage.', 2, 'HIGH', 'RESOLVED', 'Academic Block B', 'Restroom 104', 5, 4, 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=600&q=80', 'https://images.unsplash.com/photo-1585704032915-c3400ca199e7?auto=format&fit=crop&w=600&q=80', 'Replaced ruptured PVC elbow joint with heavy-duty brass valve. Tested under full pressure for 1 hour without leaks.', 8, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

(4, 'CF-2026-004', 'Broken Bench and Unstable Wooden Desk in Classroom 302', 'Two rows of benches have loose bolts and one desk has a cracked wooden surface causing snagging on clothes.', 4, 'MEDIUM', 'SUBMITTED', 'Academic Block C', 'Room 302', 6, NULL, 'https://images.unsplash.com/photo-1580582932707-520aed937b7b?auto=format&fit=crop&w=600&q=80', NULL, NULL, 2, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW(), NULL),

(5, 'CF-2026-005', 'Drinking Water Cooler RO Filter Taste Unclean', 'Water from the 1st floor water dispenser in Boys Hostel 3 tastes muddy. Filter cartridge needs periodic replacement.', 6, 'MEDIUM', 'IN_PROGRESS', 'Boys Hostel 3', '1st Floor Water Station', 6, 4, 'https://images.unsplash.com/photo-1548839140-29a749e1bc4e?auto=format&fit=crop&w=600&q=80', NULL, 'Sanitation staff dispatched. Micron sediment filter and activated carbon filter will be replaced tomorrow morning.', 9, DATE_SUB(NOW(), INTERVAL 18 HOUR), NOW(), NULL);

-- 4. Insert Complaint Timelines
INSERT INTO complaint_timeline (complaint_id, updated_by_id, previous_status, new_status, action_comment, created_at) VALUES
(1, 6, NULL, 'SUBMITTED', 'Complaint lodged with initial photo evidence.', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 1, 'SUBMITTED', 'ASSIGNED', 'System auto-routed to Electrical Dept. Assigned to Manoj Kumar.', DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(1, 2, 'ASSIGNED', 'IN_PROGRESS', 'Technician on-site inspecting the regulator and wiring harness.', DATE_SUB(NOW(), INTERVAL 24 HOUR)),

(2, 7, NULL, 'SUBMITTED', 'Network disruption logged with MAC and floor location.', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, 'SUBMITTED', 'ASSIGNED', 'Assigned to IT Admin Suresh Patel with SLA 24 Hours.', DATE_SUB(NOW(), INTERVAL 20 HOUR)),

(3, 5, NULL, 'SUBMITTED', 'Faculty reported major water overflow.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 1, 'SUBMITTED', 'ASSIGNED', 'Auto-routed to Plumbing Dept. Assigned to Ramesh Naik.', DATE_SUB(NOW(), INTERVAL 68 HOUR)),
(3, 4, 'ASSIGNED', 'IN_PROGRESS', 'Main water line isolated. Commencing pipe replacement.', DATE_SUB(NOW(), INTERVAL 48 HOUR)),
(3, 4, 'IN_PROGRESS', 'RESOLVED', 'Joint welded and tested. Cleaned floor area.', DATE_SUB(NOW(), INTERVAL 24 HOUR)),

(4, 6, NULL, 'SUBMITTED', 'Complaint registered. Awaiting automated department confirmation.', DATE_SUB(NOW(), INTERVAL 6 HOUR)),

(5, 6, NULL, 'SUBMITTED', 'Hostel resident logged drinking water filter query.', DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(5, 1, 'SUBMITTED', 'ASSIGNED', 'Auto-routed to Hostel & Plumbing maintenance.', DATE_SUB(NOW(), INTERVAL 14 HOUR)),
(5, 4, 'ASSIGNED', 'IN_PROGRESS', 'Procuring replacement RO membrane from central inventory.', DATE_SUB(NOW(), INTERVAL 8 HOUR));

-- 5. Insert Complaint Upvotes
INSERT INTO complaint_upvotes (complaint_id, user_id) VALUES
(1, 6), (1, 7), (1, 5),
(2, 6), (2, 7), (2, 5), (2, 1),
(3, 5), (3, 6), (3, 7),
(5, 6), (5, 7);

-- 6. Insert Feedbacks
INSERT INTO feedbacks (complaint_id, student_id, rating, comments, created_at) VALUES
(3, 5, 5, 'Prompt resolution by Ramesh and team. The leak was completely fixed within 24 hours, preventing water damage.', DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- 7. Insert Announcements
INSERT INTO announcements (id, title, content, department, priority, created_by_id, is_active, created_at) VALUES
(1, 'Scheduled Power Maintenance in Academic Blocks A & B', 'Please note that DG backup testing and transformer servicing will occur this Sunday between 10:00 AM and 01:00 PM. High-draw lab equipment should remain powered down.', 'Electrical & Power', 'HIGH', 1, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'Campus WiFi Gateway Upgrade (Gigabit Fiber Rollout)', 'The IT department is upgrading core access controllers tonight from 11:00 PM to 02:00 AM. Intermittent connectivity may be experienced across hostel blocks.', 'IT & Campus Network', 'NORMAL', 1, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'Zero Littering Campus Cleanliness Drive', 'Housekeeping team has installed 40 dual-stream waste segregation bins across all sports grounds and cafeteria exits. Please cooperate to keep our campus spotless.', 'Housekeeping & Sanitation', 'LOW', 1, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY));
