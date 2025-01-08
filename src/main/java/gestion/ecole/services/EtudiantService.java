package gestion.ecole.services;

import gestion.ecole.dao.EtudiantDAO;
import gestion.ecole.models.Etudiant;

import java.util.List;

public class EtudiantService {

    private final EtudiantDAO etudiantDAO;

    public EtudiantService() {
        this.etudiantDAO = new EtudiantDAO();
    }

    public Etudiant get(int id) {
        return etudiantDAO.get(id);
    }


    // Méthode pour récupérer tous les étudiants
    public List<Etudiant> getAll() {
        return etudiantDAO.getAll();
    }

    // Méthode pour mettre à jour un étudiant
    public boolean update(Etudiant etudiant) {
        return etudiantDAO.update(etudiant);
    }

    // Méthode pour supprimer un étudiant
    public boolean delete(int id) {
        return etudiantDAO.delete(id);
    }

    // Méthode pour rechercher des étudiants par mot-clé
    public List<Etudiant> search(String keyword) {
        return etudiantDAO.search(keyword);
    }

    public List<Etudiant> getStudentsByModule(int moduleId) {
        return etudiantDAO.getStudentsByModuleId(moduleId);
    }

    public List<Etudiant> searchInStudentsByModule(int moduleId, String searchItem) {
        return etudiantDAO.advancedSearch(moduleId, searchItem);
    }

    // Méthode pour ajouter un étudiant
    public boolean addStudent(Etudiant etudiant) throws Exception {
        if (etudiantDAO.getByMatricule(etudiant.getMatricule()) != null) {
            throw new Exception("L'étudiant avec le matricule " + etudiant.getMatricule() + " existe déjà !");
        }
        return etudiantDAO.insert(etudiant);
    }
}
