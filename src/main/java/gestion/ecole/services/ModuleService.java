package gestion.ecole.services;

import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.models.Module;
import java.util.List;

public class ModuleService {
    public List<Module> getModulesByProfessor(int professorId) {
        ModuleDAO moduleDAO = new ModuleDAO();
        return moduleDAO.getModulesByProfessorId(professorId);
    }
}
