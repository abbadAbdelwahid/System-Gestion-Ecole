package gestion.ecole.services;

import gestion.ecole.dao.UtilisateurDAO;
import gestion.ecole.models.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UtilisateurService {
    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {

        this.utilisateurDAO = new UtilisateurDAO();
    }


    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public boolean addUser(String username, String plainPassword, String role) {
        String hashedPassword = hashPassword(plainPassword);
        Utilisateur utilisateur = new Utilisateur(0, username, hashedPassword, role);
        return utilisateurDAO.insert(utilisateur);
    }

    public boolean updateUser(int id, String username, String plainPassword, String role) {
        String hashedPassword = hashPassword(plainPassword);
        Utilisateur utilisateur = new Utilisateur(id, username, hashedPassword, role);
        return utilisateurDAO.update(utilisateur);
    }


    public Utilisateur getUserById(int id) {
        return utilisateurDAO.get(id);
    }

    public List<Utilisateur> getAllUsers() {
        return utilisateurDAO.getAll();
    }

    public boolean usernameExists(String username) {
        return utilisateurDAO.usernameExists(username);
    }

    public boolean authenticateUser(String username, String plainPassword) {
        Utilisateur utilisateur = utilisateurDAO.getByUsername(username);
        if (utilisateur == null) {
            return false;
        }
        return verifyPassword(plainPassword, utilisateur.getPassword());
    }
    public Utilisateur getUserByUsername(String username) {
        return utilisateurDAO.getByUsername(username);
    }

}
