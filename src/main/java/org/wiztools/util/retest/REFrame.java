package org.wiztools.util.retest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author subhash
 */
public class REFrame extends JFrame {

    private static final int BORDER_LAYOUT_SPACING = 5;

    private final JTextField jtf_re = new JTextField(40);
    private final JTextArea jta_in = new JTextArea(10, 5);
    private final JTextArea jta_out = new JTextArea(10, 5);

    private final JCheckBox jcb_caseinsensitive = new JCheckBox("Case insensitive");
    private final JCheckBox jcb_dotall = new JCheckBox(". matches newline too");
    private final JCheckBox jcb_multiline = new JCheckBox("Multi-line");
    // private JCheckBox jcb_comments = new JCheckBox("Ignore whitespaces & comments");

    private final JLabel jl_status = new JLabel("WizTools.org RE Tester");

    public REFrame(String title){
        super(title);
        
        init();

        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.add(initInput());
        jsp.add(initOutput());

        this.getContentPane().add(jsp);

        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void init(){
        final DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                match();
            }

            public void removeUpdate(DocumentEvent e) {
                match();
            }

            public void changedUpdate(DocumentEvent e) {
                match();
            }
        };

        jtf_re.getDocument().addDocumentListener(dl);
        jta_in.getDocument().addDocumentListener(dl);

        final ItemListener il = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                match();
            }
        };

        jcb_caseinsensitive.addItemListener(il);
        jcb_dotall.addItemListener(il);
        jcb_multiline.addItemListener(il);

        jtf_re.setToolTipText("Regular Expression");
        jta_in.setToolTipText("Input text");
        jta_out.setEditable(false);
        jta_out.setToolTipText("Result");
        jl_status.setFont(new Font(Font.DIALOG, Font.PLAIN, 12)); // 10 is the font size
    }

    private JPanel initInput(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout(BORDER_LAYOUT_SPACING,
                BORDER_LAYOUT_SPACING));

        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new GridLayout(2, 1));

        JPanel jp_north_1 = new JPanel();
        jp_north_1.setLayout(new BorderLayout());
        JLabel jl = new JLabel("RE: ");
        jl.setDisplayedMnemonic('r');
        jl.setLabelFor(jtf_re);
        jp_north_1.add(jl, BorderLayout.WEST);
        jp_north_1.add(jtf_re, BorderLayout.CENTER);
        jp_north.add(jp_north_1);

        JPanel jp_north_2 = new JPanel();
        jp_north_2.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp_north_2.add(jcb_caseinsensitive);
        jp_north_2.add(jcb_multiline);
        jp_north_2.add(jcb_dotall);
        // jp_north_2.add(jcb_comments);
        
        jp_north.add(jp_north_2);

        jp.add(jp_north, BorderLayout.NORTH);

        // Center
        JScrollPane jsp = new JScrollPane(jta_in);

        jp.add(jsp, BorderLayout.CENTER);
        return jp;
    }

    private JPanel initOutput(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout(BORDER_LAYOUT_SPACING,
                BORDER_LAYOUT_SPACING));
        JScrollPane jsp = new JScrollPane(jta_out);
        jp.add(jsp, BorderLayout.CENTER);

        JPanel jp_south = new JPanel();
        jp_south.setLayout(new GridLayout(1, 1));
        jp_south.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        jp_south.add(jl_status);

        jp.add(jp_south, BorderLayout.SOUTH);
        return jp;
    }

    private void setStatus(final String msg){
        jl_status.setText(msg);
        jl_status.setToolTipText(msg);
    }

    // Business logic:
    private void match(){
        // Clear before doing anything:
        jta_out.setText("");

        final String re = jtf_re.getText();
        final String input = jta_in.getText();
        if(re.trim().equals("")){
            setStatus("No RE given!");
            jtf_re.requestFocus();
        }
        else{
            try{
                // Always specify this:
                int option = Pattern.CANON_EQ;

                if(jcb_caseinsensitive.isSelected()){
                    option = option | Pattern.CASE_INSENSITIVE;
                    option = option | Pattern.UNICODE_CASE;
                }
                if(jcb_multiline.isSelected()){
                    option = option | Pattern.MULTILINE;
                }
                if(jcb_dotall.isSelected()){
                    option = option | Pattern.DOTALL;
                }

                Pattern p = Pattern.compile(re, option);
                Matcher m = p.matcher(input);
                if(m.find()){
                    jta_out.append("<<Match found!>>\n\n");
                    for(int i=1; i<=m.groupCount(); i++){
                        jta_out.append("<<Group: " + i + ">>\n");
                        String[] arr = m.group(i).split("\n");
                        for(String s: arr){
                            jta_out.append("\t" + s + "\n");
                        }
                    }
                }
                else{
                    jta_out.setText("<<Does not match!>>");
                }
            }
            catch(PatternSyntaxException ex){
                setStatus(ex.getMessage());
            }
        }
    }
}
