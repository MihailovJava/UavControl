package genetic.genetic.squad;

import genetic.genetic.system.Chromosome;
import genetic.genetic.system.FitnessFunction;
import genetic.genetic.system.Person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SquadFitnessFunction implements FitnessFunction {
    List<SquadGene> from;
    int row;
    int col;
    double I;
    double D;

    public SquadFitnessFunction(List<SquadGene> from, int row,int col,double I,double D) {
        this.from = from;
        this.row = row;
        this.col = col;
        this.D = D;
        this.I = I;
    }

    @Override
    public double getFitness(Person p) {
        Chromosome<SquadGene> chromosome = p.getChromosome();
        if(checkUnique(chromosome)) {
            double dist = 0;
            for (int i = 0; i < chromosome.getSize(); i++) {
                int[] fromIJ = from.get(i).getIJ();
                int[] geneIJ = chromosome.getGene(i).getIJ();
                int dI = fromIJ[0] - geneIJ[0];
                int dJ = fromIJ[1] - geneIJ[1];
                dist += Math.sqrt(dI * dI * D + dJ * dJ * I);
            }
            return dist;
        }
        return 1234567.89;
    }

    public boolean checkUnique( Chromosome<SquadGene> chromosome){
        Set<Integer> checker = new HashSet<Integer>();
        boolean result = true;
        for (int i = 0; i < chromosome.getSize(); i++) {
            result &= checker.add(
                    getPos(
                            chromosome.getGene(i).getI(),
                            chromosome.getGene(i).getJ()
                    ));
        }
        return result;
    }

    public int getPos(int i, int j){
        return i*col + j;
    }

    public int[] getIJ(int pos){
        int i = pos/col;
        int j = pos - i*col;
        return new int[]{i,j};
    }
}

