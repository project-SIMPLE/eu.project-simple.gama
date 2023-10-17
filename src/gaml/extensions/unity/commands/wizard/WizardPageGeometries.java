package gaml.extensions.unity.commands.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import msi.gama.kernel.model.IModel;

public class WizardPageGeometries extends WizardPage {

	IModel model;

    private Composite container;
    
    VRModelGenerator generator;
    
    List<String> itemsD;
    
	protected WizardPageGeometries(IModel model, VRModelGenerator gen) {
		 super("Geometries");
		 setTitle("Define the information about the background geometries");
		 setDescription("Please enter information about the background geometries");
		 this.model = model;
		 this.generator = gen;
	}
	

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		 container.setLayout(new FillLayout(SWT.VERTICAL));
		 Group group = new Group(container, SWT.NONE);

		 group.setLayout(new GridLayout(2, false));
		 group.setText("Species of agents to send to Unity as static geometries");
			
		 Map<String,DataGeometries> dataGeoms = new HashMap<>();
		for (String sp : model.getAllSpecies().keySet()) {
			
			if (sp.equals(model.getName())) continue;
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
		        public void widgetSelected(SelectionEvent event) {
		            Button btn = (Button) event.getSource();
		            dataGeoms.get(sp).setHasCollider(btn.getSelection());
		            
		        }
		    });
			btC.setText("Collider?");
			btC.pack();
			 Label lem = new Label(groupSp, SWT.LEFT);
			 lem.setText("" );
			
			 Label lp = new Label(groupSp, SWT.LEFT);
				lp.setText("Buffer:" );
				Text tp =  new Text(groupSp, SWT.BORDER);
				tp.setText(dataGeoms.get(sp).getBuffer().toString());
			    tp.addModifyListener(new ModifyListener() {
					
					@Override
				
					public void modifyText(ModifyEvent e) {
						Double v = Double.valueOf(tp.getText());
						if (v != null)
							 dataGeoms.get(sp).setBuffer(v);
					}
			    });
			    
			    Label lh = new Label(groupSp, SWT.LEFT);
			    lh.setText("Height:" );
				Text th =  new Text(groupSp, SWT.BORDER);
				th.setText(dataGeoms.get(sp).getHeight().toString());
				th.addModifyListener(new ModifyListener() {
					
					@Override
				
					public void modifyText(ModifyEvent e) {
						Double v = Double.valueOf(th.getText());
						if (v != null)
							 dataGeoms.get(sp).setHeight(v);
					}
			    });
				
				 Label lt = new Label(groupSp, SWT.LEFT);
				  lt.setText("Tag:" );
					Text tt =  new Text(groupSp, SWT.BORDER);
					tt.setText(dataGeoms.get(sp).getTag());
					tt.addModifyListener(new ModifyListener() {
						
						@Override
					
						public void modifyText(ModifyEvent e) {
							dataGeoms.get(sp).setTag(tt.getText());
						}
				    });
					
					bt.addSelectionListener(new SelectionAdapter() {

				        @Override
				        public void widgetSelected(SelectionEvent event) {
				            Button btn = (Button) event.getSource();
				            if(btn.getSelection()) {
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
		 
		 setControl(container);
		
	}

}
