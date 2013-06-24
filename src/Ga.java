import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class Ga {
	
    int tamanhoDaPopulacao = 1000;
    int taxaMutacao = 7; //taxa de mutação em percentagem, deve estar entre 0 e 99.
    int numJobs;
    int numMachs;
    int aptidaoTotal = 0;
    
    static int custos[][];
    Random rand = new Random();
    
    Gene gene[];
    Gene filho[];
    Gene melhorGene;
    
    public Ga(int matrizCustos[][]) {
    	
    	custos = matrizCustos;
    	numJobs = custos[0].length;
    	numMachs = custos.length;
    	
    	melhorGene = new Gene(numJobs, numMachs);
    	melhorGene.aptidao = Integer.MAX_VALUE;
    	
        gene = new Gene[tamanhoDaPopulacao];
        filho = new Gene[tamanhoDaPopulacao];
        
        for (int i = 0; i < tamanhoDaPopulacao; i++) {
            gene[i] = new Gene(numJobs, numMachs);
            filho[i] = new Gene(numJobs, numMachs);
        }
    }
    
    public void inicializa() {
    	
        for (int i = 0; i < gene.length; i++) {
        	gene[i].preencheGeneAleatoriamente();
        	gene[i].calculaAptidao(custos, melhorGene);
        }
    }
    
    /* private void preparaRoleta(){
        for (int i = 0; i < gene.length; i++) {
            gene[i].roletaPercent = gene[i].aptidao/aptidaoTotal;
        }
    }*/
    
    /* A reproducao eh feita por corte,
     * O algoritmo escolhe dois indices aleatoreos da populacao atual e cruza-os. Existe a possibilidade de,
     * apos o cruzamento, o mesmo Job aparecer no gene duas vezes. Para esse caso, existe um tratamento que 
     * verifica os Jobs que não existem no vetor solução e substitui, aleatoreamente, um dos Jobs repetidos 
     * por um Job que não existe naquela solução.
     *
     */
    public void reproducao() {
    	
        Gene filhos[] = new Gene[tamanhoDaPopulacao];
        //int tamanhoDoGene = custos.length;
        //preparaRoleta();
        int r, indice1, indice2;
        int i = 0;
        
        if(tamanhoDaPopulacao%2 == 1 ) 
        	throw new Error( "A variavel tamanhoDaPopulacao deve ser par!");
        
        while( i < tamanhoDaPopulacao ) {
        	
            r = rand.nextInt(tamanhoDaPopulacao);
            indice1 = r;
            while( r == indice1 )
                r = rand.nextInt(tamanhoDaPopulacao);
            indice2 = r;
            
            Gene g[] = gene[indice1].calculaPontoDeCorte(gene[indice2], custos);
            filhos[i++] = g[0];
            filhos[i++] = g[1];
        }
        
        filho = filhos;
        
        for (int j = 0; j < filho.length; j++)
        	filho[j].calculaAptidao(custos, melhorGene);
    }
    
    /*Eh escolhido aleatoriamente um filho para disputar com um pai, o vencendor vai para a proxima geracao*/
    public void selecao() {
    	
    	if( filho.length != gene.length ) throw new Error( "Tamanho dos genes pai e filho são diferentes... my bad!" );
    	Gene novaGeracao[] =  new Gene[tamanhoDaPopulacao];
    	int j, indiceNovaGeracao = 0;
    	boolean indicesUsados[] = new boolean[ tamanhoDaPopulacao ];
    	
    	int jAnterior;
    	for (int i = 0; i < gene.length; i++) {
    		j = rand.nextInt(tamanhoDaPopulacao);
        	do{
        		if( !indicesUsados[j] ){
        			indicesUsados[j] = true;
        			novaGeracao[indiceNovaGeracao] = competeGene( gene[i] , filho[j] ); 
        			//novaGeracao[indiceNovaGeracao] = gene[i].aptidao < filho[j].aptidao ? gene[i] : filho[j];        			
        			indiceNovaGeracao++;
        			break; //Haters gonna hate 
        		}
        		jAnterior = j;
        		j = j < tamanhoDaPopulacao-1 ? j+1 : 0; 
        	} while( jAnterior != j);
		}
    	
    	gene = novaGeracao;
    	
    	for (int i = 0; i < gene.length; i++)
    		gene[i].calculaAptidao(custos, melhorGene);
    }
    
    public Gene competeGene( Gene gene1, Gene gene2 ) {
    	Gene vencedor;
    	int total = gene1.aptidao + gene2.aptidao;
    	float r = rand.nextFloat();
    	/*
    	float razao = (float)1 - (float)gene1.aptidao/total; //Forma nao gulosa, a chance do gene ir para a 
		 													  // proxima geracao depende de quao bom ele eh em
    	vencedor = r < razao ? gene1 : gene2; 
    	//*/
    	
    	///*
    	float razao = (float)0.9; //Forma gulosa, o melhor gene tem 90 de chance de ir para proximo geracao    	
    	//System.out.println("gene1.aptidao: " + gene1.aptidao + " gene2.aptidao: " + gene2.aptidao );
    	Gene melhor, pior;
    	 	if( gene1.aptidao < gene2.aptidao ){
    	 		melhor = gene1;
    	 		pior = gene2;
    	 	} else {
    	 		melhor = gene2;
    	 		pior = gene1;
    	  	}
    	vencedor = r < razao ? melhor : pior;
    	// relacao ao gene que compete com ele.
    	//System.out.println("razao: " + razao);
    	
    	//System.out.println("rand: " + r);
    	//*/
    	return vencedor;
    }
    
    public void obtemIndiceAleatorio(boolean vetorDeIndices[]) {
    	
    	int i = rand.nextInt(vetorDeIndices.length);
    	int indiceAnterior = i;
    	do{
    		if( !vetorDeIndices[i] ){
    			vetorDeIndices[i] = true;
    			break; //Haters gonna hate 
    		} else {
    			i = i < vetorDeIndices.length-1 ? i++ : 0; 
    		}
    	}
    	while( indiceAnterior != i);
    }
    
    // A mutação ocorre com probabilidade igual a que estiver presente na variavel "taxaMutacao" e, caso o gene selecionado
    // seja escolhido para ser mutado, dois índices desse gene são trocados aleatoreamente.
    public void mutacao() {
    	
    	int indiceRand1, indiceRand2, swap;
    	for (int i = 0; i < gene.length; i++) {
			if( rand.nextInt(100) < taxaMutacao ){
				indiceRand1 = rand.nextInt( gene[i].array.length );
				indiceRand2 = rand.nextInt( gene[i].array.length );
				swap = gene[i].array[ indiceRand1 ];
				gene[i].array[ indiceRand1 ] = gene[i].array[ indiceRand2 ];
				gene[i].array[ indiceRand2 ] = swap;
				
				gene[i].calculaAptidao(custos, melhorGene);
			}
		}
    }
    
    public void imprimeGenes(Gene [] g) {
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].array.length; j++) {
                System.out.print( g[i].array[j] + " " );
            }
            System.out.println( " Aptidao:  " + gene[i].aptidao );
        }
    }
    
    static void printMatriz(int [][] custos) {
    	
    	for (int i = 0; i < custos.length; i++) {
			for (int j = 0; j < custos[0].length; j++) {
				System.out.print(custos[i][j] + " ");
			}
			System.out.println();
		}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    	try{
    		
    		File f = new File("rec37.txt");
    		FileReader fr = new FileReader(f);
    		BufferedReader buffer = new BufferedReader(fr);
    		
    		/*FileWriter fw = new FileWriter( "qa194.adapt" );
    		BufferedWriter bufferw = new BufferedWriter(fw);*/
    		
    		String line = new String();

    		int M, J, i;
    		int [][] custos; /* Matrix MxJ de custos, onde M é o numero de Maquinas e J eh o numero de Jobs */
    		int indice;

    		String [] vetorStr;
    		//A primeira linha do arquivo contem 2 inteiros, o numero de Jobs J e o numero de Maquimas M
    		line = buffer.readLine();
    		vetorStr = line.split(" ");
    		
    		J = Integer.parseInt(vetorStr[0]);
    		M = Integer.parseInt(vetorStr[1]);
    		
    		custos = new int[M][J];

    	    //Calcula a matrix de custos de acordo com o arquivo.
    	    i = 0;
    		while( i < J ){
    			line = buffer.readLine().trim().replace("   ", " ").replace("  ", " ");
    			vetorStr = line.split(" ");
    			System.out.println(line);
    			for (int k = 0; k < vetorStr.length; ) {
    				indice = Integer.parseInt(vetorStr[k]);
    				k++; 
					custos[indice][i] = Integer.parseInt(vetorStr[k]);
					k++;
				}
    			i++;
    		}
            
    		printMatriz(custos);
    		
    		buffer.close();
    		fr.close();
    		
    		long tempInicial = System.currentTimeMillis(); 
    		
    		Ga ga = new Ga( custos );
            ga.inicializa();
            i = 0;
            while( i++ < 10000 ){
            	ga.reproducao();
                ga.selecao();
                ga.mutacao();
                System.out.println("Iteracao: " + i + " aptidão do melhor gene: " + ga.melhorGene.aptidao );
            }
            
            System.out.println( "\nGenes" );
            ga.imprimeGenes( ga.gene );
            
            System.out.println( "Melhor gene: " + ga.melhorGene.aptidao );
            for ( i = 0; i < ga.melhorGene.array.length; i++) {
    			System.out.print(ga.melhorGene.array[i] + " " );
    		}

        	long tempFinal = System.currentTimeMillis();     	  
        	long dif = (tempFinal - tempInicial); 
        	System.out.println();
        	System.out.println(String.format("Tempo de execuss�o: %02d segundos  e %02d milisegundos", dif/1000, dif%1000));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
