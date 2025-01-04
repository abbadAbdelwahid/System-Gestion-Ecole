-- Insert students
INSERT INTO etudiants (matricule, nom, prenom, date_naissance, email, promotion)
VALUES
    ('100001', 'Al-Saadi', 'Mohamed', '2000-05-12', 'mohamed.saadi@example.com', '2024'),
    ('100002', 'El-Amin', 'Fatima', '1999-10-23', 'fatima.elamin@example.com', '2023'),
    ('100003', 'Ben Youssef', 'Ali', '2001-03-15', 'ali.benyoussef@example.com', '2025'),
    ('100004', 'Bouzid', 'Khadija', '2000-07-20', 'khadija.bouzid@example.com', '2024'),
    ('100005', 'Chérif', 'Samir', '2002-01-05', 'samir.cherif@example.com', '2025'),
    ('100006', 'Mehdi', 'Oussama', '2001-08-14', 'mehdi.oussama@example.com', '2024'),
    ('100007', 'Hanafi', 'Mouna', '1998-12-03', 'hanafi.mouna@example.com', '2023'),
    ('100008', 'Jabari', 'Amine', '2002-04-27', 'jabari.amine@example.com', '2025'),
    ('100009', 'Zahidi', 'Nadia', '2000-06-10', 'zahidi.nadia@example.com', '2024'),
    ('100010', 'Fellah', 'Karim', '1999-11-22', 'fellah.karim@example.com', '2023');

-- Insert professors
INSERT INTO professeurs (nom, prenom, specialite)
VALUES
    ('Bouaziz', 'Rachid', 'Bases de Données'),
    ('Mansouri', 'Amina', 'Réseaux Informatiques'),
    ('Khaldi', 'Salim', 'Développement Logiciel'),
    ('Zouaoui', 'Layla', 'Sécurité Informatique'),
    ('Ben Ali', 'Omar', 'Intelligence Artificielle');

-- Insert modules
INSERT INTO modules (nom_module, code_module, professeur_id)
VALUES
    ('Bases de Données', 'BD101', 1),
    ('Réseaux Informatiques', 'NET202', 2),
    ('Développement Web', 'WEB303', 3),
    ('Sécurité Informatique', 'SEC404', 4),
    ('Machine Learning', 'ML505', 5);

-- Insert inscriptions (at least 5 students per module)
-- Module: Bases de Données
INSERT INTO inscriptions (etudiant_id, module_id, date_inscription)
VALUES
    (1, 1, '2023-09-01'),
    (2, 1, '2023-09-02'),
    (3, 1, '2023-09-03'),
    (4, 1, '2023-09-04'),
    (5, 1, '2023-09-05');

-- Module: Réseaux Informatiques
INSERT INTO inscriptions (etudiant_id, module_id, date_inscription)
VALUES
    (1, 2, '2023-09-01'),
    (6, 2, '2023-09-01'),
    (7, 2, '2023-09-02'),
    (8, 2, '2023-09-03'),
    (9, 2, '2023-09-04'),
    (10, 2, '2023-09-05');

-- Module: Développement Web
INSERT INTO inscriptions (etudiant_id, module_id, date_inscription)
VALUES
    (1, 3, '2023-09-01'),
    (2, 3, '2023-09-02'),
    (3, 3, '2023-09-03'),
    (4, 3, '2023-09-04'),
    (5, 3, '2023-09-05');

-- Module: Sécurité Informatique
INSERT INTO inscriptions (etudiant_id, module_id, date_inscription)
VALUES
    (1, 4, '2023-09-01'),
    (6, 4, '2023-09-01'),
    (7, 4, '2023-09-02'),
    (8, 4, '2023-09-03'),
    (9, 4, '2023-09-04'),
    (10, 4, '2023-09-05');

-- Module: Machine Learning
INSERT INTO inscriptions (etudiant_id, module_id, date_inscription)
VALUES
    (1, 5, '2023-09-01'),
    (2, 5, '2023-09-02'),
    (3, 5, '2023-09-03'),
    (4, 5, '2023-09-04'),
    (5, 5, '2023-09-05');

-- Insert users
INSERT INTO utilisateurs (username, password, role)
VALUES
    ('admin', '$2a$10$paON8g8govlrodgTrQBNf.KT8b2zDDrH0goOEYvVnRtkD02yqOEBy', 'ADMIN'),
    ('prof_rachid', '$2a$10$s7ncI/hN1nB1sEqHdvULTO1u8cCIvtRClvlEdpGs97gP4jC545kbW', 'PROFESSOR'),
    ('sec_amina', '$2a$10$wzEsIS6T6jsjdxDPRYr4K.FGDMHapjiqqhheA//tytceO1IEON5Ya', 'SECRETARY'),
    ('prof_salim', '$2a$10$YTobVrF9x4aXKyFlIP.Nr.LlBheCJLU9861UakkqzncOC4dFAv4p6', 'PROFESSOR'),
    ('prof_omar', '$2a$10$d4tv05Ij.0Os26VM/aGHGue37vefKcr7qFgcDNVwb0o8gR9ce.dLi', 'PROFESSOR');
