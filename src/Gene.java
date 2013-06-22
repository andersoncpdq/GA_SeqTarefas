import java.util.Random;

/*
 * Cada Gene representa uma solucao para o problema do sequenciamento de tarefas. A resposta do problema 
 * estah no vetor chamado "Array", no qual os valores do array representam os numeros do Jobs em ordem de 
 * processamento.
 * 
 */

public class Gene {
	
    int array[] ;
    int aptidao = 0;
    int numJobs = 0;
    int numMachs = 0;
    float roletaPercent = 0;
    Random rand = new Random();
    
    public Gene(int nJobs, int nMachs) {
    	
        numJobs = nJobs;
        numMachs = nMachs;
        array = new int[numJobs];
    }

	// Preenche cada solucao aleatoriamente.
    public void preencheGeneAleatoriamente() {
    	
        int k = this.array.length-1; // coloca n-1 elementos no array.
        int i = this.rand.nextInt(this.array.length);
        
        while(k > 0) {
        	
            if(this.array[i] == 0){
                this.array[i] = k--;
                i = this.rand.nextInt(this.array.length);
            } else {
                i = (i < this.array.length-1) ? ++i : 0 ;
            }
        }
    }
    
    public void calculaAptidao(int [][] custos, Gene melhorGene){
    	
    	int result[] = new int[this.numMachs];
    	
    	for (int i = 0; i < this.numMachs; ++i)
    		result[i] = 0;

    	for (int k = 0; k < this.numJobs; ++k) {
    		
    		int j = this.array[k];
    		result[0] = result[0] + custos[0][j];
    		
    		for (int i = 1; i < this.numMachs; ++i) {
    			
    			if(result[i] > result[i-1])
    				result[i] = result[i] + custos[i][j];
    			else
    				result[i] = result[i-1] + custos[i][j];
    		}
    	}
    	
    	this.aptidao = result[numMachs-1];
    	
    	if(this.aptidao < melhorGene.aptidao) {
    		
        	for (int i = 0; i < this.array.length; i++) {
				melhorGene.array[i] = this.array[i];
			}
        	melhorGene.aptidao = this.aptidao;
        	
        	System.out.println(" Melhor Gene: " + melhorGene.aptidao);
        }
    }
    
    public void imprimeGene() {
    	
    	System.out.println();
        for (int i = 0; i < this.array.length; i++)
            System.out.print(this.array[i] + " ");
        System.out.println(" Aptidao:  " + this.aptidao);
    }
}
