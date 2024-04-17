package cn.vvbbnn00.rpc.server.service;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.server.annotation.MethodPermission;
import cn.vvbbnn00.rpc.server.annotation.RpcExposed;
import cn.vvbbnn00.rpc.server.model.ServicePackage;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ClassScanner {
    private final String TARGET_PACKAGE;
    private final List<ServicePackage> servicePackages;
    private final List<MethodDeclaration> methodDeclarations = new ArrayList<>();
    private final Logger l = Logger.getLogger("ClassScanner");
    private final int SCAN_DEPTH;

    public ClassScanner(String targetPackage, List<ServicePackage> servicePackages, int scanDepth) {
        TARGET_PACKAGE = targetPackage;
        this.servicePackages = servicePackages;
        this.SCAN_DEPTH = scanDepth;
    }

    /**
     * 递归扫描指定包及其子包下的所有类
     *
     * @param packageName 包名
     * @param depth       当前递归深度
     * @return 类列表
     * @throws ClassNotFoundException 未找到类
     */
    private List<Class<?>> findClasses(String packageName, int depth) throws ClassNotFoundException {
        if (depth > SCAN_DEPTH) {
            return new ArrayList<>(); // 超过最大深度，直接返回空列表
        }

        ArrayList<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes; // 如果资源不存在，返回空列表
        }

        File directory = new File(resource.getFile());
        if (directory.exists()) {
            for (String file : Objects.requireNonNull(directory.list())) {
                File subFile = new File(directory, file);
                if (subFile.isDirectory()) {
                    // 如果是目录，递归查找
                    classes.addAll(findClasses(packageName + '.' + file, depth + 1));
                } else if (file.endsWith(".class")) {
                    // 如果是类文件，加载类
                    String className = file.substring(0, file.length() - 6);
                    classes.add(Class.forName(packageName + '.' + className));
                }
            }
        }
        return classes;
    }

    public List<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        return findClasses(packageName, 0); // 开始递归，初始深度为0
    }

    /**
     * 获取该类的别名列表
     *
     * @param className 类名
     * @return 别名列表
     */
    public synchronized List<String> getClassAliases(String className) {
        // 寻找最长匹配
        int max = 0;
        ServicePackage maxPackage = null;
        for (ServicePackage servicePackage : servicePackages) {
            String packageName = servicePackage.getPackageName();
            if (className.startsWith(packageName) && packageName.length() > max) {
                max = packageName.length();
                maxPackage = servicePackage;
            }
        }
        int finalMax = max;
        if (maxPackage == null) return null;
        if (maxPackage.getAlias() == null) {
            maxPackage.setAlias(new ArrayList<>());
        }
        List<String> result = new ArrayList<>(maxPackage.getAlias()
                .stream()
                .map(alias -> alias + className.substring(finalMax))
                .toList());
        // 注册完整包名
        if (maxPackage.isRegisterPackageName()) {
            result.add(className);
        }

        // 至少需要一个别名
        if (result.isEmpty()) {
            throw new IllegalArgumentException("No alias found for class " + className);
        }
        return result;
    }


    /**
     * 扫描指定类下的所有方法，将公开方法加入到 methodDeclarations 中
     *
     * @param clazz 类
     */
    private void scanClassMethods(Class<?> clazz) {
        List<String> aliases = getClassAliases(clazz.getName());

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            boolean expose = false;
            // 检查方法是否为 public 声明
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)) { // Public method
                expose = true;
            }
            // 检查是否有 MethodPermission 注解
            MethodPermission annotation = method.getAnnotation(MethodPermission.class);
            if (annotation != null) {
                expose = annotation.isPublic();
            }
            if (!expose) continue;

            // 获取方法参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();

            // 添加别名
            for (String alias : aliases) {
                methodDeclarations.add(new MethodDeclaration(alias, method.getName(), parameterTypes));
            }
        }
    }

    /**
     * 扫描指定包下的所有类
     */
    private void scanClasses() {
        try {
            List<Class<?>> classes = findClasses(TARGET_PACKAGE);
            for (Class<?> aClass : classes) {
                RpcExposed annotation = aClass.getAnnotation(RpcExposed.class);
                if (annotation == null) continue;

                // 检查是否有无参构造函数
                try {
                    aClass.getConstructor();
                } catch (NoSuchMethodException e) {
                    l.warning("Class " + aClass.getName() + " does not have a no-arg constructor, skipping");
                    continue;
                }

                // 扫描类下的所有方法
                scanClassMethods(aClass);
            }
        } catch (ClassNotFoundException e) {
            l.severe("Failed to scan classes: " + e.getMessage());
        }
    }

    /**
     * 获取所有方法声明
     *
     * @return 方法声明列表
     */
    public List<MethodDeclaration> getMethodDeclarations() {
        scanClasses();
        return methodDeclarations;
    }
}
