package gaml.extensions.unity.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException; 
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class GenerateVrModelHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final GamlEditor editor = (GamlEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		
	
		
		final IModel model = editor.getDocument().readOnly(state -> GamlModelBuilder.getDefaultInstance().compile(state.getURI(), null));
		if (model == null) {
			return null;
		}
		final File file = new File(model.getProjectPath() +  model.getName() + ".gaml");
		if (file.exists()) {
			file.delete();
		}

		final IResource resource = editor.getResource();
		final IContainer container = resource.getProject();
		
		final IFile fileP = container.getFile(new Path(model.getName() + ".gaml"));
		return null;
	}
	
	
	protected void createVRModel() {
	//WizardDialog dialog = new WizardDialog(WorkbenchHelper.getShell(), new YourWizardClass());
	
	//dialog.open();
	}
}