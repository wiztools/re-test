package org.wiztools.util.retest;

import javax.swing.SwingUtilities;

/**
 *
 * @author subhash
 */
public class Main {
    public static void main(String[] arg){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new REFrame("WizTools.org Regular Expression Tester");
            }
        });
    }
}
