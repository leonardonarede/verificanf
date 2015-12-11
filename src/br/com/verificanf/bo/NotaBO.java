/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.verificanf.bo;

import br.com.verificanf.dao.NotaDAO;
import br.com.verificanf.modelo.Nota;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author leoma
 */
public class NotaBO {
    private List<Nota> ultimasAtual;
    private List<Nota> ultimasAnterior;
    private List<Nota> naoEncontradas;
    private String hostEmail = "";
    private String portEmail = "";
    private String userEmail = "";
    private String passEmail = "";
    private String fromEmail = "";
    private String toEmail = "";
    private String msg = "Salto da numeração favor verificar - \n";
    
    public void buscar(){
        /*Inicio - Pegar Configurações de email do arquivo*/
          try {
          String local = new File("./email.txt").getCanonicalFile().toString();
          File arq = new File(local);
          boolean existe = arq.exists();        
              if (existe){
                  FileReader fr = new FileReader( arq );
                  BufferedReader br = new BufferedReader( fr );
                  while(br.ready()){
                      String linha = br.readLine();
                      if(linha.contains("host:")){
                          hostEmail = linha.replace("host:", "").replace(" ", "");
                      }
                      if(linha.contains("port:")){
                          portEmail = linha.replace("port:", "").replace(" ", "");
                      }
                      if(linha.contains("user:")){
                          userEmail = linha.replace("user:", "").replace(" ", "");
                      }
                      if(linha.contains("pass:")){
                          passEmail = linha.replace("pass:", "").replace(" ", "");
                      }
                      if(linha.contains("from:")){
                          fromEmail = linha.replace("from:", "").replace(" ", "");
                      }
                      if(linha.contains("to:")){
                          toEmail = linha.replace("to:", "").replace(" ", "");
                      }
                  }

              } 
           } catch (FileNotFoundException ex) {
                  Logger.getLogger(NotaBO.class.getName()).log(Level.SEVERE, null, ex);
                  StackTraceElement st[] = ex.getStackTrace();
                  String erro = "";
                  for (int i = 0;i < st.length;i++){
                      erro += st[i].toString() + "\n";
                  } 
                  
           } catch (IOException ex) {
                  Logger.getLogger(NotaBO.class.getName()).log(Level.SEVERE, null, ex);
                  StackTraceElement st[] = ex.getStackTrace();
                  String erro2 = "";
                  for (int i = 0;i < st.length;i++){
                      erro2 += st[i].toString() + "\n";
                  } 
                  
          }
          /*FIM - Pegar Configurações de email do arquivo*/
        
        
        
        NotaDAO notaDAO = new NotaDAO();
        try {
            ultimasAtual = notaDAO.getUltimaNotaMesAtual();
            ultimasAnterior = notaDAO.getUltimaNotaMesAnterior();
            naoEncontradas = new ArrayList<>();
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
                                naoEncontradas.add(notacorrente);
                                msg=msg.concat("      Loja: "+notacorrente.getLoja()+" Nota: "+notacorrente.getNumero()+" Serie: "+notacorrente.getSerie()+" \n");
                           }
                        
                        }
                    }
                }    
            }
               if(naoEncontradas.size() > 0){
                    Email email = new SimpleEmail();
                    email.setHostName(hostEmail);
                    email.setSmtpPort(Integer.parseInt(portEmail));
                    email.setAuthentication(userEmail, passEmail);
                    email.setFrom(fromEmail);
                    email.setSubject("Alerta Nerus!!");
                    email.setMsg(msg);

                    email.addTo(toEmail);
                    email.send();
               }
        } catch (Exception ex) {
            Logger.getLogger(NotaBO.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    
}
