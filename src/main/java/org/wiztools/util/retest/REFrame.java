package org.wiztools.util.retest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author subhash
 */
public class REFrame extends JFrame {

    private static final int BORDER_LAYOUT_SPACING = 5;

    private JTextField jtf_re = new JTextField(40);
    private JTextArea jta_in = new JTextArea(10, 5);
    private JTextArea jta_out = new JTextArea(10, 5);
    private JButton jb_match = new JButton("  Match  ");
    private JButton jb_clear = new JButton("Clear");

    private final REFrame me;

    public REFrame(String title){
        super(title);
        me = this;
        
        init();

        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.add(initInput());
        jsp.add(initOutput());

        this.getContentPane().add(jsp);

        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void init(){
        jtf_re.setToolTipText("Regular Expression");
        jta_in.setToolTipText("Input text");
        jta_out.setEditable(false);
        jta_out.setToolTipText("Result");

        jb_clear.setMnemonic('c');
        jb_clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta_out.setText("");
            }
        });

        jb_match.setMnemonic('m');
        jb_match.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Clear before doing anything:
                jta_out.setText("");

                final String re = jtf_re.getText();
                final String input = jta_in.getText();
                if(re.trim().equals("")){
                    JOptionPane.showMessageDialog(me, "No RE given!");
                    jtf_re.requestFocus();
                }
                else{
                    try{
                        Pattern p = Pattern.compile(re);
                        Matcher m = p.matcher(input);
                        if(m.find()){
                            jta_out.append("<<Match found!>>\n\n");
                            for(int i=1; i<=m.groupCount(); i++){
                                jta_out.append("<<Group: " + i + ">>\n");
                                jta_out.append("\t" + m.group(i) + "\n");
                            }
                        }
                        else{
                            jta_out.setText("<<Does not match!>>");
                        }
                    }
                    catch(PatternSyntaxException ex){
                        JOptionPane.showMessageDialog(me, ex.getMessage());
                    }
                }
            }
        });
    }

    private JPanel initInput(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout(BORDER_LAYOUT_SPACING,
                BORDER_LAYOUT_SPACING));

        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new BorderLayout(BORDER_LAYOUT_SPACING,
                BORDER_LAYOUT_SPACING));
        JLabel jl = new JLabel("RE: ");
        jl.setDisplayedMnemonic('r');
        jl.setLabelFor(jtf_re);
        jp_north.add(jl, BorderLayout.WEST);
        jp_north.add(jtf_re, BorderLayout.CENTER);

        jp.add(jp_north, BorderLayout.NORTH);

        // Center
        JScrollPane jsp = new JScrollPane(jta_in);

        jp.add(jsp, BorderLayout.CENTER);

        // South
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp_south.add(jb_match);
        jp_south.add(jb_clear);

        jp.add(jp_south, BorderLayout.SOUTH);

        return jp;
    }

    private JPanel initOutput(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout(BORDER_LAYOUT_SPACING,
                BORDER_LAYOUT_SPACING));
        JScrollPane jsp = new JScrollPane(jta_out);
        jp.add(jsp, BorderLayout.CENTER);
        return jp;
    }
}
