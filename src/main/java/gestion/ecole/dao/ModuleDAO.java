package gestion.ecole.dao;

import gestion.ecole.models.Module;
import gestion.ecole.utils.DatabaseConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAO implements CRUD<Module> {
    private final Connection connection;

    public ModuleDAO() {
        this.connection = DatabaseConnexion.getInstance().getConnection();
    }

    @Override
    public boolean insert(Module module) {
        try {
            String query = "INSERT INTO modules (nom_module, code_module, professeur_id) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, module.getNomModule());
            ps.setString(2, module.getCodeModule());
            ps.setInt(3, module.getProfesseurId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting Module: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Module get(int id) {
        try {
            String query = "SELECT * FROM modules WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Module(
                        rs.getInt("id"),
                        rs.getString("nom_module"),
                        rs.getString("code_module"),
                        rs.getInt("professeur_id")
                );
            }
        } catch (Exception e) {
            System.out.println("Error fetching Module: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Module> getAll() {
        List<Module> modules = new ArrayList<>();
        try {
            String query = "SELECT * FROM modules";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modules.add(new Module(
                        rs.getInt("id"),
                        rs.getString("nom_module"),
                        rs.getString("code_module"),
                        rs.getInt("professeur_id")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching all Modules: " + e.getMessage());
        }
        return modules;
    }

    @Override
    public boolean update(Module module) {
        try {
            String query = "UPDATE modules SET nom_module = ?, code_module = ?, professeur_id = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, module.getNomModule());
            ps.setString(2, module.getCodeModule());
            ps.setInt(3, module.getProfesseurId());
            ps.setInt(4, module.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating Module: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM modules WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error deleting Module: " + e.getMessage());
            return false;
        }
    }
}
