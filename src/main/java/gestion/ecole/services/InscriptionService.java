package gestion.ecole.services;

import gestion.ecole.dao.InscriptionDAO;
import gestion.ecole.models.Inscription;
import java.util.List;

public class InscriptionService {
    private final InscriptionDAO inscriptionDAO;

    public InscriptionService() {
        this.inscriptionDAO = new InscriptionDAO();
    }

    public List<Inscription> getAll() {
        return inscriptionDAO.getAll();
    }

    public boolean insert(Inscription inscription) {
        return inscriptionDAO.insert(inscription);
    }

    public boolean update(Inscription inscription) {
        return inscriptionDAO.update(inscription);
    }

    public boolean delete(int id) {
        return inscriptionDAO.delete(id);
    }

    public Inscription get(int id) {
        return inscriptionDAO.get(id);
    }
}