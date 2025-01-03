package gestion.ecole.dao;

import gestion.ecole.models.Utilisateur;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO implements CRUD<Utilisateur> {
    private final Connection connection;

    public UtilisateurDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    @Override
    public boolean insert(Utilisateur utilisateur) {
        try {
            String query = "INSERT INTO utilisateurs (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, utilisateur.getUsername());
            ps.setString(2, utilisateur.getPassword()); // Assume password is already hashed
            ps.setString(3, utilisateur.getRole());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting Utilisateur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Utilisateur get(int id) {
        try {
            String query = "SELECT * FROM utilisateurs WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.out.println("Error fetching Utilisateur by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Utilisateur> getAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            String query = "SELECT * FROM utilisateurs";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching all Utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }

    @Override
    public boolean update(Utilisateur utilisateur) {
        try {
            String query = "UPDATE utilisateurs SET username = ?, password = ?, role = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, utilisateur.getUsername());
            ps.setString(2, utilisateur.getPassword()); // Assume password is already hashed
            ps.setString(3, utilisateur.getRole());
            ps.setInt(4, utilisateur.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating Utilisateur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM utilisateurs WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error deleting Utilisateur: " + e.getMessage());
            return false;
        }
    }

    public Utilisateur getByUsername(String username) {
        try {
            String query = "SELECT * FROM utilisateurs WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.out.println("Error fetching Utilisateur by username: " + e.getMessage());
        }
        return null;
    }

    public boolean usernameExists(String username) {
        try {
            String query = "SELECT COUNT(*) AS count FROM utilisateurs WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }
}
