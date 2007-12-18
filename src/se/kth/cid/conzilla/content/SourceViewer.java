package se.kth.cid.conzilla.content;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import se.kth.cid.conzilla.controller.MapController;

import com.hp.hpl.jena.rdf.model.Model;

public class SourceViewer extends JFrame {
	
	JTextArea text;
	
	MapController controller;
	
	String format;
	
	public SourceViewer(MapController controller, String format) {
		super();
		this.controller = controller;
		this.format = format;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int)(screenSize.width/2),(int)(screenSize.height/2));
		int x = (int)(frameSize.width/2);
		int y = (int)(frameSize.height/2);
		setBounds(x, y, frameSize.width, frameSize.height);
		setTitle("Source of " + controller.getConceptMap().getURI());
		
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setFont(new Font("Courier", Font.PLAIN, 10));
		getContentPane().add(new JScrollPane(text));
		
		text.append(getContextMapSource());
	}
	
	private String getContextMapSource() {
		String container = controller.getConceptMap().getLoadContainer();
		Model mapModel = (Model) controller.getMapStoreManager().getStore().getContainerManager().getContainer(container);
		StringWriter sw = new StringWriter();
		mapModel.write(sw, format);
		return sw.toString();
	}

}