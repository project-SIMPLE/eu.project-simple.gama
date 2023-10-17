package gaml.extensions.unity.commands.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import msi.gama.kernel.model.IModel;

public class WizardPageAgentsToSend extends WizardPage {

	IModel model;

    private Composite container;
    
    VRModelGenerator generator;
    
	protected WizardPageAgentsToSend(IModel model, VRModelGenerator gen) {
		 super("AgentsToSend");
		 setTitle("Define the agents and geometries to send");
		 setDescription("Please enter information about the agents to send");
		 this.model = model;
		 this.generator = gen;
	}
	
	

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		 container.setLayout(new FillLayout(SWT.VERTICAL));
		 Group group = new Group(container, SWT.NONE);

		 group.setLayout(new GridLayout(1, false));
			group.setText("Species of agents to send to Unity");
			

		for (String sp : model.getAllSpecies().keySet()) {
			if (sp.equals(model.getName())) continue;
			Button bt = new Button(group, SWT.CHECK);
			bt.addSelectionListener(new SelectionAdapter() {

		        @Override
		        public void widgetSelected(SelectionEvent event) {
		            Button btn = (Button) event.getSource();
		            if(btn.getSelection()) {
		            	generator.getSpeciesToSend().add(btn.getText() );
		            } else {
		            	generator.getSpeciesToSend().remove(btn.getText() );
		            }
		            
		        }
		    });
			bt.setText(sp);
			bt.pack();
		}
		 
		 setControl(container);
	       
		
	}

}
