/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.verificanf.dao;

import br.com.verificanf.factory.Factory;
import br.com.verificanf.modelo.Nota;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leoma
 */
public class NotaDAO {
    private Connection connection;

    public NotaDAO(){
        try {
            this.connection = Factory.getInstance().obterConexao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NotaDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NotaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() 
          throws SQLException, ClassNotFoundException, Exception{
        if((this.connection == null) || (this.connection.isClosed())){
            this.connection = Factory.getInstance().obterConexao();
        }
        return this.connection;
    }
    
    public List<Nota> getUltimaNotaMesAnterior() 
            throws SQLException, Exception{
       String sql = "SELECT y.Lj Loja " +
                    "     , MAX(y.Nota) Nota " +
                    "     , '01' Serie " +
                    "FROM ( " +
                    "SELECT nf.storeno Lj " +
                    "     , nf.nfno Nota " +
                    "FROM  sqldados.nf " +
                    "WHERE (nf.issuedate = DATE_FORMAT(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)),'%Y%m%d')) " +
                    "AND   (nf.nfse LIKE '01') " +
                    "UNION ALL " +
                    "SELECT inv.storeno Lj " +
                    "     , ROUND(inv.nfname) Nota " +
                    "FROM  sqldados.inv " +
                    "WHERE (inv.issue_date = DATE_FORMAT(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)),'%Y%m%d')) " +
                    "AND   (inv.invse like '01') " +
                    "AND   (inv.auxShort7&POW(2,10) = POW(2,10)) " +
                    "ORDER  BY 1 " +
                    ") y " +
                    "GROUP BY 1 ";
       PreparedStatement stmt = getConnection().prepareStatement(sql);
       stmt.executeQuery();
       ResultSet rs = stmt.getResultSet();
       List<Nota> notas = new ArrayList<>();
       while (rs.next()){
           Nota nota = new Nota();
           nota.setLoja(rs.getInt("Loja"));
           nota.setNumero(rs.getInt("Nota"));
           nota.setSerie(rs.getString("Serie"));
           notas.add(nota);
       }
       stmt.close();
       return notas;
    }
    
    public List<Nota> getUltimaNotaMesAtual()
             throws SQLException, Exception{
       String sql = "SELECT lastno.storeno Loja " +
                    "     , MAX(lastno.no) Nota " + 
                    "     , '01' Serie " +
                    "FROM   sqldados.lastno " +
                    "WHERE  lastno.se = '01' " +
                    "GROUP  BY 1 ";
       PreparedStatement stmt = getConnection().prepareStatement(sql);
       stmt.executeQuery();
       ResultSet rs = stmt.getResultSet();
       List<Nota> notas = new ArrayList<>();
       while (rs.next()){
           Nota nota = new Nota();
           nota.setLoja(rs.getInt("Loja"));
           nota.setNumero(rs.getInt("Nota"));
           nota.setSerie(rs.getString("Serie"));
           notas.add(nota);
       }
       stmt.close();
       return notas; 
    }
    
    public Boolean notaIsValida(Nota nota)
             throws SQLException, Exception{
       Boolean resp = false;
       String sql = "SELECT * FROM ( " +
                    "SELECT nf.storeno Lj " +
                    "     , nf.nfno Nota " +
                    "     , nf.nfse Se " +
                    "     , 'Saida' Tipo " +
                    "FROM  sqldados.nf " +
                    "WHERE (nf.issuedate BETWEEN CONCAT(DATE_FORMAT(CURDATE(),'%Y%m'),'01') and DATE_FORMAT(LAST_DAY(CURDATE()),'%Y%m%d')) " +
                    "AND   (nf.nfse LIKE '01') " +
                    "UNION ALL " +
                    "SELECT inv.storeno Lj " +
                    "     , ROUND(inv.nfname) Nota " +
                    "     , inv.invse Se " +
                    "     , 'Entrada' Tipo " +
                    "FROM  sqldados.inv " +
                    "WHERE (inv.issue_date BETWEEN CONCAT(DATE_FORMAT(CURDATE(),'%Y%m'),'01') and DATE_FORMAT(LAST_DAY(CURDATE()),'%Y%m%d')) " +
                    "AND   (inv.invse like '01') " +
                    "AND   (inv.auxShort7&POW(2,10) = POW(2,10)) " +
                    "ORDER  BY 1,2,3 " +
                    ") x " +
                    "WHERE x.Lj = ? AND x.Nota = ? AND x.Se = ? ";
       PreparedStatement stmt = getConnection().prepareStatement(sql);
       stmt.setInt(1, nota.getLoja());
       stmt.setInt(2, nota.getNumero());
       stmt.setString(3, nota.getSerie());
       stmt.executeQuery();
       ResultSet rs = stmt.getResultSet();
       List<Nota> notas = new ArrayList<>();
       if (rs.next()){
          resp = true;
       }
       stmt.close();
       return resp; 
    }
}
