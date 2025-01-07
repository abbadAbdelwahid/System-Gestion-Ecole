package gestion.ecole.dao;

import gestion.ecole.models.Etudiant;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO implements CRUD<Etudiant> {

    private final Connection connection;

    public EtudiantDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    // **Insertion d'un étudiant**
    @Override
    public boolean insert(Etudiant etudiant) {
        try {
            String query = "INSERT INTO etudiants (matricule, nom, prenom, date_naissance, email, promotion) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setDate(4, Date.valueOf(etudiant.getDateNaissance()));
            ps.setString(5, etudiant.getEmail());
            ps.setString(6, etudiant.getPromotion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting Etudiant: " + e.getMessage());
            return false;
        }
    }

    // **Récupération d'un étudiant par ID**
    @Override
    public Etudiant get(int id) {
        try {
            String query = "SELECT * FROM etudiants WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Etudiant: " + e.getMessage());
        }
        return null;
    }

    // **Récupération de tous les étudiants**
    @Override
    public List<Etudiant> getAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        try {
            String query = "SELECT * FROM etudiants";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all Etudiants: " + e.getMessage());
        }
        return etudiants;
    }

    // **Mise à jour d'un étudiant**
    @Override
    public boolean update(Etudiant etudiant) {
        try {
            String query = "UPDATE etudiants SET matricule = ?, nom = ?, prenom = ?, date_naissance = ?, email = ?, promotion = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setDate(4, Date.valueOf(etudiant.getDateNaissance()));
            ps.setString(5, etudiant.getEmail());
            ps.setString(6, etudiant.getPromotion());
            ps.setInt(7, etudiant.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating Etudiant: " + e.getMessage());
            return false;
        }
    }

    // **Suppression d'un étudiant par ID**
    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM etudiants WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting Etudiant: " + e.getMessage());
            return false;
        }
    }

    // **Recherche avancée d'étudiants par mot-clé (nom, matricule, promotion, email)**
    public List<Etudiant> search(String keyword) {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM etudiants WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(matricule) LIKE ? OR LOWER(email) LIKE ? OR LOWER(promotion) LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, searchPattern);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error searching Etudiant: " + e.getMessage());
        }
        return etudiants;
    }

    public Etudiant getByMatricule(String matricule) {
        try {
            String query = "SELECT * FROM etudiants WHERE matricule = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, matricule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Etudiant by matricule: " + e.getMessage());
        }
        return null;
    }

    // **Récupération des étudiants par module**
    public List<Etudiant> getStudentsByModuleId(int moduleId) {
        List<Etudiant> students = new ArrayList<>();
        try {
            String query = "SELECT e.* FROM etudiants e " +
                    "JOIN inscriptions i ON e.id = i.etudiant_id " +
                    "WHERE i.module_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, moduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students by module ID: " + e.getMessage());
        }
        return students;
    }

    public List<Etudiant> advancedSearch(int moduleId, String searchItem) {
        List<Etudiant> etudiants = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT e.* " +
                    "FROM etudiants e, inscriptions i, modules m " +
                    "WHERE e.id = i.etudiant_id " +
                    "AND i.module_id = m.id " +
                    "AND i.module_id = ? " +
                    "AND (LOWER(e.matricule) LIKE ? OR " +
                    "     LOWER(e.nom) LIKE ? OR " +
                    "     LOWER(e.prenom) LIKE ? OR " +
                    "     LOWER(e.email) LIKE ? OR " +
                    "     LOWER(e.promotion) LIKE ?)";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, moduleId);
            String likePattern = "%" + searchItem.toLowerCase() + "%";
            for (int i = 2; i <= 6; i++) {
                ps.setString(i, likePattern);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("promotion")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error performing advanced search: " + e.getMessage());
        }
        return etudiants;
    }

}
