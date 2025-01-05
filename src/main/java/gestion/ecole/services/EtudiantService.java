package gestion.ecole.services;

import gestion.ecole.dao.EtudiantDAO;
import gestion.ecole.models.Etudiant;
import gestion.ecole.models.Module;

import java.util.ArrayList;
import java.util.List;

public class EtudiantService {
    public List<Etudiant> getStudentsByModule(int moduleId) {
        EtudiantDAO etudiantDAO = new EtudiantDAO();
        return etudiantDAO.getStudentsByModuleId(moduleId);
    }
    public List<Etudiant> searchInStudentsByModule(int moduleId,String searchItem){
        EtudiantDAO etudiantDAO = new EtudiantDAO();
        return etudiantDAO.advancedSearch(moduleId,searchItem);
    }
    public List<Etudiant> getStudentsByProfessor(int professorId) {

        ModuleService moduleService = new ModuleService();
        List<Module> modules = moduleService.getModulesByProfessor(professorId);

        EtudiantDAO etudiantDAO = new EtudiantDAO();
        List<Etudiant> allStudents = new ArrayList<>();
        for (Module module : modules) {
            allStudents.addAll(etudiantDAO.getStudentsByModuleId(module.getId()));
        }
        return allStudents;
    }


}
