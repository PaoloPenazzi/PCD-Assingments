package it.unibo.pcd.assignment.event.collector;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.utils.Pair;
import com.github.javaparser.utils.SourceRoot;
import it.unibo.pcd.assignment.event.report.ClassReportImpl;
import it.unibo.pcd.assignment.event.report.MethodInfo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCollector {

    public static void main(String[] args) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get("src/main/java/"));
        sourceRoot.setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        try {
            parseResultList = sourceRoot.tryToParse("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var allCus = parseResultList.stream()
                                                       .filter(ParseResult::isSuccessful)
                                                       .map(r -> r.getResult().get())
                                                       .collect(Collectors.toList());

        List<PackageDeclaration> cuPack = allCus.stream().map(c -> c.getPackageDeclaration().get())
                                                        .distinct()
                                                        .collect(Collectors.toList());
        System.out.println(cuPack);


        ClassReportImpl classReport = new ClassReportImpl();
        ClassCollector classCollector = new ClassCollector();
        List<Pair<String, Boolean>> pairList = new ArrayList<>(); // (it.unibo.pcd.Event.java, true)
        // System.out.println(allCus.get(0).getType(0).getMethods().get(0).getParameterByType(String[].class).get());
        for(CompilationUnit cu : allCus){
            classCollector.visit(cu, classReport);
            Pair<String, Boolean> pair = new Pair<>(classReport.getSrcFullFileName(), classReport.getMethodsInfo().stream().anyMatch(MethodInfo::isMain));
            pairList.add(pair);
            /*var c = cu.getTypes().stream()
                    //.filter(m -> m instanceof ClassOrInterfaceDeclaration)
                    .map(m -> new Pair<TypeDeclaration, List<MethodDeclaration>>(m, m.getMethods()))
                    .filter(p -> p.b.stream().anyMatch(m -> m.isStatic()
                            && m.getNameAsString().equals("main")
                            && m.getParameterByType(String[].class).isPresent()
                            && m.getParameters().size() == 1))
                    .collect(Collectors.toList()).get(0);

            Pair<PackageDeclaration, String> pair = new Pair<>(cu.getPackageDeclaration().get(), c.a.getNameAsString());
            System.out.println(pair);*/
        }
        List<Pair<String, String>> pairs = new ArrayList<>();
        for(Pair<String, Boolean> pairone : pairList){
            for(PackageDeclaration packageDeclaration : cuPack){
                if(pairone.a.contains(packageDeclaration.getNameAsString())){
                    if(pairone.b){
                        Pair<String, String> stringStringPair = new Pair<>(packageDeclaration.getNameAsString(), pairone.a);
                        pairs.add(stringStringPair);
                    }
                }
            }
        }

        System.out.println(pairs);




        /*
        System.out.println(cuPack);
        System.out.println();
        System.out.println(allCus.get(0).getAllComments());
        System.out.println();
        System.out.println(allCus.get(0).getImports());
        System.out.println();
        System.out.println(allCus.get(0).getMetaModel());
        System.out.println();
        System.out.println(allCus.get(0).getModule());
        System.out.println();
        System.out.println(allCus.get(0).getPrimaryType());
        System.out.println();
        System.out.println(allCus.get(0).getPrimaryTypeName().get());
        System.out.println();
        System.out.println(allCus.get(0).getStorage().get());
        System.out.println();
        System.out.println(allCus.get(0).getTypes());
        System.out.println();
        System.out.println(allCus.get(0).findRootNode());
        //ProjectRoot projectRoot = new ParserCollectionStrategy().collect(Paths.get("src/main/java/"));
        //System.out.println(projectRoot);*/
    }
}
