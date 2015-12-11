/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.verificanf.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author leoma
 */
public class Factory {
    private static Factory connectionFactory;
    private Connection connection;
    
    public static Factory getInstance(){
        if(connectionFactory == null){
            connectionFactory =  new Factory();
        }
        return connectionFactory;
    }
    
    public Connection obterConexao()
           throws SQLException, ClassNotFoundException, Exception
            {
    
                /*Inicio lendo configurações para o banco*/  
                  String host = "";
                  String db = "";
                  String user = "";
                  String pass = "";
                  try {
                      String local = new File("./bdconfig.txt").getCanonicalFile().toString();
                      File arq = new File(local);
                      boolean existe = arq.exists(); 
                      if (existe){
                              FileReader fr = new FileReader( arq );
                              BufferedReader br = new BufferedReader( fr );
                              while(br.ready()){
                                  String linha = br.readLine();
                                  if(linha.contains("Host:")){
                                      host = linha.replace("Host:", "").replace(" ", "");
                                  }
                                  if(linha.contains("DB:")){
                                      db = linha.replace("DB:", "").replace(" ", "");
                                  }
                                  if(linha.contains("User:")){
                                      user = linha.replace("User:", "").replace(" ", "");
                                  }
                                  if(linha.contains("Pass:")){
                                      pass = linha.replace("Pass:", "").replace(" ", "");
                                  }
                              }

                       }
                  } catch (Exception e) {
                      StackTraceElement st[] = e.getStackTrace();
                      String erro = "";
                      for (int i = 0;i < st.length;i++){
                          erro += st[i].toString() + "\n";
                      } 
                  }
                /*Fim lendo configurações para o banco*/ 
                if ((this.connection == null) || (this.connection.isClosed()))
               {
                  Class.forName("com.mysql.jdbc.Driver");
                  String serverName = host;
                  String banco = db;
                  String url = "jdbc:mysql://" + serverName + "/" + banco;
                  try
                  {
                    this.connection = DriverManager.getConnection(url, user, pass);
                    if (this.connection != null) {
                       return this.connection;
                    }
                  }
                  catch (Exception ex)
                  {
                    ex.printStackTrace();
                    throw new Exception("Conexao Realizada!");
                  }
                  return this.connection;
                  }
                  return this.connection;
           }
}
