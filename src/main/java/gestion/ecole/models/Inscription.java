package gestion.ecole.models;

import java.time.LocalDate;

public class Inscription {
    private int id;
    private int etudiantId;
    private int moduleId;
    private LocalDate dateInscription;


    public Inscription(int id, int etudiantId, int moduleId, LocalDate dateInscription) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.moduleId = moduleId;
        this.dateInscription = dateInscription;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }
    public int getModuleId() { return moduleId; }
    public void setModuleId(int moduleId) { this.moduleId = moduleId; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
}
