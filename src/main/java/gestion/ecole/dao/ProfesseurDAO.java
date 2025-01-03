package gestion.ecole.dao;

import gestion.ecole.models.Professeur;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurDAO implements CRUD<Professeur> {
    private final Connection connection;

    public ProfesseurDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    @Override
    public boolean insert(Professeur professeur) {
        try {
            String query = "INSERT INTO professeurs (nom, prenom, specialite) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, professeur.getNom());
            ps.setString(2, professeur.getPrenom());
            ps.setString(3, professeur.getSpecialite());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting Professeur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Professeur get(int id) {
        try {
            String query = "SELECT * FROM professeurs WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Professeur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                );
            }
        } catch (Exception e) {
            System.out.println("Error fetching Professeur: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Professeur> getAll() {
        List<Professeur> professeurs = new ArrayList<>();
        try {
            String query = "SELECT * FROM professeurs";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                professeurs.add(new Professeur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching all Professeurs: " + e.getMessage());
        }
        return professeurs;
    }

    @Override
    public boolean update(Professeur professeur) {
        try {
            String query = "UPDATE professeurs SET nom = ?, prenom = ?, specialite = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, professeur.getNom());
            ps.setString(2, professeur.getPrenom());
            ps.setString(3, professeur.getSpecialite());
            ps.setInt(4, professeur.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating Professeur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM professeurs WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error deleting Professeur: " + e.getMessage());
            return false;
        }
    }

    public List<Professeur> searchByModule(String moduleName) {
        List<Professeur> professeurs = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT p.* " +
                    "FROM professeurs p, modules m " +
                    "WHERE p.id = m.professeur_id " +
                    "AND m.nom_module LIKE ?";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + moduleName + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                professeurs.add(new Professeur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error searching professors by module: " + e.getMessage());
        }
        return professeurs;
    }
}
