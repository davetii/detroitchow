-- Test data for DetroitChow Admin API
-- This file is loaded only when the 'test' profile is active

-- Delete existing test data to allow re-running
DELETE FROM detroitchow.google_places WHERE locationid LIKE 'loc-%';
DELETE FROM detroitchow.menus WHERE locationid LIKE 'loc-%';
DELETE FROM detroitchow.locations WHERE locationid LIKE 'loc-%';

-- Insert test locations
INSERT INTO detroitchow.locations (locationid, name, description, status, address1, city, locality, zip, region, country, phone1, lat, lng, website, facebook, instagram, hours, create_date, create_user, updated_date, update_user)
VALUES
    ('loc-001', 'Lafayette Coney Island', 'Historic Detroit coney island restaurant serving classic hot dogs and Greek-American fare since 1924', 'active', '118 W Lafayette Blvd', 'Detroit', 'Downtown', '48226', 'MI', 'US', '313-964-8198', '42.3297', '-83.0458', 'https://www.coneydetroit.com', 'https://facebook.com/lafayetteconeyisland', 'https://instagram.com/lafayetteconeyisland', 'Mon-Sun: 24 hours', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-002', 'Buddy''s Pizza', 'Original Detroit-style pizza with crispy, caramelized cheese edges and thick, airy crust', 'active', '17125 Conant St', 'Detroit', 'East Side', '48212', 'MI', 'US', '313-892-9001', '42.4043', '-83.0307', 'https://www.buddyspizza.com', 'https://facebook.com/buddyspizza', 'https://instagram.com/buddyspizza', 'Mon-Thu: 11am-10pm, Fri-Sat: 11am-11pm, Sun: 12pm-10pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-003', 'Selden Standard', 'Contemporary American restaurant featuring seasonal small plates and craft cocktails in a rustic-chic setting', 'active', '3921 2nd Ave', 'Detroit', 'Midtown', '48201', 'MI', 'US', '313-438-5055', '42.3516', '-83.0632', 'https://www.seldenstandard.com', 'https://facebook.com/seldenstandard', 'https://instagram.com/seldenstandard', 'Mon-Thu: 5pm-10pm, Fri-Sat: 5pm-11pm, Sun: 5pm-9pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-004', 'Supino Pizzeria', 'Thin-crust, wood-fired pizzas in a casual Eastern Market locale with outdoor seating', 'active', '2457 Russell St', 'Detroit', 'Eastern Market', '48207', 'MI', 'US', '313-567-7879', '42.3477', '-83.0389', 'https://www.supinopizzeria.com', 'https://facebook.com/supinopizzeria', 'https://instagram.com/supinopizzeria', 'Tue-Sat: 11am-9pm, Sun: 12pm-8pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-005', 'Green Dot Stables', 'Hip slider joint in Corktown serving creative mini burgers, mac and cheese, and local beers', 'active', '2200 W Lafayette Blvd', 'Detroit', 'Corktown', '48216', 'MI', 'US', '313-962-5588', '42.3296', '-83.0652', 'https://www.greendotstables.com', 'https://facebook.com/greendotstables', 'https://instagram.com/greendotstables', 'Mon-Thu: 11am-11pm, Fri-Sat: 11am-12am, Sun: 10am-10pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-006', 'Prime + Proper', 'Upscale steakhouse with a sophisticated atmosphere, dry-aged beef, and extensive wine list', 'active', '1145 Griswold St', 'Detroit', 'Downtown', '48226', 'MI', 'US', '313-636-3100', '42.3323', '-83.0483', 'https://www.primeandproperdetroit.com', 'https://facebook.com/primeandproperdetroit', 'https://instagram.com/primeandproperdetroit', 'Mon-Thu: 5pm-10pm, Fri-Sat: 5pm-11pm, Sun: 5pm-9pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-007', 'Dime Store', 'All-day breakfast and brunch spot in downtown Detroit with creative dishes and local ingredients', 'active', '719 Griswold St', 'Detroit', 'Downtown', '48226', 'MI', 'US', '313-962-9106', '42.3316', '-83.0477', 'https://www.eatdimestore.com', 'https://facebook.com/dimestore', 'https://instagram.com/dimestore', 'Mon-Fri: 7am-3pm, Sat-Sun: 8am-3pm', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('loc-008', 'Closed Test Restaurant', 'This location is used for testing the temporarily_closed status', 'temporarily_closed', '123 Test St', 'Detroit', 'Test Area', '48201', 'MI', 'US', '313-555-0000', '42.3314', '-83.0458', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

-- Insert test menus
INSERT INTO detroitchow.menus (menu_link, locationid, image, priority, descr, create_date, create_user, updated_date, update_user)
VALUES
    ('https://www.coneydetroit.com/menu.pdf', 'loc-001', 'https://www.coneydetroit.com/images/menu-thumb.jpg', 1, 'Full Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.buddyspizza.com/menu/pizza', 'loc-002', NULL, 1, 'Pizza Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('https://www.buddyspizza.com/menu/appetizers', 'loc-002', NULL, 2, 'Appetizers & Salads', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.seldenstandard.com/menus/dinner', 'loc-003', NULL, 1, 'Dinner Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('https://www.seldenstandard.com/menus/drinks', 'loc-003', NULL, 2, 'Cocktail Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.supinopizzeria.com/menu.pdf', 'loc-004', 'https://www.supinopizzeria.com/images/menu.jpg', 1, 'Pizza & Salads', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.greendotstables.com/food-menu', 'loc-005', NULL, 1, 'Sliders Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('https://www.greendotstables.com/drink-menu', 'loc-005', NULL, 2, 'Drinks', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.primeandproperdetroit.com/menus/dinner.pdf', 'loc-006', NULL, 1, 'Dinner Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('https://www.primeandproperdetroit.com/menus/wine.pdf', 'loc-006', NULL, 2, 'Wine List', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),

    ('https://www.eatdimestore.com/breakfast', 'loc-007', NULL, 1, 'Breakfast Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('https://www.eatdimestore.com/lunch', 'loc-007', NULL, 2, 'Lunch Menu', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

-- Insert test Google Places data
INSERT INTO detroitchow.google_places (locationid, place_id, lat, lng, phone1, formatted_address, website, google_url, business_status, created_at)
VALUES
    ('loc-001', 'ChIJN5X_gFcJO4gRZn0F5hC0k9c', '42.3297', '-83.0458', '+13139648198', '118 W Lafayette Blvd, Detroit, MI 48226', 'https://www.coneydetroit.com', 'https://maps.google.com/?cid=123456789', 'OPERATIONAL', CURRENT_TIMESTAMP),

    ('loc-002', 'ChIJxYx-YnYJO4gR5_8h5zQN2qM', '42.4043', '-83.0307', '+13138929001', '17125 Conant St, Detroit, MI 48212', 'https://www.buddyspizza.com', 'https://maps.google.com/?cid=987654321', 'OPERATIONAL', CURRENT_TIMESTAMP),

    ('loc-003', 'ChIJ_e3KLMwKO4gR8pPw5YhN1dU', '42.3516', '-83.0632', '+13134385055', '3921 2nd Ave, Detroit, MI 48201', 'https://www.seldenstandard.com', 'https://maps.google.com/?cid=456789123', 'OPERATIONAL', CURRENT_TIMESTAMP);
