package org.smell.astmodeler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.smell.smellruler.Smell;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

public class MethodNode implements Node {
	private MethodTree methodTree;
	private String methodName;
	private BlockTree blockTree;
	private File file;
	private int startLine;
	private Smell featureEnvy;

	public MethodNode(MethodTree methodTree) {
		this.methodTree = methodTree;
		this.methodName = this.methodTree.simpleName().name();
		this.blockTree = this.methodTree.block();
		this.startLine = methodTree.openParenToken().line();
	}

	@Override
	public String getName() {
		return this.methodName;
	}

	public BlockTree getBlockTree() {
		return this.blockTree;
	}

	private List<StatementTree> visitBlockStatment(StatementTree s) {
		List<StatementTree> statements = new ArrayList<StatementTree>();
		if (s.is(Tree.Kind.EXPRESSION_STATEMENT) || s.is(Tree.Kind.VARIABLE)) {
			statements.add(s);
		}else if (isSelectionStatements(s)) {
			// 遞迴走訪 if else for switch等等區塊
			visitSelectionStatement(s, statements);
		}
		return statements;
	}

	private boolean isSelectionStatements(StatementTree statement) {
		return statement.is(Tree.Kind.IF_STATEMENT) | statement.is(Tree.Kind.DO_STATEMENT) | statement.is(Tree.Kind.WHILE_STATEMENT) | statement.is(Tree.Kind.FOR_STATEMENT) | statement.is(Tree.Kind.SWITCH_STATEMENT) | statement.is(Tree.Kind.FOR_EACH_STATEMENT);
	}

	private void visitSelectionStatement(StatementTree statementTree, List<StatementTree> statements) {
		if (statementTree.is(Tree.Kind.IF_STATEMENT)) {
			parseIfStatementTree(statementTree, statements);
		} else if (statementTree.is(Tree.Kind.DO_STATEMENT)) {
			StatementTree doWhileStatement = ((DoWhileStatementTree) statementTree).statement();
			statements = addStatements(statements, getStatements(doWhileStatement));
		} else if (statementTree.is(Tree.Kind.WHILE_STATEMENT)) {
			StatementTree whileStatement = ((WhileStatementTree) statementTree).statement();
			statements = addStatements(statements, getStatements(whileStatement));
		} else if (statementTree.is(Tree.Kind.FOR_STATEMENT)) {
			StatementTree forStatement = ((ForStatementTree) statementTree).statement();
			statements = addStatements(statements, getStatements(forStatement));
		} else if (statementTree.is(Tree.Kind.SWITCH_STATEMENT)) {
			StatementTree switchStatement = statementTree;
			statements = addStatements(statements, getStatements(switchStatement));
		} else if (statementTree.is(Tree.Kind.FOR_EACH_STATEMENT)) {
			StatementTree forEachStatement = ((ForEachStatement) statementTree).statement();
			statements = addStatements(statements, getStatements(forEachStatement));
		}
	}

	private void parseIfStatementTree(StatementTree s, List<StatementTree> statements) {
		StatementTree elseStatement = ((IfStatementTree) s).elseStatement();
		// elseStatement 可能會是某種ifStatement (else if)
		if (notNull(elseStatement)) {
			if (elseStatement.is(Tree.Kind.IF_STATEMENT)) {
				visitSelectionStatement(elseStatement, statements);
			} else {
				statements = addStatements(statements, getStatements(elseStatement));
			}
		}
		StatementTree thenStatement = ((IfStatementTree) s).thenStatement();
		statements = addStatements(statements, getStatements(thenStatement));
	}

	private List<StatementTree> visitSwitchStatements(StatementTree s) {
		List<CaseGroupTree> cases = ((SwitchStatementTree) s).cases();
		List<StatementTree> caseStatements = new ArrayList<StatementTree>();
		if (notNull(cases)) {
			for (CaseGroupTree c : cases) {
				List<StatementTree> caseBlock = c.body();
				caseStatements = addStatements(caseStatements, caseBlock);
			}
		}
		return caseStatements;
	}
	
	
	//TODO
	//StatementTree底下還有很多其他的tree : ex: ExpressionStatmentTree, ForStatementTree, ThrowStatementTree...
	private List<StatementTree> getStatements(StatementTree s) {
		if (notNull(s)) {
			if (s.is(Tree.Kind.SWITCH_STATEMENT)) {
				return visitBlock(visitSwitchStatements(s));
			} else if (s.is(Tree.Kind.EXPRESSION_STATEMENT)) {
				//Not implemtnt yet
			}else if (s.is(Tree.Kind.FOR_STATEMENT)) {
				//Not implemtnt yet
			}else if (s.is(Tree.Kind.BLOCK)) {
				return visitBlock(getBlockStatements((BlockTree) s));
			}else if (!s.is(Tree.Kind.RETURN_STATEMENT)) {
				//Not implemtnt yet
			}
		}
		return null;
	}

	private List<StatementTree> addStatements(List<StatementTree> originStatements, List<StatementTree> newStatements) {
		if (notNull(newStatements)) {
			originStatements.addAll(newStatements);
			return originStatements;
		}
		return originStatements;
	}

	private List<StatementTree> visitBlock(List<StatementTree> blockStatements) {
		List<StatementTree> allStatements = new ArrayList<StatementTree>();
		if (notNull(blockStatements)) {
			for (StatementTree s : blockStatements) {
				List<StatementTree> statementsInBlock = this.visitBlockStatment(s);
				if (notNull(statementsInBlock)) {
					allStatements = addStatements(allStatements, statementsInBlock);
				}
			}
		}
		return allStatements;
	}

	private List<StatementTree> getBlockStatements(BlockTree blockTree) {
		if (notNull(blockTree)) {
			return blockTree.body();
		}
		return null ;
	}

	public List<StatementTree> getStatementsInMethodNode() {
		BlockTree blockTree = this.getBlockTree();
		List<StatementTree> blockStatements = this.getBlockStatements(blockTree);
		
		return visitBlock(blockStatements);
	}

	private boolean notNull(Object object) {
		return object != null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public Smell getFeatureEnvy() {
		return featureEnvy;
	}

	public void setFeatureEnvy(Smell featureEnvy) {
		this.featureEnvy = featureEnvy;
	}
}