package gestion.ecole.services;

import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.models.Module;
import java.util.List;

public class ModuleService {
    private final ModuleDAO moduleDAO;

    public ModuleService() {
        this.moduleDAO = new ModuleDAO();
    }

    // Méthode existante
    public List<Module> getModulesByProfessor(int professorId) {
        return moduleDAO.getModulesByProfessorId(professorId);
    }

    // Nouvelles méthodes nécessaires
    public List<Module> getAll() {
        return moduleDAO.getAll();
    }

    public Module get(int id) {
        return moduleDAO.get(id);
    }
}