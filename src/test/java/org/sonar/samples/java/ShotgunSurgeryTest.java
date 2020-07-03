package org.sonar.samples.java;

import java.util.ArrayList;
import java.util.List;

import org.codehawk.plugin.java.checks.ShotgunSurgery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.java.model.InternalSyntaxToken;
import org.sonar.java.model.declaration.ClassTreeImpl;
import org.sonar.java.model.declaration.MethodTreeImpl;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

public class ShotgunSurgeryTest {

	private static InternalSyntaxToken openBraceToken;
	private static List<Tree> members = new ArrayList<Tree>();
	private static InternalSyntaxToken closeBraceToken;
	private static ShotgunSurgery ShotgunSurgery;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ShotgunSurgery = new ShotgunSurgery();
		//MethodTreeImpl methodTreeImpl = new MethodTreeImpl(null, null, null, openBraceToken, null, null, closeBraceToken);
		//members.add(methodTreeImpl);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		ShotgunSurgery.nodesToVisit();
		ClassTreeImpl classTreeImpl = new ClassTreeImpl(Kind.CLASS, openBraceToken, members, closeBraceToken);
		ShotgunSurgery.visitNode(classTreeImpl);
	}

}
