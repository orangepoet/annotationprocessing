## Lombok原理分析

(暂时不做源码 不够时间吃透)

### 什么是Lombok？

一. 定义
Lombok项目是一个Java库，它会自动插入编辑器和构建工具中，Lombok提供了一组有用的注释，用来消除Java类中的大量样板代码。仅五个字符(
@Data)就可以替换数百行代码从而产生干净，简洁且易于维护的Java类。

可以在编译期间生成getter，setter，构造器，builder等代码，

二. 与运行时注解的区别
区别于运行时
但是，我们发现这个包跟一般的包有很大区别，绝大多数java包都工作在运行时，比如spring提供的那种注解，通过在运行时用反射来实现业务逻辑。Lombok这个东西工作却在编译期，在运行时是无法通过反射获取到这个注解的。

区别于前面的例子:
前面的是生成新的java文件
lombok大量的都是修改已存在的Java文件, 做功能增强

如果要使用Lombok的话还需要配合安装相应的插件，防止IDE的自动检查报错。

核心是修改了AST, [抽象语法树]

三. 执行流程

Lombok的基本流程应该基本是这样：

* 定义编译期的注解
* 利用JSR269 api(Pluggable Annotation Processing API )创建编译期的注解处理器
* 利用tools.jar的javac api处理AST(抽象语法树)
* 将功能注册进jar包

@Override
public synchronized void init(ProcessingEnvironment processingEnv) {
super.init(processingEnv);
// 打log用的
this.messager = processingEnv.getMessager();
// 待处理的抽象语法树
this.trees = JavacTrees.instance(processingEnv);
// 上下文
Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
// 封装了创建AST节点的一些方法
this.treeMaker = TreeMaker.instance(context);
// 提供了创建标识符的方法
this.names = Names.instance(context);
}

@Override
public synchronized boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Getter.class);
set.forEach(element -> {
// AST 语法树
JCTree jcTree = trees.getTree(element);
jcTree.accept(new TreeTranslator() {
@Override
public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

                for (JCTree tree : jcClassDecl.defs) {
                    if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                        JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                        jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                    }
                }

                jcVariableDeclList.forEach(jcVariableDecl -> {
                    messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                    jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl));
                });
                super.visitClassDef(jcClassDecl);
            }

        });
    });

    return true;

}

### 源码详解

步骤大概是下面这样：

利用roundEnv的getElementsAnnotatedWith方法过滤出被Getter这个注解标记的类，并存入set
遍历这个set里的每一个元素，并生成jCTree这个语法树
创建一个TreeTranslator，并重写其中的visitClassDef方法，这个方法处理遍历语法树得到的类定义部分jcClassDecl
创建一个jcVariableDeclList保存类的成员变量
遍历jcTree的所有成员(包括成员变量和成员函数和构造函数)，过滤出其中的成员变量，并添加进jcVariableDeclList
将jcVariableDeclList的所有变量转换成需要添加的getter方法，并添加进jcClassDecl的成员中
调用默认的遍历方法遍历处理后的jcClassDecl
利用上面的TreeTranslator去处理jcTree

private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {
ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")),
jcVariableDecl.getName())));
JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()),
jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
}

private Name getNewMethodName(Name name) {
String s = name.toString();
return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
}

注解处理器的编译方式:

1) 使用javac需要指定 processor
2) MAVEN构建并打包, 通过spi的方式, 在meta-inf下放置javax.annotation.processing.Processor

问题:

1. idea的插件是为什么 ?
