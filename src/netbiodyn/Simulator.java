/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import netbiodyn.ihm.IhmListener;
import netbiodyn.ihm.Env_Parameters;
import netbiodyn.util.RandomGen;
import netbiodyn.util.UtilPoint3D;

/**
 *
 * @author riviere
 */
public class Simulator {

    private final EventListenerList listeners;

    private int speed = 1;
    private int time = 0;
    private boolean pause = false;
    private boolean stopped = true;
    private int maxStep = -1;
    private final Model model;
    private javax.swing.Timer timer_play;

    private AllInstances instances;
    private AllInstances instancesFutur;

    private int nb_processus_a_traiter = 0;

    public Simulator(Model model) {
        listeners = new EventListenerList();

        this.model = model;
        init();
        initTimer();
    }

    private void init() {
        instances = new AllInstances(model.getInstances());
    }

    public void addListener(IhmListener listen) {
        listeners.add(IhmListener.class, listen);
    }

    private void initTimer() {
        timer_play = new javax.swing.Timer(this.getSpeed(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
    }

    public void start() {
        if (getTime() == 0) {
            init();
        }
        setStopped(false);
        setPause(false);
    }

    public void stop() {
        setStopped(true);
        timer_play.stop();
        init();
        pause = false;
        setTime(0);

        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), this.updateList(), getTime());
        }
    }

    private void play() {
        // 1/2 vie
        // --------
        for (int pos_in_list = instances.getSize() - 1; pos_in_list >= 0; pos_in_list--) {
            InstanceAgent c = instances.getInList(pos_in_list);//_matrice_cubes[i, j];
            // Gestion de la demie-vie
            if (c.getDemie_vie() > 0 && !c.isSelectionne()) {
                double proba_mort = 1 - Math.pow(0.5, 1.0 / c.getDemie_vie());
                if (RandomGen.getInstance().nextDouble() < proba_mort) {
                    instances.removeReaxel(c);
                }
            }
        }

        // ***************************************
        // Prise en compte des diverses reactions
        // ***************************************
        // Liste de ts les moteurs de reactions
        List<Behavior> lst_react = model.getListManipulesReactions();
        // RAZ de la matrice future des reaxels        
        Env_Parameters param = model.getParameters();
        instancesFutur = new AllInstances(param.getX(), param.getY(), param.getZ());

        // Lancement en // de toutes les reactions
        setNb_processus_a_traiter(lst_react.size());

        final Simulator s = this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (Behavior lst_react1 : lst_react) {
                    lst_react1.computeReactions(s, param, instances, getTime());
                }
            }
        }).start();

        // Attente passive de tous les retours
        waitForProcess();
        if (nb_processus_a_traiter != 0) {
            System.err.println("Il Manque des réactions !!!!!!!!! " + nb_processus_a_traiter);
        }

        // Concatenation de toutes les reactions possibles
        ArrayList<InstanceBehavior> lst_rp = new ArrayList<>();
        for (Behavior lst_react1 : lst_react) {
            lst_rp.addAll(lst_react1.getReactionsPossibles());
        }

        // Index de parcours melanges
        ArrayList<Integer> lst_int = RandomGen.getInstance().liste_entiers_melanges(lst_rp.size());
        // Execution du choix effectif des reactions
        for (int r = 0; r < lst_rp.size(); r++) {
            InstanceBehavior reaction_possible = lst_rp.get(lst_int.get(r));
            // On tente d'appliquer la transformation
            boolean possible = true;

            if (reaction_possible._type != 2) { // Reaction situees et semi-situee
                // Reactifs tjs présents ?
                for (int i = 0; i < reaction_possible._reactifs_noms.size(); i++) {
                    int x = reaction_possible._reactifs_pos.get(i).x;
                    int y = reaction_possible._reactifs_pos.get(i).y;
                    int z = reaction_possible._reactifs_pos.get(i).z;
                    // Cas du vide
                    if (instances.getFast(x, y, z) == null) {
                        if (!reaction_possible._reactifs_noms.get(i).equals("0")) { // La reaction veut un vide sinon...
                            possible = false;
                            i = reaction_possible._reactifs_noms.size();
                        }
                    } else // cas non vide
                    {
                        if (!instances.getFast(x, y, z).getNom().equals(reaction_possible._reactifs_noms.get(i))) { // La reaction peut trouver le bon nom de reaxel ds la présente matrice
                            possible = false;
                            i = reaction_possible._reactifs_noms.size(); // mais s'il n'y est pas, il n'y est pas
                        }
                    }
                }

                // Place encore là pour les produits ?
                if (possible == true) {
                    for (int i = 0; i < reaction_possible._produits_noms.size(); i++) {
                        int x = reaction_possible._produits_pos.get(i).x;
                        int y = reaction_possible._produits_pos.get(i).y;
                        int z = reaction_possible._produits_pos.get(i).z;
                        if (instancesFutur.getFast(x, y, z) != null) { // Si espace occupé...
                            if (!reaction_possible._produits_noms.get(i).equals("")) { // et si volonté d'y placer un produit alors impossible !
                                possible = false;
                                i = reaction_possible._produits_noms.size();
                            }
                        }
                    }
                }

                // On effectue la reaction si tjs possible
                if (possible == true) {
                    // on ajoute les produits dans la matrice future
                    for (int p = 0; p < reaction_possible._produits_noms.size(); p++) { // p = numero du produit (0,1 ou 2)
                        String nom = reaction_possible._produits_noms.get(p);
                        int xprod = reaction_possible._produits_pos.get(p).x;
                        int yprod = reaction_possible._produits_pos.get(p).y;
                        int zprod = reaction_possible._produits_pos.get(p).z;
                        // Choix de l'age (pour la continuite entre un reactif et un produit)
                        double age = 0;
                        int reactif_origine = reaction_possible._behavior._origine[p]; // -1 => pas d'origine, 0 => origine reactif 0, etc
                        if( reactif_origine >= 0 && reactif_origine < reaction_possible._reactifs_noms.size() ) {
                            int xr = reaction_possible._reactifs_pos.get(reactif_origine).x;
                            int yr = reaction_possible._reactifs_pos.get(reactif_origine).y;
                            int zr = reaction_possible._reactifs_pos.get(reactif_origine).z;
                            InstanceAgent reactif = instances.getFast(xr, yr, zr);
                            if ( reactif != null) {                                
                                age = reactif.age;
                            }
                        }
                        
                        this.AjouterFuturReaxel(xprod, yprod, zprod, nom, age);
                    }

                    // on enleve les reactifs de la matrice courante pour eviter qu'il ne reagissent a nouveau (conservation E et matiere)
                    for (int i = 0; i < reaction_possible._reactifs_noms.size(); i++) {
                        int x = reaction_possible._reactifs_pos.get(i).x;
                        int y = reaction_possible._reactifs_pos.get(i).y;
                        int z = reaction_possible._reactifs_pos.get(i).z;
                        if (instances.getFast(x, y, z) != null) {
                            instances.removeReaxel(x, y, z);
                        }
                    }
                }
            }
            // Reaction possible suivante...
        }

        // Fin de l'application effective des reactions
        // Les agents qui n'ont pas reagit voit leur age augmente tout de meme
        ArrayList<InstanceAgent> lst_reax = instances.getList();
        for(int a=0; a<lst_reax.size(); a++)
            lst_reax.get(a).age++;
        // On verse les réaxels qui n'ont pas réagit dans la liste future et dans la matrice future
        instancesFutur.setMatrixAndList(lst_reax);

        instances = new AllInstances(instancesFutur.getList(), instancesFutur.getMatrix(), instancesFutur.getX(), instancesFutur.getY(), instancesFutur.getZ());
        this.setTime(getTime() + 1);
        int t = getTime();
        instancesFutur.clear();

        if (getMaxStep() != -1) {
            if (getTime() == getMaxStep()) {
                setStopped(true);
                timer_play.stop();
                pause = false;
                setMaxStep(-1);
            }
        }

        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), this.updateList(), t);
        }

    }

    public void play_one() {
        if (this.getTime() == 0) {
            init();
        }
        this.play();
    }

    private synchronized void waitForProcess() {
        if (getNb_processus_a_traiter() != 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Used to automatically remove all instances of the entities in reaxels
     *
     * @param reaxels
     */
    public void ProtoReaxelDeleted(ArrayList<String> reaxels) {
        for (String p : reaxels) {
            this.removeEntityInstances(p);
        }
        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), updateList(), getTime());
        }
    }

    /**
     *
     * @param entities
     * @return
     */
    public HashMap<String, Integer> updateList() {
        HashMap<String, Integer> entities = instances.getBook();
        ArrayList<Agent> reaxels = model.getListManipulesNoeuds();
        for (Agent entity : reaxels) {
            if (entities.containsKey(entity._etiquettes) == false) {
                entities.put(entity._etiquettes, 0);
            }
        }

        return entities;
    }

    private void removeEntityInstances(String nom) {
        for (int c = instances.getSize() - 1; c >= 0; c--) {
            InstanceAgent cube = instances.getInList(c);
            if (cube.getNom().equals(nom)) {
                instances.removeReaxel(cube);
            }
        }
    }

    public void addEntityInstances(ArrayList<UtilPoint3D> points, String etiquette) {
        boolean toUpdate = false;
        for (UtilPoint3D point : points) {
            int x = point.x;
            int y = point.y;
            int z = point.z;
            if (this.AjouterReaxel(x, y, z, etiquette)) {
                toUpdate = true;
            }
        }
        if (toUpdate) {
            for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
                listen.matrixUpdate(getInstances(), updateList(), getTime());
            }
        }
    }

    public void removeEntityInstance(int x, int y, int z) {
        boolean changed = instances.removeReaxel(x, y, z);
        if (changed) {
            for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
                listen.matrixUpdate(getInstances(), updateList(), getTime());
            }
        }
    }

    public void removeManyEntitiyInstance(ArrayList<UtilPoint3D> points) {
        boolean changed = false;
        for (UtilPoint3D p : points) {
            int x = p.x;
            int y = p.y;
            int z = p.z;
            if (instances.removeReaxel(x, y, z)) {
                changed = true;
            }
        }
        if (changed) {
            for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
                listen.matrixUpdate(getInstances(), updateList(), getTime());
            }
        }
    }

    private boolean AjouterReaxel(int i, int j, int k, String etiquette) {
        boolean changed = false;
        ArrayList<Agent> reaxels = model.getListManipulesNoeuds();
        for (int n = 0; n < reaxels.size(); n++) {
            if (reaxels.get(n).TrouveEtiquette(etiquette) >= 0) {
                InstanceAgent r = InstanceAgent.CreerReaxel(reaxels.get(n));
                r.setX(i);
                r.setY(j);
                r.setZ(k);
                changed = instances.addReaxel(r);
                n = reaxels.size();
            }
        }
        return changed;
    }

    public String getType(int x, int y, int z) {
        InstanceAgent r = instances.getFast(x, y, z);
        if (r != null) {
            return r.getNom();
        } else {
            return "";
        }
    }

    private boolean AjouterFuturReaxel(int i, int j, int k, String etiquette, double age) {
        boolean changed = false;
        ArrayList<Agent> lst_reaxels = model.getListManipulesNoeuds();
        for (int n = 0; n < lst_reaxels.size(); n++) {
            if (lst_reaxels.get(n).TrouveEtiquette(etiquette) >= 0) {
                InstanceAgent r = InstanceAgent.CreerReaxel(lst_reaxels.get(n));
                r.setX(i);
                r.setY(j);
                r.setZ(k);
                r.age = age+1;
                changed = instancesFutur.addReaxel(r);
                n = lst_reaxels.size();
            }
        }
        return changed;
    }

    public void ProtoReaxelEdited(Agent entity, String old_name) {
        instances.editReaxels(entity, old_name);
        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), updateList(), getTime());
        }
    }

    public boolean isRunning() {
        return timer_play.isRunning();
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        if (isPause()) {
            timer_play.stop();
        } else {
            timer_play.start();
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if (!isStopped()) {
            timer_play.stop();
            initTimer();
            if (!isPause()) {
                timer_play.start();
            }
        } else {
            initTimer();
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNb_processus_a_traiter() {
        return nb_processus_a_traiter;
    }

    public void setNb_processus_a_traiter(int nb_processus_a_traiter) {
        this.nb_processus_a_traiter = nb_processus_a_traiter;
    }

    public synchronized void decrementer_nb_processus_a_traiter() {
        setNb_processus_a_traiter(getNb_processus_a_traiter() - 1);
        if (getNb_processus_a_traiter() == 0) {
            notify();
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }

    public AllInstances getInstances() {
        return instances;
    }

}
