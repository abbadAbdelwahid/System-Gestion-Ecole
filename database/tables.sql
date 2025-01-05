-- Create table for students
CREATE TABLE etudiants (
                           id SERIAL PRIMARY KEY,
                           matricule VARCHAR(50) NOT NULL UNIQUE,
                           nom VARCHAR(100) NOT NULL,
                           prenom VARCHAR(100) NOT NULL,
                           date_naissance DATE NOT NULL,
                           email VARCHAR(100) NOT NULL UNIQUE,
                           promotion VARCHAR(50) NOT NULL
);
-- Create table for users
CREATE TABLE utilisateurs (
                              id SERIAL PRIMARY KEY,
                              username VARCHAR(50) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL -- Example values: 'admin', 'professor', 'student', etc.
);

-- Create table for professors
CREATE TABLE professeurs (
                             id SERIAL PRIMARY KEY,
                             utilisateur_id INT UNIQUE NOT NULL REFERENCES utilisateurs(id) ON DELETE CASCADE, -- Link to utilisateurs table
                             nom VARCHAR(100) NOT NULL,
                             prenom VARCHAR(100) NOT NULL,
                             specialite VARCHAR(100) NOT NULL
);

-- Create table for modules
CREATE TABLE modules (
                         id SERIAL PRIMARY KEY,
                         nom_module VARCHAR(100) NOT NULL UNIQUE,
                         code_module VARCHAR(50) NOT NULL UNIQUE,
                         professeur_id INT REFERENCES professeurs(id) ON DELETE CASCADE
);

-- Create table for inscriptions
CREATE TABLE inscriptions (
                              id SERIAL PRIMARY KEY,
                              etudiant_id INT REFERENCES etudiants(id) ON DELETE CASCADE,
                              module_id INT REFERENCES modules(id) ON DELETE CASCADE,
                              date_inscription DATE NOT NULL
);


