import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class Ga {
	
    int tamanhoDaPopulacao = 100;
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
     * O algoritmo escolhe dois indices aleatoreos da populacao atual e cruza-os 
     * Existe a possibilidade de, apos o cruzamento, a mesma cidade aparecer no gene duas vezes. Para esse caso, existe um
     * tratamento que verifica as cidades que não existem no vetor solução e substitui, aleatoreamente, uma das cidades
     * repetidas por uma cidade que não existe naquela solução. 
     *
     * Exemplo: suponhamos o cruzamento dos genes com as cidades [1, 4, 3, 5, 2, 6] e [4, 3, 5, 1, 6, 2] com corte na 3a casa.
     * Teremos como resultado os genes: [1, 4, 3, 1, 6, 2] e [4, 3, 5, 5, 2, 6] e que não são soluções viáveis para o 
     * problema do caixeiro viajante. O 1o gene resultante, por exemplo, não passa pela cidade 5 e passa 2 vezes pela cidade 2.
     * O tratamento desse caso colocaria a cidade 5 aleatoreamente em uma das posições repetidas pela cidade 1.
     * O resultado tratado para o primeiro gene resultando seria: [5, 4, 3, 1, 6, 2] ou [1, 4, 3, 5, 6, 2].
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
            
            Gene g[] = calculaPontoDeCorte(gene[indice1], gene[indice2]);
            filhos[i++] = g[0];
            filhos[i++] = g[1];
        }
        
        filho = filhos;
        
        for (int j = 0; j < filho.length; j++)
        	filho[j].calculaAptidao(custos, melhorGene);
    }
    
    /* Resposavel pelo processamento do ponto de corte.
     * O range do corte eh escolhido aleatoreamente.
     */
    public Gene[] calculaPontoDeCorte(Gene pai1, Gene pai2) {
    	
        int tamanhoDoGene = custos.length;
        Gene filhos[] = new Gene[2];
        
        for (int i = 0; i < filhos.length; i++)
			filhos[i] = new Gene(numJobs, numMachs);
        
        int pontoDeCorte = rand.nextInt( tamanhoDoGene );
        					//rand.nextInt( tamanhoDoGene/2 ) + tamanhoDoGene/4;
        
        for (int i = 0; i < pai1.array.length; i++) {
            if( i < pontoDeCorte ){
                filhos[0].array[i] = pai1.array[i];
                filhos[1].array[i] = pai2.array[i];
            } else {
                filhos[0].array[i] = pai2.array[i];
                filhos[1].array[i] = pai1.array[i]; 
            }
        }
        
        balanceiaGene(filhos[0]);
        balanceiaGene(filhos[1]);
        return filhos;
    }
    
    /*
     * O ponto de corte pode causar desbalanceamento dos genes, por exemplo:
     * 
     * O gene [0,1,2,3,4,5] cruzado com o gene [5,4,3,2,1,0] com ponto de corte na 3a casa daria como resultado o filho:
     * [0,1,2,2,1,0 ]
     * que obiviamente nao eh uma solucao valida. Esse metodo escolhe uma das cidades repetidas aleateroeamente e a 
     * substitui por uma das cidades que nao esta presente no conjunto solucao. 
     * Para o filho gerado acima, uma possibilidade de balanceamento seria:
     * [5,1,2,4,3,0]
     */
    public void balanceiaGene(Gene g) {
    	
    	//System.out.println( "\nGene no balanceamento: " );
    	int i, j = 0;
    	for ( i = 0; i < g.array.length; i++) {
    		//System.out.print(  g.array[i] +" " );
		}
        int cidadesRepetidas[] = new int[g.array.length];

        int indiceCidadesRepetidas[][] = new int[g.array.length][2];
        for ( i = 0; i < indiceCidadesRepetidas.length; i++) {
            for ( j = 0; j < indiceCidadesRepetidas[0].length; j++) {
                indiceCidadesRepetidas[i][j] = -1;
            }
        }
        
        int indice2, valorOposto;
        for ( i = 0; i < g.array.length; i++) {
            cidadesRepetidas[ g.array[i] ] ++;
            indice2 = indiceCidadesRepetidas[ g.array[i] ][0] == -1 ? 0 : 1;
            indiceCidadesRepetidas[ g.array[i] ][indice2] = i;
            if( cidadesRepetidas[ g.array[i] ] > 3 ) 
                throw new Error("Erro no balanceiaGene: cromossomo g.array["+g.array[i]+"] com mais de duas cidades repetidas ");
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
                        g.array[ indice2 ] = indiceSecundario;
                        //System.out.println( "g.array[ "+ indice2 +" ] = " + indiceSecundario );
                        break;
                    }
                }
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
    
    public Gene competeGene(Gene gene1, Gene gene2) {
    	int total = 0;
    	
    	total = gene1.aptidao + gene2.aptidao;
    	float razao = (float)0.9;//1 - gene1.aptidao/total;
    	
    	Gene melhor, pior;
    	if( gene1.aptidao < gene2.aptidao ){
    		melhor = gene1;
    		pior = gene2;
    	} else {
    		melhor = gene2;
    		pior = gene1;
    	}
    	
    	//float razao2 = gene2.aptidao/total;
    
    	float r = rand.nextFloat();
    	
    	Gene vencedor = r < razao ? melhor : pior;
    	
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
    		/* O arquivo de teste para o problema do caixeiro viajante filho da mae que eu achei na internet não 
    		 * dá as distancias entre as cidades, mas sim as coordenadas de cada cidade no plano cartesiano. 
    		 * Tive que fazer um processamento extra para calcular a distancia entre todas as cidades 
    		 * segundo a formula da distancia entre dois pontos.
    		 *   
    		 */
    		File f = new File("abz5.txt");
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
    			line = buffer.readLine().trim();
    			vetorStr = line.split(" ");
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
    		
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
