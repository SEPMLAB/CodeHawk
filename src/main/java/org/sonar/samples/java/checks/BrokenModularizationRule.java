package org.sonar.samples.java.checks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.MethodNode;
import org.smell.metricruler.Atfd;
import org.smell.metricruler.Fdp;
import org.smell.metricruler.Laa;
import org.smell.metricruler.NopaAndNoam;
import org.smell.metricruler.Wmc;
import org.smell.metricruler.Woc;
import org.smell.rule.pluginregister.MyJavaRulesDefinition;
import org.smell.smellruler.BrokenModularization;
import org.smell.smellruler.DataClass;
import org.smell.smellruler.FeatureEnvy;
import org.smell.smellruler.Smell;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;

//TODO
//Not implement yet:
//method body以外的ATFD
//方法參數中的ATFD ex: setValue(B.getValue);
//return statement中的內容
// +-*% 等含有運算元的內容
// 如何修改Tag
//Check wmc取的值對不對
//記錄所有method的LAA
//偵測api的FE新增為另外一條rule
//TCSE 2018-Software Smell Ontology Model and Detection Method for Architecture Smells, Design Smells and Code Smells
//Design and Implementation of Software Smell Ontology and Software Smell Detection SonarQube Extension
//Ontology知識本體
//設計與實作軟體氣味知識本體與軟體氣味偵測之SonarQube擴充
//內部類別中有FE的時候 reportIssue的位置應該要在內部類別上
//弄清楚sonarQUbe WMC的定義
//
@Rule(key = "S120")

public class BrokenModularizationRule extends BaseTreeVisitor implements Sensor, JavaFileScanner {

	private JavaFileScannerContext context;
	private static List<ClassNode> classes = new ArrayList<ClassNode>();
	private Smell smell = new BrokenModularization();

	// 逐一走訪專案中的所有classes
	// 先對一個file中的每個class各呼叫一次visitClass 接著對這個file呼叫一次scanFile

	// 第一次訪問某個class的時候先把class放入這個準備進行分析的list(classes)中
	// 在分析每個file的時候檢查 classes中存放的每個ClassNode是否有smell

	// 一直需要檢查取出來的東西是否為null 想一個比較general的解決方法來取代 一直用if(XXx!=null)做check
	@Override
	public void visitClass(ClassTree classTree) {
		ClassNode classNode = new ClassNode(classTree);
		File file = context.getFile();
		int classComplexity = context.getComplexityNodes(classTree).size();
		classNode.setWMC(classComplexity);
		classNode.setFile(file);
		classes.add(classNode);
		super.visitClass(classTree);
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Compute number of files");
		descriptor.onlyOnLanguage("java");
		descriptor.createIssuesForRuleRepositories(MyJavaRulesDefinition.REPOSITORY_KEY);
	}

	// execute方法會在scanFile方法執行完後才執行
	@Override
	public void execute(SensorContext context) {
		for (ClassNode classNode : classes) {
			if (classNode.haveSmell(smell)) {
				String filePath = "D:\\test\\Broken ModularizationSmell.txt";
				String info = "Broken Modularization detected in : " + classNode.getName() + "\r\n";
				String path = classNode.getFile().getPath();
				FileSystem fs = context.fileSystem();
				// InputFile是相對於sonar.sources 的檔案路徑 Ex: src\chess\ChessBoard.java
				// File則是絕對路徑 Ex: C:\Users\\user\eclipse-workspace\Expert
				// System\src\chess\\ChessBoard.java

				Iterable<InputFile> javaFiles = fs.inputFiles(fs.predicates().hasPath(path));
				for (InputFile javaFile : javaFiles) {
					try {
						logOnFile(filePath, info);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					// reportBroken Modularization
					NewIssue brokenModularizationIssue = context.newIssue().forRule(MyJavaRulesDefinition.RULE_ON_LINE_1)
							// gap is used to estimate the remediation cost to fix the debt
							.gap(2.0);
					int issueStartLine = classNode.getStartLine();
					NewIssueLocation brokenModularizationLocation = brokenModularizationIssue.newLocation().on(javaFile).at(javaFile.selectLine(issueStartLine)).message("Broken Modularization Location!");
					brokenModularizationIssue.at(brokenModularizationLocation);
					List<MethodNode> methods = classNode.getAllMethodNodes();
					if (classNode.getDataClass() != null) {
						int classStartLine = classNode.getStartLine();
						String dataClassMessage = "Data Class detected" + "\r\n" 
											  + "WOC: " + ((Woc)((DataClass)classNode.getDataClass()).getWoc()).getValue() + "\r\n" 
											  + "WMC: " + ((Wmc)((DataClass)classNode.getDataClass()).getWmc()).getValue() + "\r\n" 
											  + "NOPA + NOAM: " + ((NopaAndNoam)((DataClass)classNode.getDataClass()).getNopaAndNoam()).getValue() + "\r\n" 
								;
						NewIssueLocation dataClassLocation = brokenModularizationIssue.newLocation().on(javaFile).at(javaFile.selectLine(classStartLine)).message(dataClassMessage);
						brokenModularizationIssue.addLocation(dataClassLocation);
					}

					for (MethodNode method : methods) {
						if (method.getFeatureEnvy() != null) {
							int methodStartLine = method.getStartLine();
							String featureEnvyMessage = "Feature Envy detected" + "\r\n" 
									  + "ATFD: " + ((Atfd)((FeatureEnvy)method.getFeatureEnvy()).getAtfd()).getMetricValue() + "\r\n" 
									  + "LAA: "  + ((Laa) ((FeatureEnvy)method.getFeatureEnvy()).getLaa()) .getMetricValue() + "\r\n" 
									  + "FDP: "  + ((Fdp) ((FeatureEnvy)method.getFeatureEnvy()).getFdp()) .getMetricValue() + "\r\n" 
						;
							NewIssueLocation featureEnvyLocation = brokenModularizationIssue.newLocation().on(javaFile).at(javaFile.selectLine(methodStartLine)).message(featureEnvyMessage);
							brokenModularizationIssue.addLocation(featureEnvyLocation);
						}
					}
					brokenModularizationIssue.save();
				}
			}
		}
	}

	public static List<ClassNode> getClasses() {
		return classes;
	}

	// 每掃描一個檔案 就會執行一次scanFile方法
	@Override
	public void scanFile(JavaFileScannerContext context) {
		this.context = context;
		CompilationUnitTree cut = context.getTree();
		scan(cut);
	}

	public static void logOnFile(String filePath, String issueName) throws ClassNotFoundException {
		String path = filePath;
		File f = new File(path);
		if (!f.exists()) {
			try {
				f.createNewFile();
				writeToFile(f, issueName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			writeToFile(f, issueName);
		}
	}

	private static void writeToFile(File f, String issueName) {
		try {
			FileWriter fw = null;
			fw = new FileWriter(f, true);
			String log = issueName;
			fw.write(log);
			if (fw != null) {
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}