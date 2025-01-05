package gestion.ecole.dao;

import gestion.ecole.models.Etudiant;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.*;
import java.util.*;


public class EtudiantDAO implements CRUD<Etudiant> {
    private final Connection connection;

    public EtudiantDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    @Override
    public boolean insert(Etudiant etudiant) {
        try {
            String query = "INSERT INTO etudiants (matricule, nom, prenom, date_naissance, email, promotion) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setDate(4, java.sql.Date.valueOf(etudiant.getDateNaissance()));
            ps.setString(5, etudiant.getEmail());
            ps.setString(6, etudiant.getPromotion());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting Etudiant: " + e.getMessage());
            return false;
        }
    }

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
        } catch (Exception e) {
            System.out.println("Error fetching Etudiant: " + e.getMessage());
        }
        return null;
    }

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
        } catch (Exception e) {
            System.out.println("Error fetching all Etudiants: " + e.getMessage());
        }
        return etudiants;
    }

    @Override
    public boolean update(Etudiant etudiant) {
        try {
            String query = "UPDATE etudiants SET matricule = ?, nom = ?, prenom = ?, date_naissance = ?, email = ?, promotion = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setDate(4, java.sql.Date.valueOf(etudiant.getDateNaissance()));
            ps.setString(5, etudiant.getEmail());
            ps.setString(6, etudiant.getPromotion());
            ps.setInt(7, etudiant.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating Etudiant: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM etudiants WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error deleting Etudiant: " + e.getMessage());
            return false;
        }
    }
    public List<Etudiant> advancedSearch(String searchItem) {
        List<Etudiant> etudiants = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT e.* " +
                    "FROM etudiants e, inscriptions i, modules m " +
                    "WHERE e.id = i.etudiant_id " +
                    "AND i.module_id = m.id " +
                    "AND (e.matricule LIKE ? OR " +
                    "     e.nom LIKE ? OR " +
                    "     e.prenom LIKE ? OR " +
                    "     e.email LIKE ? OR " +
                    "     e.promotion LIKE ? OR " +
                    "     m.nom_module LIKE ?)";

            PreparedStatement ps = connection.prepareStatement(query);
            String likePattern = "%" + searchItem + "%";
            for (int i = 1; i <= 6; i++) {
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
            System.out.println("Error performing smart search: " + e.getMessage());
        }
        return etudiants;
    }
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
        } catch (Exception e) {
            System.out.println("Error fetching students by module ID: " + e.getMessage());
        }
        return students;
    }


}
