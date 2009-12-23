package org.wiztools.util.retest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

    private final JTextField jtf_re = new JTextField(40);
    private final JTextArea jta_in = new JTextArea(10, 5);
    private final JTextArea jta_out = new JTextArea(10, 5);

    private final JCheckBox jcb_caseinsensitive = new JCheckBox("Case insensitive");
    private final JCheckBox jcb_dotall = new JCheckBox(". matches newline too");
    private final JCheckBox jcb_multiline = new JCheckBox("Multi-line");
    // private JCheckBox jcb_comments = new JCheckBox("Ignore whitespaces & comments");

    private final ImageIcon IMAGE_WRONG = new ImageIcon(this.getClass().getClassLoader().getResource("org/wiztools/util/retest/cross.png"));
    private final ImageIcon IMAGE_RIGHT = new ImageIcon(this.getClass().getClassLoader().getResource("org/wiztools/util/retest/tick.png"));

    private final JLabel jl_status = new JLabel("WizTools.org RegularExpression Tester");
    private final JLabel jl_indicator = new JLabel(IMAGE_WRONG);

    private final Font DIALOG_PLAIN_12 = new Font(Font.DIALOG, Font.PLAIN, 12);
    private final Font DIALOG_BOLD_12 = new Font(Font.DIALOG, Font.BOLD, 12);

    private final String MSG_PATTERN_WRONG = "Error compiling pattern";
    private final String MSG_PATTERN_RIGHT = "Pattern compiles";

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
        // Set DocumentListener on Text Objects:
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

        // Set the ItemListener on JComboBoxes:
        final ItemListener il = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                match();
            }
        };

        jcb_caseinsensitive.addItemListener(il);
        jcb_dotall.addItemListener(il);
        jcb_multiline.addItemListener(il);

        // Font:
        jtf_re.setFont(DIALOG_BOLD_12);
        jcb_caseinsensitive.setFont(DIALOG_PLAIN_12);
        jcb_dotall.setFont(DIALOG_PLAIN_12);
        jcb_multiline.setFont(DIALOG_PLAIN_12);
        jl_status.setFont(DIALOG_PLAIN_12);

        // Other configurations:
        jtf_re.setToolTipText("Regular Expression");
        jta_in.setToolTipText("Input text");
        jta_out.setEditable(false);
        jta_out.setToolTipText("Result");
        jl_indicator.setToolTipText(MSG_PATTERN_WRONG);
        jta_out.setBackground(new Color(250, 252, 170));
    }

    private JPanel initInput(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new GridLayout(2, 1));

        JPanel jp_north_1 = new JPanel();
        jp_north_1.setLayout(new BorderLayout());
        JLabel jl = new JLabel(" RE: ");
        jl.setFont(DIALOG_PLAIN_12);
        jl.setDisplayedMnemonic('r');
        jl.setLabelFor(jtf_re);
        jp_north_1.add(jl, BorderLayout.WEST);
        jp_north_1.add(jtf_re, BorderLayout.CENTER);
        jp_north_1.add(jl_indicator, BorderLayout.EAST);
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
        jp.setLayout(new BorderLayout());
        JScrollPane jsp = new JScrollPane(jta_out);
        jp.add(jsp, BorderLayout.CENTER);

        JPanel jp_south = new JPanel();
        jp_south.setLayout(new GridLayout(1, 1));
        jp_south.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        jp_south.add(jl_status);

        jp.add(jp_south, BorderLayout.SOUTH);
        return jp;
    }

    private void setIndicatorRight(){
        jl_indicator.setIcon(IMAGE_RIGHT);
        jl_indicator.setToolTipText(MSG_PATTERN_RIGHT);
    }

    private void setIndicatorWrong(){
        jl_indicator.setIcon(IMAGE_WRONG);
        jl_indicator.setToolTipText(MSG_PATTERN_WRONG);
    }

    private void setStatus(final String msg){
        jl_status.setText(msg);
    }

    // Business logic:
    private void match(){
        // Clear before doing anything:
        jta_out.setText("");

        final String re = jtf_re.getText();
        final String input = jta_in.getText();
        if(re.equals("")){
            setIndicatorWrong();
            setStatus("No RE given!");
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
                setIndicatorRight();
                Matcher m = p.matcher(input);
                if(m.find()){
                    jta_out.append("<<Match found!>>\n\n");
                    for(int i=1; i<=m.groupCount(); i++){
                        jta_out.append("<<Group: " + i + ">>\n");
                        final String grp = m.group(i);
                        if(grp != null){
                            String[] arr = m.group(i).split("\n");
                            for(String s: arr){
                                jta_out.append("\t" + s + "\n");
                            }
                        }
                    }
                    setStatus("Matched :-)");
                }
                else{
                    jta_out.setText("<<Does not match!>>");
                    setStatus("No match :-(");
                }
            }
            catch(PatternSyntaxException ex){
                jta_out.append("<<Error!>>\n\n");
                jta_out.append(ex.getMessage());
                setIndicatorWrong();
                setStatus(MSG_PATTERN_WRONG);
            }
            catch(StringIndexOutOfBoundsException ex){
                jta_out.append("<<Error!>>\n\n");
                jta_out.append(ex.getMessage());
                setIndicatorWrong();
                setStatus(MSG_PATTERN_WRONG);
            }
        }
    }
}
