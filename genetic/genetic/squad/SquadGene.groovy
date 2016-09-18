package genetic.genetic.squad


class SquadGene {
    int i,j
    int position

    public int[] getIJ(){
        return [i,j];
    }

    SquadGene(int i, int j,int position) {
        this.i = i
        this.j = j
        this.position = position
    }

    @Override
    String toString() {
        return '[' + i + ',' + j + '] ' + position;
    }
}
