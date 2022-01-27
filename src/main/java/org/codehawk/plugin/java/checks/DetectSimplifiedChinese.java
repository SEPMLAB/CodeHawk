package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key= "DetectSimplifiedChinese")
public class DetectSimplifiedChinese extends IssuableSubscriptionVisitor {
    
    @Override
    public List<Tree.Kind> nodesToVisit(){
        return Collections.singletonList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree){
        ClassTree ct = (ClassTree) tree;
        for(Tree t : ct.members()){
            if (t.is(Tree.Kind.CLASS) || t.is(Tree.Kind.METHOD) || t.is(Tree.Kind.VARIABLE) || t.is(Tree.Kind.ENUM)){
                for(SyntaxTrivia syntaxTrivia: t.firstToken().trivias()){ 
                    String comment = syntaxTrivia.comment();
                    if(comment != null && (comment.trim().startsWith("/*") || comment.trim().startsWith("//"))){
                        issuing(comment, syntaxTrivia);
                    }
                }
            }
        }
    }

  
    private boolean isSimplifiedChinese(String comment){
        String regEx = "[\u2E80-\u2FD5\u3190-\u319f\u3400-\u4DBF\u4E00-\u9FCC\uF900-\uFAAD]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(comment);
        
        return m.find();
    }

    private void issuing(String comment, SyntaxTrivia syntaxTrivia){
        if (isSimplifiedChinese(comment)){
            addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
        }
    }
}