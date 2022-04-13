package it.unibo.pcd.assignment.event;

import io.vertx.core.*;
import java.util.function.Consumer;

public interface ProjectAnalyzer {

	/**
	 * Async method to retrieve the report about a specific interface,
	 * given the full path of the interface source file
	 *
	 * @param srcInterfacePath
	 * @return
	 */
	Future<InterfaceReport> getInterfaceReport(String srcInterfacePath);

	/**
	 * Async method to retrieve the report about a specific class,
	 * given the full path of the class source file
	 * 
	 * @param srcClassFileName
	 * @return
	 */
	Future<ClassReport> getClassReport(String srcClassPath);

	/**
	 * Async method to retrieve the report about a package,
	 * given the full path of the package folder
	 * 
	 * @param srcClassFileName
	 * @return
	 */
	Future<PackageReport> getPackageReport(String srcPackagePath);

	/**
	 * Async method to retrieve the report about a project
	 * given the full path of the project folder 
	 * 
	 * @param srcProjectFolderPath
	 * @return
	 */
	Future<ProjectReport> getProjectReport(String srcProjectFolderPath);
	
	/**
	 * Async function that analyze a project given the full path of the project folder,
	 * executing the callback each time a project element is found 
	 * 
	 * @param srcProjectFolderName
	 * @param callback
	 */
	void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback);
}
