package gestion.ecole.services;

import gestion.ecole.dao.UtilisateurDAO;
import gestion.ecole.models.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UtilisateurService {
    private final UtilisateurDAO utilisateurDAO;
    private Utilisateur loggedInUser; // Track the currently logged-in user

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    // Password hashing
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    // Add a new user
    public Utilisateur addUser(String username, String plainPassword, String role) {
        String hashedPassword = hashPassword(plainPassword);
        Utilisateur utilisateur = new Utilisateur(0, username, hashedPassword, role);
        return utilisateurDAO.insert(utilisateur);
    }

    // Update an existing user
    public boolean updateUser(int id, String username, String plainPassword, String role) {
        String hashedPassword = hashPassword(plainPassword);
        Utilisateur utilisateur = new Utilisateur(id, username, hashedPassword, role);
        return utilisateurDAO.update(utilisateur);
    }

    // Get user by ID
    public Utilisateur getUserById(int id) {
        return utilisateurDAO.get(id);
    }

    // Get all users
    public List<Utilisateur> getAllUsers() {
        return utilisateurDAO.getAll();
    }

    // Check if a username already exists
    public boolean usernameExists(String username) {
        return utilisateurDAO.usernameExists(username);
    }

    // Authenticate a user and set the logged-in user
    public boolean authenticateUser(String username, String plainPassword) {
        Utilisateur utilisateur = utilisateurDAO.getByUsername(username);
        if (utilisateur == null) {
            return false;
        }
        if (verifyPassword(plainPassword, utilisateur.getPassword())) {
            this.loggedInUser = utilisateur; // Set the logged-in user
            return true;
        }
        return false;
    }

    // Get the currently logged-in user
    public Utilisateur getLoggedInUser() {
        return loggedInUser;
    }

    // Get the role of the logged-in user
    public String getLoggedInUserRole() {
        if (loggedInUser != null) {
            return loggedInUser.getRole();
        }
        return null;
    }

    // Clear the logged-in user (logout functionality)
    public void logout() {
        loggedInUser = null;
    }

    // Get user by username
    public Utilisateur getUserByUsername(String username) {
        return utilisateurDAO.getByUsername(username);
    }
}
