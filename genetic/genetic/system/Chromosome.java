package genetic.genetic.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Author Grinch
 * Date: 01.03.2015
 * Time: 19:29
 */
public class Chromosome<A> {
    List<A> chromosome;

    public Chromosome(List<A> init){
        chromosome = new ArrayList(init);
    }

    public void setGene(int index, A value){
        chromosome.set(index, value);
    }

    public A getGene(int index) {
        return chromosome.get(index);
    }

    public List<A> getLeftPart(int separator){
        List<A> list = new ArrayList();
        for (int i = 0; i < separator; i++) {
            list.add(chromosome.get(i));
        }
        return list;
    }

    public List<A> getRightPart(int separator){
        List<A> list = new ArrayList();
        for (int i = separator; i < chromosome.size(); i++){
            list.add(chromosome.get(i));
        }
        return list;
    }

    public int getSize(){
        return chromosome.size();
    }

    public List<A> getChromosome() {
        return chromosome;
    }
}
