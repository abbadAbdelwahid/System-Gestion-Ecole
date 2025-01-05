package gestion.ecole.services;

import gestion.ecole.dao.ProfesseurDAO;

public class ProfesseurService {
    private final ProfesseurDAO professeurDAO;

    public ProfesseurService() {
        this.professeurDAO = new ProfesseurDAO();
    }

    // Method to get the professor's ID by the user ID
    public Integer getProfesseurIdByUserId(int userId) {
        return professeurDAO.getProfesseurIdByUserId(userId);
    }
}
