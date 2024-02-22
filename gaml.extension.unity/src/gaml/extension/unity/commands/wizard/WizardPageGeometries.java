/*******************************************************************************************************
 *
 * WizardPageGeometries.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.commands.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gama.core.kernel.model.IModel;

/**
 * The Class WizardPageGeometries.
 */
public class WizardPageGeometries extends WizardPage {

	/** The model. */
	IModel model;

	// private Composite container;

	/** The generator. */
	VRModelGenerator generator;

	/** The items D. */
	List<String> itemsD;

	/**
	 * Instantiates a new wizard page geometries.
	 *
	 * @param model
	 *            the model
	 * @param gen
	 *            the gen
	 */
	protected WizardPageGeometries(final IModel model, final VRModelGenerator gen) {
		super("Geometries");
		setTitle("Define the information about the background geometries");
		setDescription("Please enter information about the background geometries");
		this.model = model;
		this.generator = gen;

	}

	@Override
	public void createControl(final Composite parent) {
		// container = new Composite(parent, SWT.NONE);
		// container.setLayout(new FillLayout(SWT.VERTICAL));
		ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
		scroll.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		scroll.setAlwaysShowScrollBars(false);
		scroll.setExpandVertical(true);
		scroll.setExpandHorizontal(true);

		scroll.setMinHeight(600);
		scroll.setLayout(new GridLayout(1, false));

		Composite group = new Composite(scroll, SWT.NONE);
		scroll.setContent(group);
		group.setLayout(new GridLayout(2, false));
		// group.setText("Species of agents to send to Unity as static geometries");
		Label lma = new Label(group, SWT.LEFT);
		lma.setText("Species of agents to send to Unity as static geometries");
		Label lmae = new Label(group, SWT.LEFT);
		lmae.setText("");

		Map<String, DataGeometries> dataGeoms = new HashMap<>();
		for (String sp : model.getAllSpecies().keySet()) {

			if (sp.equals(model.getName())) { continue; }
			DataGeometries gg = new DataGeometries();
			gg.setSpeciesName(sp);

			dataGeoms.put(sp, gg);
			Button bt = new Button(group, SWT.CHECK);

			bt.setText(sp);
			bt.pack();

			Group groupSp = new Group(group, SWT.NONE);

			groupSp.setLayout(new GridLayout(2, false));

			Button btC = new Button(groupSp, SWT.CHECK);
			btC.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					dataGeoms.get(sp).setHasCollider(btn.getSelection());

				}
			});
			btC.setText("Collider?");

			btC.pack();
			Label lem = new Label(groupSp, SWT.LEFT);
			lem.setText("");

			Button bt3D = new Button(groupSp, SWT.CHECK);
			bt3D.setSelection(dataGeoms.get(sp).getIs3D());

			bt3D.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					dataGeoms.get(sp).setIs3D(btn.getSelection());

				}
			});
			bt3D.setText("is 3D?");

			bt3D.pack();
			Label le3D = new Label(groupSp, SWT.LEFT);
			le3D.setText("");

			Button btSelectable = new Button(groupSp, SWT.CHECK);
			btSelectable.setSelection(dataGeoms.get(sp).getIsSelectable());
			btSelectable.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					dataGeoms.get(sp).setIsSelectable(btn.getSelection());

				}
			});
			btSelectable.setText("is selectable?");

			btSelectable.pack();
			Label leSelectable = new Label(groupSp, SWT.LEFT);
			leSelectable.setText("");

			Button btGrabable = new Button(groupSp, SWT.CHECK);
			btGrabable.setSelection(dataGeoms.get(sp).getIsGrabable());
			btGrabable.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					dataGeoms.get(sp).setIsGrabable(btn.getSelection());

				}
			});
			btGrabable.setText("is grabable?");

			btGrabable.pack();
			Label leGrab = new Label(groupSp, SWT.LEFT);
			leGrab.setText("");

			Label lp = new Label(groupSp, SWT.LEFT);
			lp.setText("Buffer:");
			Text tp = new Text(groupSp, SWT.BORDER);
			tp.setText(dataGeoms.get(sp).getBuffer().toString());
			tp.addModifyListener(e -> {
				Double v = Double.valueOf(tp.getText());
				if (v != null) { dataGeoms.get(sp).setBuffer(v); }
			});

			Label lh = new Label(groupSp, SWT.LEFT);
			lh.setText("Height:");
			Text th = new Text(groupSp, SWT.BORDER);
			th.setText(dataGeoms.get(sp).getHeight().toString());
			th.addModifyListener(e -> {
				Double v = Double.valueOf(th.getText());
				if (v != null) { dataGeoms.get(sp).setHeight(v); }
			});

			Label lt = new Label(groupSp, SWT.LEFT);
			lt.setText("Tag:");
			Text tt = new Text(groupSp, SWT.BORDER);
			tt.setText(dataGeoms.get(sp).getTag());
			tt.addModifyListener(e -> dataGeoms.get(sp).setTag(tt.getText()));

			Label lpc = new Label(groupSp, SWT.LEFT);
			lpc.setText("Color:");
			Text tpc = new Text(groupSp, SWT.BORDER);
			tpc.setText(dataGeoms.get(sp).getColor());
			tpc.addModifyListener(e -> dataGeoms.get(sp).setColor(tpc.getText()));

			bt.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					if (btn.getSelection()) {
						generator.getGeometries().add(dataGeoms.get(sp));
						tp.setEnabled(true);
						tt.setEnabled(true);
						th.setEnabled(true);
						btC.setEnabled(true);
					} else {
						generator.getGeometries().remove(dataGeoms.get(sp));
						tp.setEnabled(false);
						tt.setEnabled(false);
						th.setEnabled(false);
						btC.setEnabled(false);
					}

				}
			});
		}

		setControl(scroll);

	}

}
