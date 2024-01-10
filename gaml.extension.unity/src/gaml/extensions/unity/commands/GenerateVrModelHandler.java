package gaml.extensions.unity.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import gaml.extensions.unity.commands.wizard.ModelToVRWizard;
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
		final File file = new File(model.getProjectPath() + "/models/"+ model.getName() + "-VR.gaml");
		if (file.exists()) {
			file.delete();
		}
		String path = editor.getDocument().getResourceURI().path();
		createVRModel(path, model, file);
		return null;
	}
	
	 
	protected void createVRModel(String path, IModel model, final File file ) {
		Shell shell = WorkbenchHelper.getShell();
		ModelToVRWizard wizard = new ModelToVRWizard(path, model, file);
		WizardDialog dialog = new WizardDialog(shell, wizard);
	
		dialog.open();
	}
}