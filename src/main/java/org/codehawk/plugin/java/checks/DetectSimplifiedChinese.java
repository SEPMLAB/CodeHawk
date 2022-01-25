package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
// import org.sonar.plugins.java.api.tree.AnnotationTree;
// import org.sonar.plugins.java.api.tree.ArrayDimensionTree;
// import org.sonar.plugins.java.api.tree.AssertStatementTree;
// import org.sonar.plugins.java.api.tree.BlockTree;
// import org.sonar.plugins.java.api.tree.ClassTree;
// import org.sonar.plugins.java.api.tree.MethodTree;
// import org.sonar.plugins.java.api.tree.ReturnStatementTree;
// import org.sonar.plugins.java.api.tree.StatementTree;
// import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key= "DetectSimplifiedChinese")
public class DetectSimplifiedChinese extends IssuableSubscriptionVisitor {
    
    @Override
    public List<Tree.Kind> nodesToVisit(){
        return Collections.singletonList(Tree.Kind.CLASS);
        // return ImmutableList.of(Tree.Kind.TRIVIA);
    }

    @Override
    public void visitNode(Tree tree){
        ClassTree ct = (ClassTree) tree;
        for(Tree t : ct.members()){
            if (t.is(Tree.Kind.CLASS)){
                for(SyntaxTrivia syntaxTrivia: t.firstToken().trivias()){ 
                    String comment = syntaxTrivia.comment();
                    if(comment != null && comment.trim().startsWith("/*")){// <= get /** */
                        if (isSimplifiedChinese(comment)){
                            addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
                        }
                    }
                    else if (comment != null && comment.trim().startsWith("//")){ // <= get //
                        if (isSimplifiedChinese(comment) == true){
                            addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
                        }
                    }
                }
            }else if (t.is(Tree.Kind.METHOD)){
                for(SyntaxTrivia syntaxTrivia: t.firstToken().trivias()){ 
                    String comment = syntaxTrivia.comment();
                    if(comment != null && comment.trim().startsWith("/*")){// <= get /** */
                        if (isSimplifiedChinese(comment) == true){
                            addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
                        }
                    }
                    else if (comment != null && comment.trim().startsWith("//")){ // <= get //
                        if (isSimplifiedChinese(comment) == true){
                            addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
                        }
                    }
                }
            // }else if (t.is(Tree.Kind.VARIABLE)){
            //     for(SyntaxTrivia syntaxTrivia: t.firstToken().trivias()){ 
            //         String comment = syntaxTrivia.comment();
            //         if(comment != null && comment.trim().startsWith("/*")){// <= get /** */
            //             if (isSimplifiedChinese(comment) == true){
            //                 addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
            //             }
            //         }
            //         else if (comment != null && comment.trim().startsWith("//")){ // <= get //
            //             if (isSimplifiedChinese(comment) == true){
            //                 addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
            //             }
            //         }
            //     }
            // }else if (t.is(Tree.Kind.ENUM)){
            //     for(SyntaxTrivia syntaxTrivia: t.firstToken().trivias()){ 
            //         String comment = syntaxTrivia.comment();
            //         if(comment != null && comment.trim().startsWith("/*")){// <= get /** */
            //             if (isSimplifiedChinese(comment) == true){
            //                 addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
            //             }
            //         }
            //         else if (comment != null && comment.trim().startsWith("//")){ // <= get //
            //             if (isSimplifiedChinese(comment) == true){
            //                 addIssue(syntaxTrivia.startLine(), "Found Simplified Chinese");
            //             }
            //         }
            //     }
            }
        }
    }
    

