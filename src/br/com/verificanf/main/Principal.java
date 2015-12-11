/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.verificanf.main;

import br.com.verificanf.bo.NotaBO;

/**
 *
 * @author leoma
 */
public class Principal {
    public static void main(String[] args) {
        NotaBO notabo = new NotaBO();
        notabo.buscar();
    }
}
