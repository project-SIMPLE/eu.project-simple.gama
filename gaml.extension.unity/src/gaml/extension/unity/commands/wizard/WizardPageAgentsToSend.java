/*******************************************************************************************************
 *
 * WizardPageAgentsToSend.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.commands.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import gama.core.kernel.model.IModel;

/**
 * The Class WizardPageAgentsToSend.
 */
public class WizardPageAgentsToSend extends WizardPage {

	/** The model. */
	IModel model;

	/** The container. */
	private Composite container;

	/** The generator. */
	VRModelGenerator generator;

	/**
	 * Instantiates a new wizard page agents to send.
	 *
	 * @param model
	 *            the model
	 * @param gen
	 *            the gen
	 */
	protected WizardPageAgentsToSend(final IModel model, final VRModelGenerator gen) {
		super("AgentsToSend");
		setTitle("Define the agents and geometries to send");
		setDescription("Please enter information about the agents to send");
		this.model = model;
		this.generator = gen;
	}

	@Override
	public void createControl(final Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		Group group = new Group(container, SWT.NONE);

		group.setLayout(new GridLayout(1, false));
		group.setText("Species of agents to send to Unity");

		for (String sp : model.getAllSpecies().keySet()) {
			if (sp.equals(model.getName())) { continue; }
			Button bt = new Button(group, SWT.CHECK);
			bt.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					Button btn = (Button) event.getSource();
					if (btn.getSelection()) {
						generator.getSpeciesToSend().add(btn.getText());
					} else {
						generator.getSpeciesToSend().remove(btn.getText());
					}

				}
			});
			bt.setText(sp);
			bt.pack();
		}

		setControl(container);

	}

}
