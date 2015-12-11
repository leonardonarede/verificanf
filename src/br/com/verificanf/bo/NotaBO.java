/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.verificanf.bo;

import br.com.verificanf.dao.NotaDAO;
import br.com.verificanf.modelo.Nota;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leoma
 */
public class NotaBO {
    private List<Nota> ultimasAtual;
    private List<Nota> ultimasAnterior;
    public void buscar(){
        NotaDAO notaDAO = new NotaDAO();
        try {
            ultimasAtual = notaDAO.getUltimaNotaMesAtual();
            ultimasAnterior = notaDAO.getUltimaNotaMesAnterior();
            
            for(Nota notaAnterior : ultimasAnterior){
                
                for(Nota notaAtual : ultimasAtual){
                    
                    if(notaAnterior.getLoja() == notaAtual.getLoja()){
                        //System.out.println("Anterior Loja: "+notaAnterior.getLoja()+" Nota: "+notaAnterior.getNumero());
                        //System.out.println("Atual Loja: "+notaAtual.getLoja()+" Nota: "+notaAtual.getNumero());
                        Integer numero=0;
                        for(Integer i=notaAnterior.getNumero();i<=notaAtual.getNumero();i++){
                            numero = i;
                            Nota notacorrente = new Nota();
                            notacorrente.setLoja(notaAnterior.getLoja());
                            notacorrente.setNumero(numero);
                            notacorrente.setSerie(notaAnterior.getSerie());
                            
                            //System.out.println("Corrente Loja: "+notacorrente.getLoja()+" Nota: "+notacorrente.getNumero());
                           if(!notaDAO.notaIsValida(notacorrente)){
                                System.out.println("Loja "+notaAnterior.getLoja()+" Numero: "+numero); 
                           }
                        }
                    }
                }    
            }
        } catch (Exception ex) {
            Logger.getLogger(NotaBO.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    
}
