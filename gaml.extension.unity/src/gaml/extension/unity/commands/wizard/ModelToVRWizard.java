package gaml.extension.unity.commands.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import msi.gama.kernel.model.IModel;

public class ModelToVRWizard extends Wizard {

	IModel model;
	VRModelGenerator generator;
	File file;
	String path;
	
	IWizardPage finalPage;
	
	
	public ModelToVRWizard(String path, IModel model, final File file) {
		super();
		generator = new VRModelGenerator();
		 setWindowTitle("VR Experiment generation");
		// setNeedsProgressMonitor(true);
		 this.model = model;
		 this.file = file; 
		 this.path = path;
		 
	}
	
	 public void addPages() {
		 
		 WizardPageGeneralInformation wizI = new WizardPageGeneralInformation(path, model, generator);
		addPage(wizI);
		 WizardPageDisplay wizD = new WizardPageDisplay(model, generator);
		 wizI.setwDisplay(wizD);
		addPage(wizD);
		addPage(new WizardPageAgentsToSend(model, generator));
		 addPage(new WizardPageGeometries(model, generator));
		 finalPage = new WizardPagePlayer(model, generator);
		 
		 addPage(finalPage);
		 getShell().setSize(600, 600);
	 }
	
	 @Override
		public boolean canFinish()
	 {
		 if(getContainer().getCurrentPage() == finalPage)
			 return true;
		 else
			 return false;
	 }
	
	@Override
	public boolean performFinish() {
		String modelVRStr = generator.BuildVRModel();
		
		try {
			FileWriter fw = new FileWriter(file);
			
			fw.write(modelVRStr);
			fw.close();
			  IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
			    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    try {
			        IDE.openEditorOnFileStore( page, fileStore );
			    } catch ( PartInitException e ) {
			        e.printStackTrace();
			        System.out.println("An Error occured while loading the file.");
			    }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

	
	

}
