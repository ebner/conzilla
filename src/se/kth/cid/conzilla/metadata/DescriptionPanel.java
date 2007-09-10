/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.MatteBorder;

import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.util.ArrowBorder;
import se.kth.nada.kmr.shame.form.FormModel;
import se.kth.nada.kmr.shame.form.impl.PopUpForm;
import se.kth.nada.kmr.shame.form.impl.SinglePanelFormContainer;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.query.QueryEngine;
import se.kth.nada.kmr.shame.query.QueryExecutionException;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.RDFEngine;
import se.kth.nada.kmr.shame.query.UnsupportedQueryModelException;
import se.kth.nada.kmr.shame.query.UnsupportedQueryTargetException;
import se.kth.nada.kmr.shame.query.VariableBindingSet;
import se.kth.nada.kmr.shame.query.impl.GraphPatternQueryEngine;
import se.kth.nada.kmr.shame.query.impl.JenaRDFEngine;
import se.kth.nada.kmr.shame.workflow.WorkFlowManager;

public class DescriptionPanel extends SinglePanelFormContainer {

	private static final long serialVersionUID = 1L;

	Object component;

	int ox;

	int oy;

	public static final int xoffset = 10, yoffset = 10;

	JComponent form;

	PopupTrigger2QueryTarget pt2qt;
	
	boolean visible = true;

	Formlet formlet;

	// private AffineTransform transform;
	private double scale;

	private boolean collaborative;

	private boolean expanded = false;

	public DescriptionPanel(Object comp, double scale, PopupTrigger2QueryTarget p2t) {
		super();
		this.pt2qt = p2t;
		setOpaque(true);
        setBackground(ColorTheme.getTranslucentColor(Colors.INFORMATION));
		component = comp;

		setDoubleBuffered(false);
		formlet = pt2qt.getFormlet(comp);
		// FIXME: Fulhack
		setScale(scale);
		collaborative = pt2qt.isCollaborative(component);
		createShameForm();
	}

	public void expand() {
		if (!collaborative || expanded) {
			return;
		}
		expanded = true;
		createShameForm();
	}

	public void unExpand() {
		if (!collaborative || !expanded) {
			return;
		}
		expanded = false;
		createShameForm();
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	public void create(Container form, FormModel model,
			WorkFlowManager workFlowManager) {
		this.form = (JComponent) form;
		removeAll();
		setLayout(new BorderLayout());
		add(form, BorderLayout.CENTER);
		model.getVariableBindingSet().addVariableBindingSetListener(this);
		revalidate();
	}

	public void setLocation(int x, int y) {
		this.ox = x + (int) (xoffset / scale);
		this.oy = y + (int) (yoffset / scale);
		setLocation();
	}

	private void setLocation() {
		super.setLocation((int) (ox * scale), (int) (oy * scale));
	}

	public void toggleVisibility() {
		visible = !visible;
	}

	void createShameForm() {
		if (!visible) {
			return;
		}
		updateBorder();
		QueryTarget queryTarget = null;
		if (expanded) {
			queryTarget = pt2qt.getCollaborativeQueryTarget(component);
		} else {
			queryTarget = pt2qt.getQueryTarget(component);
		}
		RDFEngine engine = new JenaRDFEngine(null);
		QueryEngine queryEngine = new GraphPatternQueryEngine(engine);
		VariableBindingSet variableBindingSet = null;
		try {
			variableBindingSet = queryEngine.execute(formlet.getQueryModel(),
					queryTarget);
		} catch (UnsupportedQueryModelException e) {
			e.printStackTrace();
		} catch (UnsupportedQueryTargetException e) {
			e.printStackTrace();
		} catch (QueryExecutionException e) {
			e.printStackTrace();
		}

		// Here the FormModel is bound to the values supplied by the
		// VariableBindingSet.
		FormModel formModel = formlet.getFormModel().duplicate();
		formModel.setVariableBindingSet(variableBindingSet);
		formModel.setQueryTarget(queryTarget);

		PopUpForm form = new PopUpForm(this) {
			protected void addFormContentBorders(JComponent c) {
			}
		};
		if (!expanded) {
			form.chooseValuesWithPreferredLanguage(true);
		}
		form.setMaxPreferredWidth((int) (400));
		form.setScale((float) scale);

		form.setPrimaryColor(ColorTheme
				.getTranslucentColor(Colors.INFORMATION));
		form.setSecondaryColor(ColorTheme
				.getBrighterColor(Colors.INFORMATION));
		form.create(formModel, null);
		// XXX: No WorkFlowManager used.
		this.revalidate();
	}

	public boolean getVisible() {
		return visible;
	}

	private void updateBorder() {

		if (collaborative) {
			setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 0.9f)),
					new ArrowBorder(expanded ? ArrowBorder.NORTH : ArrowBorder.SOUTH, ArrowBorder.SOUTH)));
		} else {
			setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 0.9f)));
		}
	}

	public void setScale(double scale) {
		// Tracer.debug("setScale ");
		this.scale = scale;
		setLocation();
		updateBorder();
		repaint();
	}

	// private static void setArrowScale(double scale) {
	// /*up = new ArrowBorder((int) (scale * 6), 0);
	// both = new ArrowBorder((int) (scale * 6), (int) scale * 6);
	// down = new ArrowBorder(0, (int) (scale * 6));*/
	// }
}
