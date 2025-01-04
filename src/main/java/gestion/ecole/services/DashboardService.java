package gestion.ecole.services;

import gestion.ecole.dao.EtudiantDAO;
import gestion.ecole.dao.InscriptionDAO;
import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.dao.ProfesseurDAO;
import javafx.scene.layout.Pane;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {

    private final EtudiantDAO etudiantDAO;
    private final ProfesseurDAO professeurDAO;
    private final ModuleDAO moduleDAO;
    private final InscriptionDAO inscriptionDAO;

    public DashboardService() {
        this.etudiantDAO = new EtudiantDAO();
        this.professeurDAO = new ProfesseurDAO();
        this.moduleDAO = new ModuleDAO();
        this.inscriptionDAO = new InscriptionDAO();
    }

    public int getTotalEtudiants() {
        return etudiantDAO.getAll().size();
    }

    public int getTotalProfesseurs() {
        return professeurDAO.getAll().size();
    }

    public int getTotalModules() {
        return moduleDAO.getAll().size();
    }

    public String getMostPopularModule() {
        String module = inscriptionDAO.getMostPopularModule();
        return module != null ? module : "Aucun module trouvé";
    }

    public String getMostActiveProfessor() {
        String professor = moduleDAO.getProfessorWithMostModules();
        return professor != null ? professor : "Aucun professeur trouvé";
    }

    public Map<String, Integer> getModuleStatistics() {
        return inscriptionDAO.getModulesWithStudentCounts();
    }
}