            // else if(t.is(Tree.Kind.ANNOTATION)){
            //     AnnotationTree anT = (AnnotationTree) t;
            //     List<SyntaxTrivia> ST = anT.atToken().trivias();
            //     //for (type variableName : arrayName) => forEach
            //     for(SyntaxTrivia STta : ST){
            //         String comment = STta.comment();
            //         if(isSimplifiedChinese(comment) == true){
            //             addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //         }
            //     }
            // }
            // else if(t.is(Tree.Kind.ARRAY_DIMENSION)){
            //     ArrayDimensionTree adt = (ArrayDimensionTree) t;
            //     List<AnnotationTree> annotation = adt.annotations();
            //     for (AnnotationTree ant : annotation){
            //         SyntaxToken stk = ant.atToken();
            //         List<SyntaxTrivia> ST = stk.trivias();
            //         for(SyntaxTrivia STta : ST){
            //             String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //         }
            //     }
            // }
            // else if(t.is(Tree.Kind.ASSERT_STATEMENT)){
            //     AssertStatementTree assertST = (AssertStatementTree) t;
            //     String assertKeyWord = ((AssertStatementTree) assertST).assertKeyword().text();
            //     if(isSimplifiedChinese(assertKeyWord)==true){
            //         addIssue(assertST.assertKeyword().line(), "Found Simplified Chinese");
            //     }
            //     List<SyntaxTrivia> ST = ((AssertStatementTree) assertST).assertKeyword().trivias();
            //     for(SyntaxTrivia STta : ST){
            //         String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //     }
            // }
            // else if(t.is(Tree.Kind.RETURN_STATEMENT)){
            //     ReturnStatementTree rst = (ReturnStatementTree) t;
            //     SyntaxToken stk = rst.returnKeyword();
            //     List<SyntaxTrivia> ST = stk.trivias();
            //         for(SyntaxTrivia STta : ST){
            //             String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //         }
            // }



/*
    private boolean isChinese(char capturedChar){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(capturedChar);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS 
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        ){
            return true;
        }
        return false;
    }
*/

  
    private boolean isSimplifiedChinese(String comment){
        String regEx = "[\u2E80-\u2FD5\u3190-\u319f\u3400-\u4DBF\u4E00-\u9FCC\uF900-\uFAAD]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(comment);
        
        if (m.find()){
            return true;
        }else{
            return false;
        }

    }
}


/*
//load all languages:
List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

//build language detector:
LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
        .withProfiles(languageProfiles)
        .build();

//create a text object factory
TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

//query:
TextObject textObject = textObjectFactory.forText("my text");
Optional<LdLocale> lang = languageDetector.detect(textObject);
*/

        // ClassTree ct = (ClassTree) tree;
        // for(Tree t : ct.members()){
        //     if(t.is(Tree.Kind.TRIVIA)){
        //         SyntaxTrivia syntaxTrivia = t;
        //         visitTrivia(syntaxTrivia);;
        //     }

            // else if(t.is(Tree.Kind.ANNOTATION)){
            //     AnnotationTree anT = (AnnotationTree) t;
            //     List<SyntaxTrivia> ST = anT.atToken().trivias();
            //     //for (type variableName : arrayName) => forEach
            //     for(SyntaxTrivia STta : ST){
            //         String comment = STta.comment();
            //         if(isSimplifiedChinese(comment) == true){
            //             addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //         }
            //     }
            // }
            // else if(t.is(Tree.Kind.ARRAY_DIMENSION)){
            //     ArrayDimensionTree adt = (ArrayDimensionTree) t;
            //     List<AnnotationTree> annotation = adt.annotations();
            //     for (AnnotationTree ant : annotation){
            //         SyntaxToken stk = ant.atToken();
            //         List<SyntaxTrivia> ST = stk.trivias();
            //         for(SyntaxTrivia STta : ST){
            //             String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //         }
            //     }
            // }
            // else if(t.is(Tree.Kind.ASSERT_STATEMENT)){
            //     AssertStatementTree assertST = (AssertStatementTree) t;
            //     String assertKeyWord = ((AssertStatementTree) assertST).assertKeyword().text();
            //     if(isSimplifiedChinese(assertKeyWord)==true){
            //         addIssue(assertST.assertKeyword().line(), "Found Simplified Chinese");
            //     }
            //     List<SyntaxTrivia> ST = ((AssertStatementTree) assertST).assertKeyword().trivias();
            //     for(SyntaxTrivia STta : ST){
            //         String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //     }
            // }
            // else if(t.is(Tree.Kind.RETURN_STATEMENT)){
            //     ReturnStatementTree rst = (ReturnStatementTree) t;
            //     SyntaxToken stk = rst.returnKeyword();
            //     List<SyntaxTrivia> ST = stk.trivias();
            //         for(SyntaxTrivia STta : ST){
            //             String comment = STta.comment();
            //             if(isSimplifiedChinese(comment) == true){
            //                 addIssue(((SyntaxToken) STta).line(), "Found Simplified Chinese");
            //             }
            //         }
            // }