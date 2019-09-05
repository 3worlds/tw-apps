/**************************************************************************
 *  TW-APPS - Applications used by 3Worlds                                *
 *                                                                        *
 *  Copyright 2018: Jacques Gignoux & Ian D. Davies                       *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-APPS contains ModelMaker and ModelRunner, programs used to         *
 *  construct and run 3Worlds configuration graphs. All code herein is    *
 *  independent of UI implementation.                                     *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-APPS (3Worlds applications).                  *
 *                                                                        *
 *  TW-APPS is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-APPS is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-APPS.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
  **************************************************************************/

package au.edu.anu.twapps.mm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.errorMessages.archetype.UnknownErr;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.root.TwConfigFactory;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class MMModel implements IMMModel {
	// Interface supplied to the controller
	private TreeGraph<VisualNode, VisualEdge> visualGraph;
	private IMMController controller;

	// Should we avoid using javafx.beans.property? - make ModelMaker a boolean
	// change listener??

	public MMModel(IMMController controller) {
		this.controller = controller;
		buildNonEditableList();
		
	}
	private Map<String, List<String>> nonEditableMap = new HashMap<>();

	private void addEntry(ConfigurationNodeLabels nl,ConfigurationPropertyNames pn) {
		String nLabel = nl.label();
		String pKey = pn.key();
		List<String> lst=nonEditableMap.get(nLabel);
		if (lst==null) {
			lst = new ArrayList<>();
			nonEditableMap.put(nLabel,lst);
		}
		lst.add(pKey);		
	}
	private void buildNonEditableList() {
		for (ConfigurationNodeLabels key:ConfigurationNodeLabels.values()) {
			List<String> keys = new ArrayList<>();
			// default - subclass is never editable
			keys.add("subclass");			
 			nonEditableMap.put(key.label(),keys);
		}
		addEntry(ConfigurationNodeLabels.N_COMPONENT,ConfigurationPropertyNames.P_PARAMETERCLASS);
		addEntry(ConfigurationNodeLabels.N_SYSTEM,ConfigurationPropertyNames.P_PARAMETERCLASS);
		addEntry(ConfigurationNodeLabels.N_FUNCTION,ConfigurationPropertyNames.P_FUNCTIONCLASS);
	}
	@Override
	public boolean propertyEditable(String classId, String key) {
		if (!nonEditableMap.containsKey(classId))
			return true;
		if (!nonEditableMap.get(classId).contains(key))
			return true;
		// TODO build this when the archetype is ready
		return false;
	}

	private void onProjectClosing() {
		controller.onProjectClosing();
	}

	private void onProjectOpened() {
		ConfigGraph.validateGraph();
		controller.onProjectOpened(visualGraph);
	}

	@Override
	public void doClearJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSetJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMenuExit() {
		// TODO Auto-generated method stub

	}


	@Override
	public void doDeploy() {
		String arg1 = Project.getProjectFile().getName();
		ProcessBuilder experimentUI = new ProcessBuilder("java", "-jar", Project.getProjectUserName()+".jar", arg1);
		experimentUI.directory(Project.getProjectFile());
		experimentUI.inheritIO();
		try {
			experimentUI.start();
		} catch (Exception e) {
			ComplianceManager.add(new UnknownErr(CheckMessage.code20Deploy,e));
		}
	}

	@Override
	public void doNewProject() {
		if (!canClose())
			return;
		String promptId = "project1";
		boolean modified = true;
		promptId = Project.proposeId(promptId);
		while (modified) {
			String userName = Dialogs.getText("New project", "", "New project name:", promptId);
			if (userName == null)
				return;
			if (userName.equals(""))
				return;
			userName = Project.formatName(userName);
			String newName = Project.proposeId(userName);
			modified = !newName.equals(userName);
			promptId = newName;
		}
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}
		promptId = Project.create(promptId);
		ConfigGraph.setGraph(new TreeGraph<TreeGraphDataNode, ALEdge>(new TwConfigFactory()));
		NodeFactory cf = ConfigGraph.getGraph().nodeFactory();
		cf.makeNode(cf.nodeClass(N_ROOT.label()), promptId);
		visualGraph = new TreeGraph<VisualNode, VisualEdge>(new VisualGraphFactory());
		visualGraph.nodeFactory().makeNode(promptId);

		shadowGraph();

		visualGraph.root().setCategory();
		visualGraph.root().setPosition(0.1, 0.5);
		visualGraph.root().setCollapse(false);

		onProjectOpened();
		doSave();
	}

	private void shadowGraph() {
		for (VisualNode vn : visualGraph.nodes())
			vn.shadowElements(ConfigGraph.getGraph());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doOpenProject(File file) {
		// TODO Auto-generated method stub
		if (!canClose())
			return;
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}
		Project.open(file);
		ConfigGraph.setGraph(
				(TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter.loadGraphFromFile(Project.makeConfigurationFile()));
		visualGraph = (TreeGraph<VisualNode, VisualEdge>) FileImporter.loadGraphFromFile(Project.makeLayoutFile());
		shadowGraph();
		onProjectOpened();
	}

	@Override
	public void doSave() {
		new OmugiGraphExporter(Project.makeConfigurationFile()).exportGraph(ConfigGraph.getGraph());
		new OmugiGraphExporter(Project.makeLayoutFile()).exportGraph(visualGraph);
		GraphState.clear();
		ConfigGraph.validateGraph();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doImport() {
		// TODO Auto-generated method stub
		File file = Dialogs.getExternalProjectFile();
		if (file == null)
			return;
//		importGraph.resolveReferences();
//		Utilities.enforceNameProperty(importGraph);
//		AotNode importRoot = Utilities.get3worldsroot(importGraph);
//		String tmpName = null;
//		if (importRoot != null)
//			tmpName = importRoot.getName();
//		if (tmpName == null) {
//			Dialogs.errorAlert("Import project", "Unable to import project.",
//					"The file " + file.getName() + " has no root labelled " + N_GRAPHROOT.toString());
//			return;
//		}
//		final String name = NameUtils.validJavaName(NameUtils.wordUpperCaseName(tmpName));
//		if (!canClose("closing"))
//			return;
//		StatusText.message("Importing " + file.getName());
//		Cursor oldCursor = controller.setCursor(Cursor.WAIT);
//		Runnable task = () -> {
//			if (Project.isOpen())
//				onProjectClosing();
//			Project.create(name);
//			currentGraph = importGraph;
//			AotNode root = Utilities.get3worldsroot(currentGraph);
//			root.setName(name);
//			layoutGraph = Utilities.createLayoutGraph(currentGraph);
//			onProjectOpened();
//			Utilities.save(currentGraph, layoutGraph);
//			log.debug("Project importted: " + Project.getCurrentProjectTitle());
//			StatusText.clear();
//			controller.setCursor(oldCursor);
//		};
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		executor.execute(task);

	}

	@Override
	public boolean canClose() {
		if (!GraphState.changed())
			return true;
		switch (Dialogs.yesNoCancel("Project has changed", "Save changes before closing projecct?", "")) {
		case yes:
			doSave();
			return true;
		case no:
			return true;
		default:
			return false;
		}
	}


}
