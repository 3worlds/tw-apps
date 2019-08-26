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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import au.edu.anu.omhtk.jars.Jars;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.jars.DataJar;
import au.edu.anu.twapps.mm.jars.SimulatorJar;
import au.edu.anu.twapps.mm.jars.UserProjectJar;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.deploy.DeployClassFileMissing;
import au.edu.anu.twcore.errorMessaging.deploy.DeployClassOutOfDate;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.jars.ThreeWorldsJar;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;
import au.edu.anu.twcore.root.TwConfigFactory;
import au.edu.anu.twcore.setup.TwSetup;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.twcore.constants.FileType;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

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
	public void doLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDeploy(String mainClass) {
		/**
		 * Since initialise and casting are no longer done here I think we just use the
		 * current graph unless this process makes makes changes to the graph which
		 * should not be allowed!!!
		 */
		generateExecutable(mainClass);
		launchExperiment();
	}

	// JG - called by deploy()
	private void launchExperiment() {
		// TODO CHECK THESE ARGS: is arg1 the full path?? check
		String arg1 = Project.getProjectFile().getAbsolutePath();
		String arg2 = Project.getProjectName();
		ProcessBuilder experimentUI = new ProcessBuilder("java", "-jar", "simulator.jar", arg1, arg2);
		experimentUI.directory(Project.getProjectFile());
		experimentUI.inheritIO();
		try {
			experimentUI.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pullAllResources() {
		File fdstRoot = Project.makeFile(Project.RES);
		File fSrcRoot = UserProjectLink.srcRoot();
		List<File> files = (List<File>) FileUtils.listFiles(fSrcRoot, null, true);
		for (File srcFile : files) {
			String name = srcFile.getName();
			if (!(name.endsWith("java") || name.endsWith("class"))) {
				File dstFile = swapDirectory(srcFile, UserProjectLink.srcRoot(), fdstRoot);
				dstFile.mkdirs();
				FileUtilities.copyFileReplace(srcFile, dstFile);
			}
		}
	}

	private static File swapDirectory(File file, File from, File to) {
		File result = new File(file.getAbsolutePath().replace(from.getAbsolutePath(), to.getAbsolutePath()));
		return result;
	}

	private void pullAllCodeFiles() {
		File fDstRoot = Project.makeFile(ProjectPaths.CODE);
		String[] extensions = new String[] { "java" };
		List<File> lstSrcJava = (List<File>) FileUtils.listFiles(UserProjectLink.srcRoot(), extensions, true);
		for (File fSrcJava : lstSrcJava) {
			if (!fSrcJava.getName().equals("UserCodeRunner.java")) {
				File fSrcClass = UserProjectLink.classForSource(fSrcJava);
				File fDstJava = swapDirectory(fSrcJava, UserProjectLink.srcRoot(), fDstRoot);
				File fDstClass = swapDirectory(fSrcClass, UserProjectLink.classRoot(), fDstRoot);
				if (!fSrcClass.exists())
					ComplianceManager.add(new DeployClassFileMissing(fSrcClass, fSrcJava));
				else {
					try {
						FileTime ftSrc = Files.getLastModifiedTime(fSrcJava.toPath());
						FileTime ftCls = Files.getLastModifiedTime(fSrcClass.toPath());
						Long ageJava = ftSrc.toMillis();
						Long ageClass = ftCls.toMillis();
						if (ageJava > ageClass)
							ComplianceManager.add(new DeployClassOutOfDate(fSrcJava, fSrcClass, ftSrc, ftCls));
					} catch (IOException e) {
						e.printStackTrace();
					}
					FileUtilities.copyFileReplace(fSrcJava, fDstJava);
					FileUtilities.copyFileReplace(fSrcClass, fDstClass);
				}
			}
		}
	}

	// JG - called by deploy()
	@SuppressWarnings("unchecked")
	private void generateExecutable(String mainClass) {
		// Get all data source nodes with FileType properties - TODO this node yet to be
		// implemented in arch
		Set<File> dataFiles = new HashSet<>();
		List<TreeGraphDataNode> experiments = (List<TreeGraphDataNode>) get(ConfigGraph.getGraph().root().getChildren(),
				selectOneOrMany(hasTheLabel(N_EXPERIMENT.label())));
		for (TreeGraphDataNode experiment : experiments) {
			List<TreeGraphDataNode> dataSources = (List<TreeGraphDataNode>) get(experiment.getChildren(),
					selectZeroOrMany(hasTheLabel(N_DATASOURCE.label())));
			for (TreeGraphDataNode dataSource : dataSources) {
				// TODO property enum yet to be defined for this node class
				File f = ((FileType) dataSource.properties().getPropertyValue(P_DESIGN_FILE.key())).getFile();
				dataFiles.add(f);
			}
		}
//		Jars dataPacker = new DataJar(dataFiles);
//		// Save to project root for deployment
//		String dataJarName = Project.getProjectName()+"Data.jar";
//		File dataFile = Project.makeFile(dataJarName);
//		dataPacker.saveJar(dataFile);
		// Are we running from within a jar?
		if (Jars.getRunningJarFilePath(this.getClass()) == null) {
			// Create threeWorlds.jar not sure about all this now
			// not this version number???
			Jars twJar = new ThreeWorldsJar(TwSetup.VERSION_MAJOR, TwSetup.VERSION_MINOR, TwSetup.VERSION_MICRO);
			File twFile = Project.makeFile("threeWorlds.jar");
			// we need to make the contents??
			twJar.saveJar(twFile);
		}
		Set<String> userLibraries = new HashSet<>();
		Set<File> srcFiles = new HashSet<>();
		Set<File> resFiles = new HashSet<>();
		Set<File> userCodeJars = new HashSet<>();
//		if (controller.haveUserProject()) {
		if (UserProjectLink.haveUserProject()) {
//			// 1) Move user dependencies to './modelCode
//			// TODO: change to ./modelCode/lib
			Set<String> libraryExclusions = new HashSet<>();
			libraryExclusions.add(TwPaths.TW_DEP_JAR);
			userLibraries = copyUserLibraries(UserProjectLink.getUserLibraries(libraryExclusions));
			pullAllCodeFiles();
			pullAllResources();
		}
		// make one userCodeJar in root of project
		loadModelCode(srcFiles, resFiles);
//		Jars upj = new UserProjectJar(srcFiles, resFiles);
//		String codeJarName = Project.getProjectName()+"Code.jar";
//
//		File userCodeJarFile = Project.makeFile(codeJarName);
//		upj.saveJar(userCodeJarFile);
//		userCodeJars.add(userCodeJarFile);
		//skip all this data, code jars and put all project specific stuff in the Simulator jar 
		Jars simPacker = new SimulatorJar(mainClass,dataFiles,srcFiles,resFiles,userLibraries);
//		Jars executable = new SimulatorJar(dataFiles, userCodeJars, userLibraries);
		File executableJarFile = Project.makeFile(Project.getProjectName()+"Sim.jar");
		simPacker.saveJar(executableJarFile);
	}

	private Set<String> copyUserLibraries(File[] fJars) {
		/**
		 * Copy any libraries used by the Java project to the targetDir. These can then
		 * be referenced in the simulator.jar
		 */

		File targetDir = Project.makeFile(ProjectPaths.LIB);
		targetDir.mkdirs();
		Set<String> result = new HashSet<>();
		String relativePath = "." + targetDir.getAbsolutePath().replace(Project.makeFile().getAbsolutePath(), "");
		if (fJars == null)
			return result;
		for (File fJar : fJars) {
			File outPath = new File(targetDir.getAbsolutePath() + File.separator + fJar.getName());
			try {
				Files.copy(fJar.toPath(), outPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
				String entry = relativePath + "/" + outPath.getName();
				result.add(entry.replace("\\", "/"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private void loadModelCode(Set<File> srcFiles, Set<File> resFiles) {
		File srcRoot = Project.makeFile(ProjectPaths.CODE);
		File resRoot = Project.makeFile(ProjectPaths.RES);
		if (srcRoot.exists())
			srcFiles.addAll(FileUtils.listFiles(srcRoot, null, true));
		if (resRoot.exists())
			resFiles.addAll(FileUtils.listFiles(resRoot, null, true));
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

	private Map<String, List<String>> nonEditableMap = new HashMap<>();

	@Override
	public boolean propertyEditable(String classId, String key) {
		if (!nonEditableMap.containsKey(classId))
			return true;
		if (!nonEditableMap.get(classId).contains(key))
			return true;
		// TODO build this when the archetype is ready
		return true;
	}

}
