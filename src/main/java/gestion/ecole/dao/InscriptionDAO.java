package gestion.ecole.dao;

import gestion.ecole.models.Inscription;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.*;

import java.util.*;



public class InscriptionDAO implements CRUD<Inscription> {
    private final Connection connection;

    public InscriptionDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    @Override
    public boolean insert(Inscription inscription) {
        try {
            String query = "INSERT INTO inscriptions (etudiant_id, module_id, date_inscription) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, inscription.getEtudiantId());
            ps.setInt(2, inscription.getModuleId());
            ps.setDate(3, java.sql.Date.valueOf(inscription.getDateInscription()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting Inscription: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Inscription get(int id) {
        try {
            String query = "SELECT * FROM inscriptions WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Inscription(
                        rs.getInt("id"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("module_id"),
                        rs.getDate("date_inscription").toLocalDate()
                );
            }
        } catch (Exception e) {
            System.out.println("Error fetching Inscription: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Inscription> getAll() {
        List<Inscription> inscriptions = new ArrayList<>();
        try {
            String query = "SELECT * FROM inscriptions";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                inscriptions.add(new Inscription(
                        rs.getInt("id"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("module_id"),
                        rs.getDate("date_inscription").toLocalDate()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching all Inscriptions: " + e.getMessage());
        }
        return inscriptions;
    }

    @Override
    public boolean update(Inscription inscription) {
        try {
            String query = "UPDATE inscriptions SET etudiant_id = ?, module_id = ?, date_inscription = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, inscription.getEtudiantId());
            ps.setInt(2, inscription.getModuleId());
            ps.setDate(3, java.sql.Date.valueOf(inscription.getDateInscription()));
            ps.setInt(4, inscription.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating Inscription: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM inscriptions WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error deleting Inscription: " + e.getMessage());
            return false;
        }
    }
    public String getMostPopularModule() {
        try {
            String query = "SELECT m.nom_module, COUNT(i.module_id) AS enrollments " +
                    "FROM inscriptions i " +
                    "JOIN modules m ON i.module_id = m.id " +
                    "GROUP BY m.nom_module " +
                    "ORDER BY enrollments DESC " +
                    "LIMIT 1";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom_module");
            }
        } catch (Exception e) {
            System.out.println("Error fetching most popular module: " + e.getMessage());
        }
        return null; // Return null if no data is found or an error occurs
    }
    public Map<String, Integer> getModulesWithStudentCounts() {
        Map<String, Integer> moduleStatistics = new HashMap<>();
        try {
            String query = "SELECT m.nom_module, COUNT(i.etudiant_id) AS student_count " +
                    "FROM inscriptions i " +
                    "JOIN modules m ON i.module_id = m.id " +
                    "GROUP BY m.nom_module " +
                    "ORDER BY student_count DESC " +
                    "LIMIT 5";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                moduleStatistics.put(rs.getString("nom_module"), rs.getInt("student_count"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching module statistics: " + e.getMessage());
        }
        return moduleStatistics;
    }



}
