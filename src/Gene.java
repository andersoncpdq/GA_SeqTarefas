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
    
    /* Resposavel pelo processamento do ponto de corte.
     * O range do corte eh escolhido aleatoreamente.
     */
	public Gene[] calculaPontoDeCorte(Gene pai2, int [][] custos) {
	    	
        int tamanhoDoGene = custos.length;
        Gene filhos[] = new Gene[2];
        
        for (int i = 0; i < filhos.length; i++)
			filhos[i] = new Gene(numJobs, numMachs);
        
        int pontoDeCorte = rand.nextInt( tamanhoDoGene );
        //rand.nextInt( tamanhoDoGene/2 ) + tamanhoDoGene/4;
        
        for (int i = 0; i < this.array.length; i++) {
            if( i < pontoDeCorte ){
                filhos[0].array[i] = this.array[i];
                filhos[1].array[i] = pai2.array[i];
            } else {
                filhos[0].array[i] = pai2.array[i];
                filhos[1].array[i] = this.array[i]; 
            }
        }
        
        filhos[0].balanceiaGene();
        filhos[1].balanceiaGene();
        return filhos;
    }
	
	/*
     * O ponto de corte pode causar desbalanceamento dos genes, por exemplo:
     * 
     * O gene [0,1,2,3,4,5] cruzado com o gene [5,4,3,2,1,0] com ponto de corte na 3a casa daria como resultado o filho:
     * [0,1,2,2,1,0 ], que obviamente nao eh uma solucao valida. Esse metodo escolhe um dos Jobs repetidos aleatoreamente
     * e a substitui por um dos Jobs que nao esta presente no conjunto solucao.
     *  
     * Para o filho gerado acima, uma possibilidade de balanceamento seria: [5,1,2,4,3,0]
     */
	public void balanceiaGene() {
    	
    	//System.out.println( "\nGene no balanceamento: " );
    	int i, j = 0;
    	/*for(i = 0; i < this.array.length; i++) {
    		System.out.print(  g.array[i] +" " );
		}*/
    	int cidadesRepetidas[] = new int[this.array.length];
        int indiceCidadesRepetidas[][] = new int[this.array.length][2];
        
        for(i = 0; i < indiceCidadesRepetidas.length; i++) {
            for(j = 0; j < indiceCidadesRepetidas[0].length; j++) {
                indiceCidadesRepetidas[i][j] = -1;
            }
        }
        
        int indice2, valorOposto;
        
        for(i = 0; i < this.array.length; i++) {
        	
            cidadesRepetidas[ this.array[i] ] ++;
            indice2 = indiceCidadesRepetidas[this.array[i]][0] == -1 ? 0 : 1;
            indiceCidadesRepetidas[this.array[i]][indice2] = i;
            
            if(cidadesRepetidas[ this.array[i] ] > 3) 
                throw new Error("Erro no balanceiaGene: cromossomo array["+this.array[i]+"] com mais de um Job repetido.");
        }

        for ( i = 0; i < cidadesRepetidas.length; i++) {
            if( cidadesRepetidas[i] == 2  || cidadesRepetidas[i] == 0 ){
                int indicePrincipal, indiceSecundario;
  
                for ( j = i+1 ; j < cidadesRepetidas.length; j++) {
                    if( cidadesRepetidas[i] == 2 ){
                    	valorOposto = 0;
                        indicePrincipal = i;
                        indiceSecundario = j;
                    }else{
                    	valorOposto = 2;
                        indicePrincipal = j;
                        indiceSecundario = i;
                    }
                    if( cidadesRepetidas[j] == valorOposto ){
                        cidadesRepetidas[indicePrincipal]--;
                        cidadesRepetidas[indiceSecundario]++;
                        indice2 = rand.nextBoolean() ? indiceCidadesRepetidas[indicePrincipal][0] : indiceCidadesRepetidas[indicePrincipal][1];
                        //System.out.println( "Indice Secundario = " + indiceSecundario );
                        this.array[ indice2 ] = indiceSecundario;
                        //System.out.println( "g.array[ "+ indice2 +" ] = " + indiceSecundario );
                        break;
                    }
                }
            }
        }
    }
    
    public void imprimeGene() {
    	
    	System.out.println();
        for (int i = 0; i < this.array.length; i++)
            System.out.print(this.array[i] + " ");
        System.out.println(" Aptidao:  " + this.aptidao);
    }
}
