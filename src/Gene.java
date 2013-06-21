/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Diego
 */
/*
 * Cada Gene representa uma soluçao para o problema dao caixeiro viajante.
 * A resposta ao problema esta no metodo chamada "Array", cada valor do array representa uma cidade a ser visitada
 * enquanto o ﾃｭndice representa a ordem. Exemplo: um arra como [1, 4, 3] quer dizer que a cidade 1 será
 * visitada 1o seguida da cidade 4 e a ultima
 * cidade será a 3, que deverá ser ligada a cidade 1 para fechar o ciclo. 
 * o metodo aptidao possui o custo total desse ciclo.
 */
public class Gene {
    int array[] ;
    int aptidao = 0;
    int numJobs = 0;
    int numMachs = 0;
    float roletaPercent = 0;
    
    public Gene( int nJobs ) {
        numJobs = nJobs;
        array = new int[numJobs];
    }
}
