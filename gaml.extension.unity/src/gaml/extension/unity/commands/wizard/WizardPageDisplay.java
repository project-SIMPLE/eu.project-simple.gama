package gaml.extension.unity.commands.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import msi.gama.kernel.model.IModel;
import msi.gama.outputs.IOutput;

public class WizardPageDisplay extends WizardPage {

	IModel model;

	private Composite container;

	VRModelGenerator generator;

	List<String> itemsD;

	protected WizardPageDisplay(IModel model, VRModelGenerator gen) {
		super("Display");
		setTitle("Define the information about the display");
		setDescription("Please enter information about displays");
		this.model = model;
		this.generator = gen;
	}

	public void updateExperiment() {
		itemsD = new ArrayList<String>();
		for (IOutput d : model.getExperiment(generator.getExperimentName()).getOriginalSimulationOutputs())
			itemsD.add(d.getOriginalName());

	}

	Combo cd;
	Group groupDisplayH;

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		Group groupDisplay = new Group(container, SWT.NONE);
		groupDisplay.setLayout(new GridLayout(2, false));
		groupDisplay.setText("Information about the display");

		Label ld = new Label(groupDisplay, SWT.LEFT);
		ld.setText("Main Display:");

		cd = new Combo(groupDisplay, SWT.READ_ONLY);

		/*
		 * for (IExperimentPlan ep : model.getExperiments()) {
		 * 
		 * for (IDisplayOutput d :ep.getExperimentOutputs().getDisplayOutputs())
		 * itemsD.add(d.getName()); }
		 */

		if (itemsD != null && itemsD.size() > 0) {

			cd.setItems((String[]) itemsD.toArray(new String[itemsD.size()]));
			if (!itemsD.isEmpty()) {
				cd.setText(itemsD.get(0));
				generator.setMainDisplay(cd.getText());
			}
			groupDisplayH = new Group(container, SWT.NONE);
			groupDisplayH.setLayout(new GridLayout(2, false));
			groupDisplayH.setText("Displays to hide");

			for (String sp : itemsD) {
				Button bt = new Button(groupDisplayH, SWT.CHECK);
				bt.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						if (btn.getSelection()) {
							generator.getDisplaysToHide().add(btn.getText());
						} else {
							generator.getDisplaysToHide().remove(btn.getText());
						}

					}
				});
				bt.setText(sp);
				bt.pack();
			}
			cd.addSelectionListener(new SelectionAdapter() {
				public void widgetDefaultSelected(SelectionEvent e) {
					generator.setMainDisplay(cd.getText());
				}
			});
		}
		/*
		 * Group groupDisplayH = new Group(container, SWT.NONE);
		 * groupDisplayH.setLayout(new GridLayout(2, false));
		 * groupDisplayH.setText("Displays to hide");
		 * 
		 * for (IExperimentPlan ep : model.getExperiments()) { for (IDisplayOutput d
		 * :ep.getExperimentOutputs().getDisplayOutputs()){ String sp = d.getName();
		 * Button bt = new Button(groupDisplayH, SWT.CHECK); bt.addSelectionListener(new
		 * SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent event) { Button btn =
		 * (Button) event.getSource(); if(btn.getSelection()) {
		 * generator.getDisplaysToHide().add(btn.getText()); } else {
		 * 
		 * generator.getDisplaysToHide().remove(btn.getText()); }
		 * 
		 * } }); bt.setText(sp); bt.pack(); } }
		 */

		setControl(container);

	}

}
